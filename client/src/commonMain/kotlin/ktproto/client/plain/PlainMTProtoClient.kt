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
import ktproto.session.MTProtoSafeSession
import ktproto.session.messageIdProvider
import ktproto.session.plain.mtprotoPlainSession
import ktproto.transport.MTProtoTransport
import ktproto.transport.Transport
import ktproto.transport.mtprotoIntermediate

@OptIn(OngoingConnection::class)
public suspend fun plainMTProtoClient(
    scope: CoroutineScope,
    clock: Clock = Clock.System,
    transport: Transport.Connector,
): MTProtoClient {
    val mtprotoTransport = mtprotoIntermediate(transport)
    return plainMTProtoClient(scope, clock, mtprotoTransport)
}

@OptIn(OngoingConnection::class)
public suspend fun plainMTProtoClient(
    scope: CoroutineScope,
    clock: Clock = Clock.System,
    transport: MTProtoTransport.Connector
): MTProtoClient {
    val client = PlainMTProtoClient(transport, scope, clock)
    client.connect()
    return client
}

@OptIn(OngoingConnection::class)
private class PlainMTProtoClient(
    transport: MTProtoTransport.Connector,
    scope: CoroutineScope,
    clock: Clock = Clock.System
) : MTProtoClient {
    private val session: MTProtoSafeSession

    init {
        val connector = MTProtoSession.Connector { connectorScope ->
            mtprotoPlainSession(
                scope = connectorScope,
                transport = transport.connect(connectorScope),
                messageIdProvider = messageIdProvider(clock)
            )
        }
        session = MTProtoSafeSession(connector, scope)
    }

    suspend fun connect() = session.connect()

    override val updates: Flow<TLExpression> = flow { awaitCancellation() }

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
