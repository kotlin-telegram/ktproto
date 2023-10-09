package ktproto.stdlib.int

import java.math.BigInteger

public actual class BigInt(
    private val underlying: BigInteger
) : Comparable<BigInt> {
    public actual operator fun plus(other: BigInt): BigInt {
        return (underlying + other.underlying).bi
    }

    public actual operator fun minus(other: BigInt): BigInt {
        return (underlying - other.underlying).bi
    }

    public actual operator fun times(other: BigInt): BigInt {
        return (underlying * other.underlying).bi
    }

    public actual operator fun div(other: BigInt): BigInt {
        return (underlying / other.underlying).bi
    }

    public actual operator fun rem(other: BigInt): BigInt {
        return (underlying % other.underlying).bi
    }

    public actual operator fun unaryPlus(): BigInt {
        return this
    }

    public actual operator fun unaryMinus(): BigInt {
        return (-underlying).bi
    }

    public actual operator fun inc(): BigInt {
        return this + 1.bi
    }

    public actual operator fun dec(): BigInt {
        return this - 1.bi
    }

    public actual override fun compareTo(other: BigInt): Int {
        return underlying.compareTo(other.underlying)
    }

    public actual infix fun pow(other: BigInt): BigInt {
        return underlying.pow(other.underlying.toInt()).bi
    }

    public actual val bytesSize: Int get() = underlying.toByteArray().size

    public actual fun toByteArrayBE(): ByteArray =
        underlying.toByteArray()

    public actual fun toByteArrayLE(): ByteArray =
        underlying.toByteArray().apply { reverse() }

    public actual companion object {
        public actual fun ofLE(bytes: ByteArray): BigInt =
            BigInteger(bytes.reversedArray()).bi
        public actual fun ofBE(bytes: ByteArray): BigInt =
            BigInteger(bytes).bi
    }
}

private val BigInteger.bi: BigInt get() = BigInt(underlying = this)
