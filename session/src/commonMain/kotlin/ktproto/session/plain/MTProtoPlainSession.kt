package ktproto.session.plain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.*
import ktproto.io.annotation.OngoingConnection
import ktproto.io.memory.*
import ktproto.session.MTProtoSession
import ktproto.session.MessageIdProvider
import ktproto.session.plain.MTProtoPlainEnvelope.DataLength
import ktproto.transport.MTProtoTransport

@OngoingConnection
public fun mtprotoPlainSession(
    transport: MTProtoTransport,
    messageIdProvider: MessageIdProvider,
    scope: CoroutineScope
): MTProtoSession {
    val session = MTProtoPlainSession(transport, messageIdProvider)
    session.launchIn(scope)
    return session
}

@OngoingConnection
private class MTProtoPlainSession(
    private val transport: MTProtoTransport,
    private val messageIdProvider: MessageIdProvider
) : MTProtoSession {
    private val _incoming = Channel<MTProtoSession.Message>()
    override val incoming: ReceiveChannel<MTProtoSession.Message> = _incoming

    private val _outgoing = Channel<MTProtoSession.Message>()
    override val outgoing: SendChannel<MTProtoSession.Message> = _outgoing

    fun launchIn(scope: CoroutineScope) {
        transport.incoming.receiveAsFlow()
            .onCompletion { cause -> close(cause) }
            .map { message -> message.decode() }
            .onEach(_incoming::send)
            .launchIn(scope)

        _outgoing.receiveAsFlow()
            .onCompletion { cause -> close(cause) }
            .map { message -> message.encode(messageIdProvider) }
            .onEach(transport.outgoing::send)
            .launchIn(scope)
    }

    private fun close(cause: Throwable?) {
        _incoming.close(cause)
        _outgoing.close(cause)
    }
}

@OptIn(OngoingConnection::class)
private fun MTProtoSession.Message.encode(
    messageId: MessageIdProvider
): MTProtoTransport.Message {
    val memory = MemoryArena.allocate(n = DataLength.SIZE_BYTES + bytes.size)
    memory.write(bytes.size).write(bytes)
    val dataLengthMemory = memory.take(DataLength.SIZE_BYTES)
    val envelope = MTProtoPlainEnvelope(
        messageId = messageId.nextMessageId(),
        dataLength = DataLength(dataLengthMemory),
        data = memory.drop(DataLength.SIZE_BYTES)
    )
    return MTProtoTransport.Message(envelope.toByteArray())
}

@OptIn(OngoingConnection::class)
private fun MTProtoTransport.Message.decode(): MTProtoSession.Message {
    val memory = MemoryArena.of(bytes)
    val envelope = MTProtoPlainEnvelope.of(memory)
    return MTProtoSession.Message(
        bytes = envelope.data.toByteArray()
    )
}
