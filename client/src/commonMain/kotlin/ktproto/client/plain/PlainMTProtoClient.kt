package ktproto.client.plain

import kotl.core.decoder.decodeFromByteArray
import kotl.core.descriptor.TLExpressionDescriptor
import kotl.core.element.TLExpression
import kotl.core.element.TLFunction
import kotl.core.encoder.encodeToByteArray
import ktproto.time.Clock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ktproto.client.MTProtoClient
import ktproto.io.annotation.OngoingConnection
import ktproto.session.MTProtoSession
import ktproto.session.SessionConnector
import ktproto.session.messageIdProvider
import ktproto.session.plain.mtprotoPlainSession
import ktproto.transport.MTProtoTransport
import ktproto.transport.Transport
import ktproto.transport.mtprotoIntermediate

@OngoingConnection
public suspend fun plainMTProtoClient(
    transport: Transport,
    scope: CoroutineScope,
    clock: Clock = Clock.System
): MTProtoClient {
    val mtprotoTransport = mtprotoIntermediate(transport, scope)
    return plainMTProtoClient(mtprotoTransport, scope, clock)
}

@OngoingConnection
public fun plainMTProtoClient(
    transport: MTProtoTransport,
    scope: CoroutineScope,
    clock: Clock = Clock.System
): MTProtoClient {
    val client = PlainMTProtoClient(transport, scope, clock)
    return client
}

@OptIn(OngoingConnection::class)
private class PlainMTProtoClient(
    transport: MTProtoTransport,
    scope: CoroutineScope,
    clock: Clock = Clock.System
) : MTProtoClient {
    private val session: SessionConnector

    init {
        val factory = SessionConnector.Factory {
            mtprotoPlainSession(
                scope = scope,
                transport = transport,
                messageIdProvider = messageIdProvider(clock)
            )
        }
        session = SessionConnector(factory, scope)
    }

    override val updates: Flow<TLExpression> = flow { awaitCancellation() }

    @OptIn(ExperimentalStdlibApi::class)
    override suspend fun execute(
        function: TLFunction,
        responseDescriptor: TLExpressionDescriptor
    ): TLExpression {
        val bytes = function.encodeToByteArray()
        val message = MTProtoSession.Message(bytes)
        val response = session.sendRequest(message) { it }
        return responseDescriptor.decodeFromByteArray(response.bytes)
    }
}
