package ktproto.io.annotation

@RequiresOptIn(
    level = RequiresOptIn.Level.WARNING,
    message = "This declaration has ongoing IO state, consider to handle all IO exceptions and do not forget to close connection"
)
public annotation class OngoingConnection
