package ktproto.transport

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import ktproto.io.annotation.OngoingConnection
import ktproto.io.input.Input
import ktproto.io.memory.*
import ktproto.io.output.Output
import kotlin.jvm.JvmInline

@OptIn(OngoingConnection::class)
public fun mtprotoIntermediate(
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
    override val incoming = Channel<MTProtoTransport.Message>()
    override val outgoing = Channel<MTProtoTransport.Message>()

    suspend fun launchIn(scope: CoroutineScope) {
        transport.output.write(INIT)

        transport.input
            .asMessagesFlow()
            .onCompletion { cause -> close(cause) }
            .onEach { message -> incoming.send(message) }
            .launchIn(scope)

        outgoing.consumeAsFlow()
            .attachOutput(transport.output)
            .onCompletion { cause -> close(cause) }
            .launchIn(scope)
    }

    private fun close(cause: Throwable?) {
        incoming.close(cause)
        outgoing.close(cause)
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
