package ktproto.transport

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.*
import ktproto.io.annotation.OngoingConnection
import ktproto.io.input.Input
import ktproto.io.memory.*
import ktproto.io.output.Output
import ktproto.stdlib.bytes.toBinaryString

@OptIn(OngoingConnection::class)
public fun mtprotoIntermediateConnector(
    transport: Transport.Connector
): MTProtoTransport.Connector = MTProtoTransport.Connector { scope ->
    val result = MTProtoIntermediate(
        transport = transport.connect(scope)
    )
    result.launchIn(scope)
    result
}


@OngoingConnection
private class MTProtoIntermediate(
    private val transport: Transport
) : MTProtoTransport {
    private val _incoming = Channel<MTProtoTransport.Message>()
    override val incoming: ReceiveChannel<MTProtoTransport.Message> = _incoming

    private val _outgoing = Channel<MTProtoTransport.Message>()
    override val outgoing: SendChannel<MTProtoTransport.Message> = _outgoing

    suspend fun launchIn(scope: CoroutineScope) {
        transport.output.write(INIT)

        transport.input
            .asMessagesFlow()
            .onCompletion { cause -> close(cause) }
            .onEach { message -> _incoming.send(message) }
            .launchIn(scope)

        _outgoing.receiveAsFlow()
            .attachOutput(transport.output)
            .onCompletion { cause -> close(cause) }
            .launchIn(scope)
    }

    private fun close(cause: Throwable?) {
        _incoming.close(cause)
        _outgoing.close(cause)
    }

    private companion object {
        private val INIT = MemoryArena.of(int = 0xeeeeeeee_u)
    }
}

@OptIn(OngoingConnection::class)
private fun Input.asMessagesFlow() = flow {
    var memory = MemoryArena.allocate(n = 32)

    while (true) {
        read(memory.take(Int.SIZE_BYTES))
        val length = memory.scanInt()
        memory = memory.ensureCapacity(length)
        val memoryView = memory.take(length)
        read(memoryView)
        val bytes = memoryView.toByteArray()
        bytes.throwTransportExceptions()
        emit(MTProtoTransport.Message(bytes))
    }
}

@OptIn(OngoingConnection::class)
private fun Flow<MTProtoTransport.Message>.attachOutput(output: Output) = flow<Nothing> {
    var memory = MemoryArena.allocate(n = 32)

    this@attachOutput.collect { message ->
        val messageLength = Int.SIZE_BYTES + message.bytes.size
        memory = memory.ensureCapacity(messageLength)

        memory
            .write(message.bytes.size)
            .write(message.bytes)

        output.write(memory.take(messageLength))
    }
}
