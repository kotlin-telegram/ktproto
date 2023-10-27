package ktproto.time

public expect interface Clock {
    public fun currentTimeMillis(): Long

    public object System : Clock
}
