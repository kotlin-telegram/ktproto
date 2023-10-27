package ktproto.stdlib.bit

import kotlin.jvm.JvmInline

@OptIn(ExperimentalUnsignedTypes::class)
@JvmInline
public value class BitArray(
    // Padded payload, where padding is determined by bit-switch from 1 to 0
    // 0b00101001001100111
    // And the respective padding is 0111
    //
    // When payload is already padded,
    // you should append one padding-only byte with the
    // following value: [data]01111111
    private val payload: UByteArray
) : Iterable<Bit> {
    public val size: Int get() = payload.size * 8 - paddingSize

    public val paddingSize: Int get() {
        var index = 1
        while (getBitUnsafe(n = (payload.size * 8) - index).enabled) index++
        return index
    }

    public val lastIndex: Int get() = size - 1

    public operator fun set(n: Int, bit: Bit) {
        checkIndex(n)
        if (bit.enabled) setBit(n) else resetBit(n)
    }

    public fun setBit(n: Int) {
        checkIndex(n)
        val bit = 0b10000000u shr (n % 8)
        payload[n / 8] = payload[n / 8] or bit.toUByte()
    }

    public fun resetBit(n: Int) {
        checkIndex(n)
        val bit = (0b10000000u shr (n % 8)).inv()
        payload[n / 8] = payload[n / 8] and bit.toUByte()
    }

    public fun getBit(n: Int): Bit {
        checkIndex(n)
        return getBitUnsafe(n)
    }

    // Allows to get padding bits
    private fun getBitUnsafe(n: Int): Bit {
        val bit = payload[n / 8].toUInt() shl (n % 8) shr 7 and 1u
        return Bit(enabled = bit == 1u)
    }

    public override fun iterator(): Iterator<Bit> = iterator block@{
        repeat(size) { i ->
            yield(getBit(i))
        }
    }

    public fun toUByteArray(): UByteArray = payload.copyOf()

    private fun checkIndex(n: Int) {
        require(n <= lastIndex) { throw IndexOutOfBoundsException("$n") }
    }

    override fun toString(): String = "0b" + payload
        .joinToString(separator = "") { byte ->
            byte.toString(radix = 2).padStart(length = 8, padChar = '0')
        }

    public companion object {
        public const val PADDING_SIZE_AUTO: Int = -1

        public fun allocateBits(n: Int): BitArray {
            val payload = UByteArray(
                size = n / 8 + 1
            )
            val padding = when (val rem = 8 - n % 8) {
                0 -> 8
                else -> rem
            }
            val paddingByte = 0b11111111u shr (8 - padding + 1)
            payload[n / 8] = paddingByte.toUByte()
            return BitArray(payload)
        }


        public fun of(
            bytes: UByteArray,
            padded: Boolean = false
        ): BitArray {
            val paddedBytes = if (padded) {
                bytes
            } else {
                bytes + 0b01111111u
            }
            val bits = BitArray(paddedBytes)
            return bits
        }
    }
}

public fun BitArray.setBits(offset: Int, bits: BitArray) {
    for ((i, bit) in bits.withIndex()) {
        this[offset + i] = bit
    }
}

public fun BitArray.setByte(offset: Int, byte: UByte) {
    this[offset + 0] = (byte.toUInt() shr 7 and 0b1u == 1u).bit
    this[offset + 1] = (byte.toUInt() shr 6 and 0b1u == 1u).bit
    this[offset + 2] = (byte.toUInt() shr 5 and 0b1u == 1u).bit
    this[offset + 3] = (byte.toUInt() shr 4 and 0b1u == 1u).bit
    this[offset + 4] = (byte.toUInt() shr 3 and 0b1u == 1u).bit
    this[offset + 5] = (byte.toUInt() shr 2 and 0b1u == 1u).bit
    this[offset + 6] = (byte.toUInt() shr 1 and 0b1u == 1u).bit
    this[offset + 7] = (byte.toUInt() shr 0 and 0b1u == 1u).bit
}

@OptIn(ExperimentalUnsignedTypes::class)
public fun main() {
    val bits = BitArray.allocateBits(10)
    bits[1] = Bit.Enabled
    val bytes = bits.toUByteArray()
    println(bytes.contentToString())
    val restored = BitArray.of(bytes, padded = true)
    println(restored.size)
    for ((i, bit) in restored.withIndex()) {
        print("$i: $bit, ")
    }
}
