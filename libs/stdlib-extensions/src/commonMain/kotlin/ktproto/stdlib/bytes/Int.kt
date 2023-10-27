package ktproto.stdlib.bytes

import kotlin.random.Random

public fun Int.encodeToByteArray(): ByteArray = encodeToByteArray(ByteArray(Int.SIZE_BYTES))

public fun Int.encodeToByteArray(to: ByteArray, offset: Int = 0): ByteArray {
    to[offset + 0] = (this shr  0).toByte()
    to[offset + 1] = (this shr  8).toByte()
    to[offset + 2] = (this shr 16).toByte()
    to[offset + 3] = (this shr 24).toByte()
    return to
}

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
