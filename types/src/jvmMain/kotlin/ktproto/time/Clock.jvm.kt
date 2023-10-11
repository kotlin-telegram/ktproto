package ktproto.time

public actual interface Clock {
    public actual fun currentTimeMillis(): Long

    public actual object System : Clock {
        override fun currentTimeMillis(): Long =
            java.lang.System.currentTimeMillis()
    }
}
