@file:OptIn(ExperimentalUnsignedTypes::class)

package ktproto.stdlib.bigint

import ktproto.stdlib.bigint.BigInt.Companion.BASE
import ktproto.stdlib.bigint.BigInt.Companion.BASE_SIZE
import ktproto.stdlib.bigint.BigInt.Companion.ZERO
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

/**
 * Kotlin Multiplatform implementation of Big Integer numbers (KBigInteger).
 *
 * @source https://github.com/SciProgCentre/kmath/blob/5129f29084ed810871a93d0d816205e0f638fa71/kmath-core/src/commonMain/kotlin/space/kscience/kmath/operations/CommonBigInt.kt#L4
 * @author Robert Drynkin
 * @author Peter Klimai
 */
public class BigInt internal constructor(
    private val sign: Byte,
    private val underlying: UIntArray,
) : Comparable<BigInt> {
    override fun compareTo(other: BigInt): Int = when {
        (sign == 0.toByte()) and (other.sign == 0.toByte()) -> 0
        sign < other.sign -> -1
        sign > other.sign -> 1
        else -> sign * compareMagnitudes(underlying, other.underlying)
    }

    override fun equals(other: Any?): Boolean = other is BigInt && compareTo(other) == 0

    override fun hashCode(): Int = underlying.hashCode() + sign

    public fun abs(): BigInt = if (sign == 0.toByte()) this else BigInt(1, underlying)

    public operator fun unaryMinus(): BigInt =
        if (this.sign == 0.toByte()) this else BigInt((-sign).toByte(), underlying)

    public operator fun plus(b: BigInt): BigInt = when {
        b.sign == 0.toByte() -> this
        sign == 0.toByte() -> b
        this == -b -> ZERO
        sign == b.sign -> BigInt(sign, addMagnitudes(underlying, b.underlying))

        else -> {
            val comp = compareMagnitudes(underlying, b.underlying)

            if (comp == 1)
                BigInt(sign, subtractMagnitudes(underlying, b.underlying))
            else
                BigInt((-sign).toByte(), subtractMagnitudes(b.underlying, underlying))
        }
    }

    public operator fun inc(): BigInt = plus(ONE)

    public operator fun minus(b: BigInt): BigInt = this + (-b)

    public operator fun dec(): BigInt = minus(ONE)

    public operator fun times(b: BigInt): BigInt = when {
        this.sign == 0.toByte() -> ZERO
        b.sign == 0.toByte() -> ZERO
        b.underlying.size == 1 -> this * b.underlying[0] * b.sign.toInt()
        this.underlying.size == 1 -> b * this.underlying[0] * this.sign.toInt()
        else -> BigInt((sign * b.sign).toByte(), multiplyMagnitudes(underlying, b.underlying))
    }

    public operator fun times(other: UInt): BigInt = when {
        sign == 0.toByte() -> ZERO
        other == 0U -> ZERO
        other == 1U -> this
        else -> BigInt(sign, multiplyMagnitudeByUInt(underlying, other))
    }

    public fun pow(exponent: BigInt): BigInt = when {
        this == ZERO && exponent == ZERO -> throw ArithmeticException("Cannot pow 0 ^ 0")
        exponent == ZERO -> ONE
        this == ZERO -> ZERO
        this == ONE -> ONE
        exponent < ZERO -> ONE / pow(-exponent)
        else -> this * pow(exponent = exponent - ONE)
    }

    public operator fun times(other: Int): BigInt = when {
        other > 0 -> this * kotlin.math.abs(other).toUInt()
        other != Int.MIN_VALUE -> -this * kotlin.math.abs(other).toUInt()
        else -> times(other.toBigInt())
    }

    public operator fun div(other: UInt): BigInt =
        BigInt(sign, divideMagnitudeByUInt(underlying, other))

    public operator fun div(other: Int): BigInt = BigInt(
        (sign * other.sign).toByte(),
        divideMagnitudeByUInt(underlying, kotlin.math.abs(other).toUInt())
    )

    private fun division(other: BigInt): Pair<BigInt, BigInt> {
        // Long division algorithm:
        //     https://en.wikipedia.org/wiki/Division_algorithm#Integer_division_(unsigned)_with_remainder
        // TODO: Implement more effective algorithm
        var q = ZERO
        var r = ZERO

        val bitSize =
            (BASE_SIZE * (this.underlying.size - 1) + log2(this.underlying.lastOrNull()?.toFloat() ?: (0f + 1))).toInt()

        for (i in bitSize downTo 0) {
            r = r shl 1
            r = r or ((abs(this) shr i) and ONE)

            if (r >= abs(other)) {
                r -= abs(other)
                q += (ONE shl i)
            }
        }

        return Pair(BigInt((sign * other.sign).toByte(), q.underlying), r)
    }

    public operator fun div(other: BigInt): BigInt = division(other).first

    public infix fun shl(i: Int): BigInt {
        if (this == ZERO) return ZERO
        if (i == 0) return this
        val fullShifts = i / BASE_SIZE + 1
        val relShift = i % BASE_SIZE
        val shiftLeft = { x: UInt -> if (relShift >= 32) 0U else x shl relShift }
        val shiftRight = { x: UInt -> if (BASE_SIZE - relShift >= 32) 0U else x shr (BASE_SIZE - relShift) }
        val newMagnitude = UIntArray(underlying.size + fullShifts)

        for (j in underlying.indices) {
            newMagnitude[j + fullShifts - 1] = shiftLeft(this.underlying[j])

            if (j != 0)
                newMagnitude[j + fullShifts - 1] = newMagnitude[j + fullShifts - 1] or shiftRight(this.underlying[j - 1])
        }

        newMagnitude[underlying.size + fullShifts - 1] = shiftRight(underlying.last())
        return BigInt(sign, stripLeadingZeros(newMagnitude))
    }

    public infix fun shr(i: Int): BigInt {
        if (this == ZERO) return ZERO
        if (i == 0) return this
        val fullShifts = i / BASE_SIZE
        val relShift = i % BASE_SIZE
        val shiftRight = { x: UInt -> if (relShift >= 32) 0U else x shr relShift }
        val shiftLeft = { x: UInt -> if (BASE_SIZE - relShift >= 32) 0U else x shl (BASE_SIZE - relShift) }
        if (this.underlying.size - fullShifts <= 0) return ZERO
        val newMagnitude = UIntArray(underlying.size - fullShifts)

        for (j in fullShifts..<underlying.size) {
            newMagnitude[j - fullShifts] = shiftRight(underlying[j])

            if (j != underlying.size - 1)
                newMagnitude[j - fullShifts] = newMagnitude[j - fullShifts] or shiftLeft(underlying[j + 1])
        }

        return BigInt(sign, stripLeadingZeros(newMagnitude))
    }

    public infix fun or(other: BigInt): BigInt {
        if (this == ZERO) return other
        if (other == ZERO) return this
        val resSize = max(underlying.size, other.underlying.size)
        val newMagnitude: UIntArray = UIntArray(resSize)

        for (i in 0..<resSize) {
            if (i < underlying.size) newMagnitude[i] = newMagnitude[i] or underlying[i]
            if (i < other.underlying.size) newMagnitude[i] = newMagnitude[i] or other.underlying[i]
        }

        return BigInt(1, stripLeadingZeros(newMagnitude))
    }

    public infix fun and(other: BigInt): BigInt {
        if ((this == ZERO) or (other == ZERO)) return ZERO
        val resSize = min(this.underlying.size, other.underlying.size)
        val newMagnitude = UIntArray(resSize)
        for (i in 0..<resSize) newMagnitude[i] = this.underlying[i] and other.underlying[i]
        return BigInt(1, stripLeadingZeros(newMagnitude))
    }

    public operator fun rem(other: Int): Int {
        val res = this - (this / other) * other
        return if (res == ZERO) 0 else res.sign * res.underlying[0].toInt()
    }

    public operator fun rem(other: BigInt): BigInt = this - (this / other) * other

    public fun modPow(exponent: BigInt, modulus: BigInt): BigInt = when {
        exponent == ZERO -> ONE
        exponent % 2 == 1 -> (this * modPow(exponent - ONE, modulus)) % modulus

        else -> {
            val sqRoot = modPow(exponent / 2, modulus)
            (sqRoot * sqRoot) % modulus
        }
    }

    public fun toUByteArray(): UByteArray {
        val absolute = underlying.reversedArray().toUByteArray()
        val signed = absolute.twosComplementSign(sign)
        return signed.stripLeadingSignBytes(sign)
    }

    public fun toByteArray(): ByteArray = toUByteArray().toByteArray()

    override fun toString(): String {
        if (this.sign == 0.toByte()) {
            return "0x0"
        }
        var res: String = if (this.sign == (-1).toByte()) "-0x" else "0x"
        var numberStarted = false

        for (i in this.underlying.size - 1 downTo 0) {
            for (j in BASE_SIZE / 4 - 1 downTo 0) {
                val curByte = (this.underlying[i] shr 4 * j) and 0xfU
                if (numberStarted or (curByte != 0U)) {
                    numberStarted = true
                    res += hexMapping[curByte]
                }
            }
        }

        return res
    }

    public companion object {
        public const val BASE: ULong = 0xffffffffUL
        public const val BASE_SIZE: Int = 32
        public val ZERO: BigInt = BigInt(0, uintArrayOf())
        public val ONE: BigInt = BigInt(1, uintArrayOf(1u))
        private const val KARATSUBA_THRESHOLD = 80

        private val hexMapping: HashMap<UInt, String> = hashMapOf(
            0U to "0", 1U to "1", 2U to "2", 3U to "3",
            4U to "4", 5U to "5", 6U to "6", 7U to "7",
            8U to "8", 9U to "9", 10U to "a", 11U to "b",
            12U to "c", 13U to "d", 14U to "e", 15U to "f"
        )

        private fun compareMagnitudes(mag1: UIntArray, mag2: UIntArray): Int {
            return when {
                mag1.size > mag2.size -> 1
                mag1.size < mag2.size -> -1

                else -> {
                    for (i in mag1.size - 1 downTo 0) return when {
                        mag1[i] > mag2[i] -> 1
                        mag1[i] < mag2[i] -> -1
                        else -> continue
                    }

                    0
                }
            }
        }

        private fun addMagnitudes(mag1: UIntArray, mag2: UIntArray): UIntArray {
            val resultLength = max(mag1.size, mag2.size) + 1
            val result = UIntArray(resultLength)
            var carry = 0uL

            for (i in 0..<resultLength - 1) {
                val res = when {
                    i >= mag1.size -> mag2[i].toULong() + carry
                    i >= mag2.size -> mag1[i].toULong() + carry
                    else -> mag1[i].toULong() + mag2[i].toULong() + carry
                }

                result[i] = (res and BASE).toUInt()
                carry = res shr BASE_SIZE
            }

            result[resultLength - 1] = carry.toUInt()
            return stripLeadingZeros(result)
        }

        private fun subtractMagnitudes(mag1: UIntArray, mag2: UIntArray): UIntArray {
            val resultLength = mag1.size
            val result = UIntArray(resultLength)
            var carry = 0L

            for (i in 0..<resultLength) {
                var res =
                    if (i < mag2.size) mag1[i].toLong() - mag2[i].toLong() - carry
                    else mag1[i].toLong() - carry

                carry = if (res < 0) 1 else 0
                res += carry * (BASE + 1UL).toLong()

                result[i] = res.toUInt()
            }

            return stripLeadingZeros(result)
        }

        private fun multiplyMagnitudeByUInt(mag: UIntArray, x: UInt): UIntArray {
            val resultLength = mag.size + 1
            val result = UIntArray(resultLength)
            var carry = 0uL

            for (i in mag.indices) {
                val cur = carry + mag[i].toULong() * x.toULong()
                result[i] = (cur and BASE).toUInt()
                carry = cur shr BASE_SIZE
            }

            result[resultLength - 1] = (carry and BASE).toUInt()

            return stripLeadingZeros(result)
        }

        internal fun multiplyMagnitudes(mag1: UIntArray, mag2: UIntArray): UIntArray = when {
            mag1.size + mag2.size < KARATSUBA_THRESHOLD || mag1.isEmpty() || mag2.isEmpty() ->
                naiveMultiplyMagnitudes(mag1, mag2)
            // TODO implement Fourier
            else -> karatsubaMultiplyMagnitudes(mag1, mag2)
        }

        internal fun naiveMultiplyMagnitudes(mag1: UIntArray, mag2: UIntArray): UIntArray {
            val resultLength = mag1.size + mag2.size
            val result = UIntArray(resultLength)

            for (i in mag1.indices) {
                var carry = 0uL

                for (j in mag2.indices) {
                    val cur: ULong = result[i + j].toULong() + mag1[i].toULong() * mag2[j].toULong() + carry
                    result[i + j] = (cur and BASE).toUInt()
                    carry = cur shr BASE_SIZE
                }

                result[i + mag2.size] = (carry and BASE).toUInt()
            }

            return stripLeadingZeros(result)
        }

        internal fun karatsubaMultiplyMagnitudes(mag1: UIntArray, mag2: UIntArray): UIntArray {
            //https://en.wikipedia.org/wiki/Karatsuba_algorithm
            val halfSize = min(mag1.size, mag2.size) / 2
            val x0 = mag1.sliceArray(0 until halfSize).toBigInt(1)
            val x1 = mag1.sliceArray(halfSize until mag1.size).toBigInt(1)
            val y0 = mag2.sliceArray(0 until halfSize).toBigInt(1)
            val y1 = mag2.sliceArray(halfSize until mag2.size).toBigInt(1)

            val z0 = x0 * y0
            val z2 = x1 * y1
            val z1 = (x0 - x1) * (y1 - y0) + z0 + z2

            return (z2.shl(2 * halfSize * BASE_SIZE) + z1.shl(halfSize * BASE_SIZE) + z0).underlying
        }

        private fun divideMagnitudeByUInt(mag: UIntArray, x: UInt): UIntArray {
            val resultLength = mag.size
            val result = UIntArray(resultLength)
            var carry = 0uL

            for (i in mag.size - 1 downTo 0) {
                val cur: ULong = mag[i].toULong() + (carry shl BASE_SIZE)
                result[i] = (cur / x).toUInt()
                carry = cur % x
            }

            return stripLeadingZeros(result)
        }
    }
}

