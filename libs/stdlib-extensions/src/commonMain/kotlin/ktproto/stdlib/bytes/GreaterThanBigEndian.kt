package ktproto.stdlib.bytes

public infix fun ByteArray.greaterThanBigEndian(other: ByteArray): Boolean {
    if (size > other.size) return true
    if (size < other.size) return false

    val firstIterator = iterator()
    val secondIterator = other.iterator()

    while (firstIterator.hasNext()) {
        val first = firstIterator.nextByte()
        val second = secondIterator.nextByte()
        if (first > second) return true
        if (first < second) return false
    }

    return false
}
