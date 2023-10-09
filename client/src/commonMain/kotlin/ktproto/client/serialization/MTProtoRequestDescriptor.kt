package ktproto.client.serialization

import kotlin.reflect.KType

public data class MTProtoRequestDescriptor<T, R>(
    public val functionType: KType,
    public val returnType: KType
)
