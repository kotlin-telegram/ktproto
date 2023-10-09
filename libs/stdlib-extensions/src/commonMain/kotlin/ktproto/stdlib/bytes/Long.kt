package ktproto.stdlib.bytes

import kotlin.random.Random

public fun Long.encodeToByteArray(): ByteArray = byteArrayOf(
    (this shr  0).toByte(),
    (this shr  8).toByte(),
    (this shr 16).toByte(),
    (this shr 24).toByte(),
    (this shr 32).toByte(),
    (this shr 40).toByte(),
    (this shr 48).toByte(),
    (this shr 56).toByte()
)

public fun ByteArray.decodeLong(offset: Int = 0): Long =
    (this[offset + 0].toLong() and 0xff shl 0) or
            (this[offset + 1].toLong() and 0xff shl  8) or
            (this[offset + 2].toLong() and 0xff shl 16) or
            (this[offset + 3].toLong() and 0xff shl 24) or
            (this[offset + 4].toLong() and 0xff shl 32) or
            (this[offset + 5].toLong() and 0xff shl 40) or
            (this[offset + 6].toLong() and 0xff shl 48) or
            (this[offset + 7].toLong() and 0xff shl 56)

private fun main() {
    repeat(1_000_000_000) {
        val long = Random.nextLong()
        require(long.encodeToByteArray().decodeLong(offset = 0) == long)
    }
}
