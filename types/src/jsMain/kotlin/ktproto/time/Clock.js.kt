package ktproto.time

import kotlin.js.Date

public actual interface Clock {
    public actual fun currentTimeMillis(): Long

    public actual object System : Clock {
        override fun currentTimeMillis(): Long = Date.now().toLong()
    }
}
