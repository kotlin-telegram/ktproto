package ktproto.transport

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import ktproto.io.annotation.OngoingConnection
import kotlin.jvm.JvmInline

@OngoingConnection
public interface MTProtoTransport {
    public val incoming: ReceiveChannel<Message>
    public val outgoing: SendChannel<Message>

    @JvmInline
    public value class Message(public val bytes: ByteArray)

    public fun interface Connector {
        public suspend fun connect(scope: CoroutineScope): MTProtoTransport
    }
}
