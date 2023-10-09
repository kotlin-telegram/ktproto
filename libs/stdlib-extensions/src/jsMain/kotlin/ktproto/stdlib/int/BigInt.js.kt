package ktproto.stdlib.int

public actual class BigInt private constructor(
    any: Any?
) : Comparable<BigInt> {
    private val underlying = js("BigInt")(any)

    public actual operator fun plus(other: BigInt): BigInt {
        return BigInt(any = underlying + other.underlying)
    }

    public actual operator fun minus(other: BigInt): BigInt {
        TODO("Not yet implemented")
    }

    public actual operator fun times(other: BigInt): BigInt {
        TODO("Not yet implemented")
    }

    public actual operator fun div(other: BigInt): BigInt {
        TODO("Not yet implemented")
    }

    public actual operator fun rem(other: BigInt): BigInt {
        TODO("Not yet implemented")
    }

    public actual operator fun unaryPlus(): BigInt {
        TODO("Not yet implemented")
    }

    public actual operator fun unaryMinus(): BigInt {
        TODO("Not yet implemented")
    }

    public actual operator fun inc(): BigInt {
        TODO("Not yet implemented")
    }

    public actual operator fun dec(): BigInt {
        TODO("Not yet implemented")
    }

    public actual override fun compareTo(other: BigInt): Int {
        TODO("Not yet implemented")
    }

    public actual infix fun pow(other: BigInt): BigInt {
        TODO("Not yet implemented")
    }

    public actual val bytesSize: Int
        get() = toByteArrayLE().size

    @OptIn(ExperimentalStdlibApi::class)
    public actual fun toByteArrayLE(): ByteArray {
        var hex = underlying.toString(16) as String
        hex = hex.padStart(length = hex.length.nearestMultipleOf(n = 2), padChar = '0')
        println(hex)
        return hex.hexToByteArray().also { println(it) }
    }

    public actual fun toByteArrayBE(): ByteArray {
        TODO("Not yet implemented")
    }

    override fun toString(): String = underlying.toString()

    public actual companion object {
        public actual fun ofLE(bytes: ByteArray): BigInt {
            return ofBE(bytes.reversedArray())
        }

        public actual fun ofBE(bytes: ByteArray): BigInt {
            val string = "0x" + bytes.joinToString(separator = "") { byte ->
                byte.toUByte().toString(radix = 16).padStart(length = 2, padChar = '0')
            }
            return BigInt(string)
        }
    }

}