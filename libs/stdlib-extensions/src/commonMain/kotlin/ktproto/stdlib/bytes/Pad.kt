package ktproto.stdlib.bytes

public fun ByteArray.padEnd(
    desiredLength: Int,
    padByte: Byte = 0
): ByteArray {
    val padSize = desiredLength - this.size
    return this + ByteArray(padSize) { padByte }
}
