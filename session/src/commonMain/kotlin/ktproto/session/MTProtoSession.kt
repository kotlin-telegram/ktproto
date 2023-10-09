package ktproto.session

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import ktproto.io.annotation.OngoingConnection
import kotlin.jvm.JvmInline

@OngoingConnection
public interface MTProtoSession {
    public val incoming: ReceiveChannel<Message>
    public val outgoing: SendChannel<Message>

    @JvmInline
    public value class Message(
        public val bytes: ByteArray
    )
}
