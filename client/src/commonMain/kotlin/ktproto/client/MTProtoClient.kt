package ktproto.client

import kotl.core.element.TLExpression
import kotlinx.coroutines.flow.Flow

public interface MTProtoClient {
    public val updates: Flow<TLExpression>
    public suspend fun execute(request: MTProtoRequest): TLExpression
}
