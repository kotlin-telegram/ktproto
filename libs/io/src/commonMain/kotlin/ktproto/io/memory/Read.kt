@file:OptIn(ExperimentalContracts::class)

package ktproto.io.memory

import ktproto.stdlib.bytes.decodeInt
import ktproto.stdlib.bytes.decodeLong
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public fun MemoryArena.scanBytes(n: Int): ByteArray {
    val result: ByteArray
    readBytes(n) { result = it }
    return result
}

public inline fun MemoryArena.readMemory(
    n: Int, block: (MemoryArena) -> Unit
): MemoryArena {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    checkCapacity(n) { "Cannot read $n bytes, max is $size" }

    val read = take(n)
    block(read)

    return MemoryArena(
        data = data,
        start = start + n,
        end = end
    )
}

public inline fun MemoryArena.readBytes(
    n: Int, block: (ByteArray) -> Unit
): MemoryArena {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return readMemory(n) { memory -> block(memory.toByteArray()) }
}

public fun MemoryArena.scanInt(): Int {
    val result: Int
    readInt { result = it }
    return result
}

@OptIn(ExperimentalContracts::class)
public inline fun MemoryArena.readInt(block: (Int) -> Unit): MemoryArena {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return readBytes(Int.SIZE_BYTES) { block(it.decodeInt()) }
}

public fun MemoryArena.scanLong(): Long {
    val result: Long
    readLong { result = it }
    return result
}

@OptIn(ExperimentalContracts::class)
public inline fun MemoryArena.readLong(block: (Long) -> Unit): MemoryArena {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return readBytes(Long.SIZE_BYTES) { block(it.decodeLong()) }
}