private fun stripLeadingZeros(mag: UIntArray): UIntArray {
    if (mag.isEmpty() || mag.last() != 0U) return mag
    var resSize = mag.size - 1

    while (mag[resSize] == 0U) {
        if (resSize == 0) break
        resSize -= 1
    }

    return mag.sliceArray(IntRange(0, resSize))
}

/**
 * Returns the absolute value of the given value [x].
 */
public fun abs(x: BigInt): BigInt = x.abs()

/**
 * Convert this [Int] to [BigInt]
 */
public fun Int.toBigInt(): BigInt =
    BigInt(sign.toByte(), uintArrayOf(kotlin.math.abs(this).toUInt()))

/**
 * Convert this [Long] to [BigInt]
 */
public fun Long.toBigInt(): BigInt = BigInt(
    sign.toByte(),
    stripLeadingZeros(
        uintArrayOf(
            (kotlin.math.abs(this).toULong() and BASE).toUInt(),
            ((kotlin.math.abs(this).toULong() shr BASE_SIZE) and BASE).toUInt()
        )
    )
)

/**
 * Convert UInt to [BigInt]
 */
public fun UInt.toBigInt(): BigInt =
    BigInt(1, uintArrayOf(this))

/**
 * Convert ULong to [BigInt]
 */
public fun ULong.toBigInt(): BigInt = BigInt(
    1,
    stripLeadingZeros(
        uintArrayOf(
            (this and BASE).toUInt(),
            ((this shr BASE_SIZE) and BASE).toUInt()
        )
    )
)

