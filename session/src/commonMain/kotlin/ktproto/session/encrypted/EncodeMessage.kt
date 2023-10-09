package ktproto.session.encrypted

import ktproto.io.annotation.OngoingConnection
import ktproto.session.AuthKeyId
import ktproto.session.MTProtoSession
import ktproto.transport.MTProtoTransport

@OptIn(OngoingConnection::class)
internal fun MTProtoSession.Message.encode(
    salt: Salt,
    authKeyId: AuthKeyId,
    sessionId: SessionId
): MTProtoTransport.Message {
    TODO()
}
