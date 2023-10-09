package ktproto.transport.exception

import kotlinx.coroutines.CancellationException

public expect class IOException : Exception {
    public constructor(message: String)
    public constructor(cause: Throwable)
    public constructor(message: String, cause: Throwable)
}

public fun Throwable.throwIO(): Nothing {
    if (this is CancellationException) throw this
    throw IOException(this)
}
