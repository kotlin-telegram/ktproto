package ktproto.session.encrypted

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import ktproto.io.annotation.OngoingConnection
import ktproto.session.MTProtoSession
import ktproto.transport.MTProtoTransport

@OngoingConnection
public class MTProtoEncryptedSession(
    private val transport: MTProtoTransport,
    private val scope: CoroutineScope
) : MTProtoSession {
    private val _incoming = Channel<MTProtoSession.Message>()
    override val incoming: ReceiveChannel<MTProtoSession.Message> = _incoming

    private val _outgoing = Channel<MTProtoSession.Message>()
    override val outgoing: SendChannel<MTProtoSession.Message> = _outgoing

    init {
        init()
    }

    private fun init() {
        transport.incoming.receiveAsFlow()
            .map { message -> message.decode() }
            .onEach { message -> _incoming.send(message) }
            .launchIn(scope)

        _outgoing.receiveAsFlow()
            .map { message ->
                // message.encode()
                TODO()
            }
            .onEach { message -> transport.outgoing.send(message) }
            .launchIn(scope)
    }
}

@OptIn(OngoingConnection::class)
private fun MTProtoTransport.Message.decode(): MTProtoSession.Message {
    TODO()
}
