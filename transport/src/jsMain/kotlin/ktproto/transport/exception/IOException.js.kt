package ktproto.transport.exception

public actual class IOException : Exception {
    public actual constructor(message: String) : super(message)
    public actual constructor(cause: Throwable) : super(cause)
    public actual constructor(message: String, cause: Throwable) : super(message, cause)
}
