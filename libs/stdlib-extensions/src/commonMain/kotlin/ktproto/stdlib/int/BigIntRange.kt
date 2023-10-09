package ktproto.stdlib.int

public class BigIntRange(
    override val start: BigInt,
    override val endInclusive: BigInt
) : ClosedRange<BigInt>, OpenEndRange<BigInt>, Iterable<BigInt> {
    override val endExclusive: BigInt get() = endInclusive + 1.bi

    override fun isEmpty(): Boolean = start > endInclusive

    override fun iterator(): Iterator<BigInt> = iterator {
        var start = start
        while (start <= endInclusive) {
            yield(start)
            start++
        }
    }

    @Suppress("ConvertTwoComparisonsToRangeCheck")
    override fun contains(value: BigInt): Boolean =
        start <= value && value <= endInclusive
}

public operator fun BigInt.rangeTo(other: BigInt): BigIntRange =
    BigIntRange(start = this, endInclusive = other)

public operator fun BigInt.rangeUntil(other: BigInt): BigIntRange =
    BigIntRange(start = this, endInclusive = other - 1.bi)
