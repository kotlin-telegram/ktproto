package ktproto.client

import kotl.core.descriptor.TLExpressionDescriptor
import kotl.core.element.TLFunction

public data class MTProtoRequest(
    public val function: TLFunction,
    public val responseDescriptor: TLExpressionDescriptor
)
