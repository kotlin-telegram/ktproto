@file:OptIn(ExperimentalUnsignedTypes::class)

package ktproto.stdlib.bytes

public fun UByteArray.toBinaryString(): String =
    joinToString(" ") { byte -> byte.toString(radix = 2).padStart(8, '0') }

public fun ByteArray.toBinaryString(): String =
    toUByteArray().toBinaryString()
