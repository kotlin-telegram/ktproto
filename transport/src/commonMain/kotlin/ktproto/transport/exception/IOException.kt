package ktproto.transport.exception

import kotlinx.coroutines.CancellationException
import ktproto.exception.MTProtoException

public open class IOException(
    message: String? = null,
    cause: Throwable? = null
) : MTProtoException(message, cause)

public fun Throwable.throwIO(): Nothing {
    if (this is CancellationException) throw this
    if (this is TransportException) throw TransportException(code, this)
    throw IOException(message, cause = this)
}
