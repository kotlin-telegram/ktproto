package kotl.time

import kotlin.js.Date

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_EXPERIMENTAL_WARNING")
public actual interface Clock {
    public actual fun currentTimeMillis(): Long

    public actual object System : Clock {
        override fun currentTimeMillis(): Long = Date.now().toLong()
    }
}