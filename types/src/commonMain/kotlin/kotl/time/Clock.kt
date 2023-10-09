package kotl.time

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_EXPERIMENTAL_WARNING")
public expect interface Clock {
    public fun currentTimeMillis(): Long

    public object System : Clock
}
