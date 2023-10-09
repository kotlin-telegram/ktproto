package ktproto.stdlib.bytes

import kotlin.random.Random

public fun Int.encodeToByteArray(): ByteArray = byteArrayOf(
    (this shr  0 and 0xff).toByte(),
    (this shr  8 and 0xff).toByte(),
    (this shr 16 and 0xff).toByte(),
    (this shr 24 and 0xff).toByte()
)

public fun ByteArray.decodeInt(offset: Int = 0): Int =
    (this[offset + 0].toInt() and 0xff shl 0) or
            (this[offset + 1].toInt() and 0xff shl 8) or
            (this[offset + 2].toInt() and 0xff shl 16) or
            (this[offset + 3].toInt() and 0xff shl 24)

private fun main() {
    repeat(1_000_000_000) {
        val int = Random.nextInt()
        require(int.encodeToByteArray().decodeInt(offset = 0) == int)
    }
}
