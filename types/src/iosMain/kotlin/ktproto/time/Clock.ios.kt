package ktproto.time

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

public actual interface Clock {
    public actual fun currentTimeMillis(): Long

    public actual object System : Clock {
        override fun currentTimeMillis(): Long =
            (NSDate().timeIntervalSince1970 * 1_000).toLong()
    }
}
