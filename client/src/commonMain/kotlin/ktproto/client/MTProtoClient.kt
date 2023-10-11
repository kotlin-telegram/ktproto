package ktproto.client

import kotl.core.descriptor.TLExpressionDescriptor
import kotl.core.element.TLExpression
import kotl.core.element.TLFunction
import kotlinx.coroutines.flow.Flow

public interface MTProtoClient {
    public val updates: Flow<TLExpression>

    public suspend fun execute(
        function: TLFunction,
        responseDescriptor: TLExpressionDescriptor
    ): TLExpression
}