/**
 * Create a [BigInt] with this array of magnitudes with protective copy
 */
public fun UIntArray.toBigInt(sign: Byte): BigInt {
    require(sign != 0.toByte() || isEmpty())
    return BigInt(sign, copyOf())
}

private fun UIntArray.toUByteArray(): UByteArray {
    val bytes = UByteArray(size = size * Int.SIZE_BYTES)

    for ((i, int) in this.withIndex()) {
        bytes[i * 4 + 0] = (int shr 24).toUByte()
        bytes[i * 4 + 1] = (int shr 16).toUByte()
        bytes[i * 4 + 2] = (int shr 8).toUByte()
        bytes[i * 4 + 3] = (int shr 0).toUByte()
    }

    return bytes
}

// mutating
private fun UByteArray.twosComplementSign(sign: Byte): UByteArray {
    if (isEmpty()) return this

    if (sign < 0) {
        for (i in this.indices) {
            this[i] = this[i].inv()
        }
    }

    val signBit = if (sign > 0) 0u else 1u

    // prepend sign-byte if need
    // Example:
    // 10000000 is an absolute value for 128
    // if we want to have positive 128, we need to prepend 00000000,
    // so the number will be 00000000 10000000
    val result = if (this[0].toUInt() shr 7 == signBit) {
        this
    } else {
        val signByte = if (sign > 0) UByte.MIN_VALUE else UByte.MAX_VALUE
        ubyteArrayOf(signByte) + this
    }

    if (sign > 0) return result

    for (i in lastIndex downTo 0) {
        val byte = this[i]
        this[i] = (byte + 1u).toUByte()

        // if we have overflow (0b11111111 + 0b1 = 0b00000000),
        // then we need to continue addition until no overflow
        // or until the end of the array
        val overflow = this[i] < byte
        if (!overflow) break
    }

    return result
}

