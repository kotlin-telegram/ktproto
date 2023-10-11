package ktproto.crypto.asn1

import ktproto.io.memory.*
import ktproto.stdlib.bytes.decodeInt
import ktproto.stdlib.bytes.padEnd

public fun parseAsn1Der(data: ByteArray): Asn1Object {
    val (_, result) = readAsn1Der(MemoryArena.of(data))
    return result
}

private const val SEQUENCE: UByte = 0x30u

private fun readAsn1Der(memory: MemoryArena): Pair<MemoryArena, Asn1Object> {
    val tag: Byte
    val dropTag = memory.readByte { tag = it }
    return when (tag.toUByte()) {
        SEQUENCE -> readAsn1Container(dropTag)
        else -> readAsn1Value(dropTag)
    }
}

private fun readAsn1Container(
    memory: MemoryArena
): Pair<MemoryArena, Asn1Object.Container> {
    val (dropLength, length) = readAsn1DerLength(memory)
    var mutable = dropLength.take(length)
    val children = buildList {
        while (mutable.size > 0) {
            val (remaining, element) = readAsn1Der(mutable)
            mutable = remaining
            add(element)
        }
    }
    return dropLength.drop(length) to Asn1Object.Container(children)
}

private fun readAsn1Value(
    memory: MemoryArena
): Pair<MemoryArena, Asn1Object.Value> {
    val (remaining, length) = readAsn1DerLength(memory)
    val bytes = remaining.take(length)
    val result = Asn1Object.Value(bytes.toByteArray())
    return remaining.drop(length) to result
}

private fun readAsn1DerLength(memory: MemoryArena): Pair<MemoryArena, Int> {
    val lengthFirstByte: Byte
    val dropFirst = memory.readByte { byte ->
        lengthFirstByte = byte
    }

    // Short-Form
    if (lengthFirstByte >= 0) {
        return dropFirst to lengthFirstByte.toInt()
    }

    // Remove sign-bit
    val lengthOfTheLength = lengthFirstByte.toInt() and 0b01111111

    val length: Int

    val remaining = dropFirst.readBytes(lengthOfTheLength) { bytes ->
        val intBytes = bytes
            .apply { reverse() }
            .padEnd(4)
        length = intBytes.decodeInt()
    }

    return remaining to length
}
