package ktproto.transport.exception

public class TransportException(
    public val code: Int,
    cause: Throwable? = null
) : IOException(
    message = "Transport was closed with code $code",
    cause = cause
)