private fun UByteArray.stripLeadingSignBytes(sign: Byte): UByteArray {
    if (isEmpty()) return this

    val signBit = if (sign > 0) 0u else 1u
    val signByte = if (sign > 0) UByte.MIN_VALUE else UByte.MAX_VALUE
    var leadingBytes = 0

    for ((i, byte) in this.withIndex()) {
        if (i == this.lastIndex) break
        // if the byte is not 0b11111111 or 0b00000000, we cannot strip anymore
        if (byte != signByte) break
        val nextByte = this[i + 1]
        // if the first bit of the next byte is other than 1 (for negative) or 0 (for positive)
        // current byte is required to indicate its sign
        if (nextByte.toUInt() shr 7 != signBit) break
        leadingBytes++
    }

    if (leadingBytes == 0) return this

    return sliceArray(leadingBytes..lastIndex)
}

// protecting copy
public fun UByteArray.toBigInt(signed: Boolean = true): BigInt {
    if (isEmpty()) return ZERO
    if (all { byte -> byte == UByte.MIN_VALUE }) return ZERO

    val sign = if (signed) {
        (this[0].toUInt() shr 7).toByte()
    } else {
        0
    }

    val absoluteValue = copyOf()
        .twosComplementAbs(sign)
        .padToInt()
        .toUIntArray()

    val result = BigInt(
        sign = if (sign > 0) -1 else 1,
        underlying = absoluteValue
    )
    if (result == ZERO) return ZERO
    return result
}

