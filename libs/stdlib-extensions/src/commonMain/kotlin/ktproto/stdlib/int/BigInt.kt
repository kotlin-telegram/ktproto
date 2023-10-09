package ktproto.stdlib.int

import ktproto.stdlib.bytes.encodeToByteArray
import kotlin.random.Random

public expect class BigInt : Comparable<BigInt> {
    public operator fun plus(other: BigInt): BigInt
    public operator fun minus(other: BigInt): BigInt
    public operator fun times(other: BigInt): BigInt
    public operator fun div(other: BigInt): BigInt
    public operator fun rem(other: BigInt): BigInt

    public operator fun unaryPlus(): BigInt
    public operator fun unaryMinus(): BigInt

    public operator fun inc(): BigInt
    public operator fun dec(): BigInt

    public override operator fun compareTo(other: BigInt): Int

    public infix fun pow(other: BigInt): BigInt

    public val bytesSize: Int
    public fun toByteArrayLE(): ByteArray
    public fun toByteArrayBE(): ByteArray

    public companion object {
        public fun ofLE(bytes: ByteArray): BigInt
        public fun ofBE(bytes: ByteArray): BigInt
    }
}

private val ZERO = BigInt.ofLE(0.encodeToByteArray())
private val ONE = BigInt.ofLE(1.encodeToByteArray())

public val BigInt.Companion.ZERO: BigInt get() = ktproto.stdlib.int.ZERO
public val BigInt.Companion.ONE: BigInt get() = ktproto.stdlib.int.ONE

public val Int.bi: BigInt get() = when (this) {
    0 -> BigInt.ZERO
    1 -> BigInt.ONE
    else -> BigInt.ofLE(encodeToByteArray())
}
public fun Random.nextBigInt(min: BigInt, max: BigInt): BigInt {
    val randomBytes = nextBytes(max.bytesSize)
    val random = BigInt.ofLE(randomBytes)
    val range = max - min
    return min + random % range
}

public inline fun repeat(int: BigInt, block: (BigInt) -> Unit) {
    for (i in 0.bi..<int) {
        block(i)
    }
}

public fun abs(x: BigInt): BigInt = when {
    x >= 0.bi -> x
    else -> -x
}

public fun min(x: BigInt, y: BigInt): BigInt = when {
    x < y -> x
    else -> y
}
