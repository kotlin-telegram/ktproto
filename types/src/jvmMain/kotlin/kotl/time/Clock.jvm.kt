package kotl.time

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_EXPERIMENTAL_WARNING")
public actual interface Clock {
    public actual fun currentTimeMillis(): Long

    public actual object System : Clock {
        override fun currentTimeMillis(): Long =
            java.lang.System.currentTimeMillis()
    }
}
