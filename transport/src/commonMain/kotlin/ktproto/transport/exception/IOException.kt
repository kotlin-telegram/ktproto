package ktproto.transport.exception

import kotlinx.coroutines.CancellationException
import ktproto.exception.MTProtoException

public open class IOException(
    message: String? = null,
    cause: Throwable? = null
) : MTProtoException(message, cause)

public fun Throwable.throwIO(): Nothing {
    if (this is CancellationException) throw this
    throw IOException(cause = this)
}