public fun ByteArray.toBigInt(signed: Boolean = true): BigInt {
    return toUByteArray().toBigInt(signed)
}

// mutating
private fun UByteArray.twosComplementAbs(sign: Byte): UByteArray {
    if (sign == 0.toByte()) return this

    for (i in lastIndex downTo 0) {
        val byte = this[i]
        this[i] = (byte - 1u).toUByte()

        // if we have overflow (0b00000000 - 0b1 = 0b11111111),
        // then we need to continue subtraction until no overflow
        // or until the end of the array
        val underflow = this[i] > byte
        if (!underflow) break
    }

    for (i in this.indices) {
        this[i] = this[i].inv()
    }

    return this
}

private fun UByteArray.padToInt(): UByteArray {
    if (this.size % 4 == 0) return this
    return UByteArray(4 - this.size % 4) + this
}

private fun UByteArray.toUIntArray(): UIntArray {
    val result = UIntArray(size = size / Int.SIZE_BYTES)

    for (i in result.indices) {
        result[i] = result[i] or (this[i * 4 + 0].toUInt() shl 24)
        result[i] = result[i] or (this[i * 4 + 1].toUInt() shl 16)
        result[i] = result[i] or (this[i * 4 + 2].toUInt() shl 8)
        result[i] = result[i] or (this[i * 4 + 3].toUInt() shl 0)
    }

    result.reverse()
    return result
}
