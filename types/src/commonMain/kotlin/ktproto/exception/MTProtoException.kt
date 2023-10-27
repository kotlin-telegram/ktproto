package ktproto.exception

public open class MTProtoException(
    message: String? = null,
    cause: Throwable? = null
) : RuntimeException(message, cause)
