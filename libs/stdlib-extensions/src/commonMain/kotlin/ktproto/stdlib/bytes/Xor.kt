package ktproto.stdlib.bytes

import kotlin.experimental.xor

public infix fun ByteArray.xor(other: ByteArray): ByteArray {
    require(this.size == other.size)
    return ByteArray(this.size) { i -> this[i] xor other[i] }
}
