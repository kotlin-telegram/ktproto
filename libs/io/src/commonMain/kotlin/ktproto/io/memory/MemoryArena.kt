package ktproto.io.memory

import ktproto.stdlib.bytes.encodeToByteArray

public class MemoryArena(
    public val data: ByteArray,
    public val range: IntRange
) {
    public constructor(
        data: ByteArray,
        start: Int,
        end: Int
    ) : this(data, start..end)

    init {
        require(
            value = size >= 0 &&
                    (range.isEmpty() ||
                    range.first in data.indices &&
                    range.last in data.indices)
        ) { "Invalid bounds (memoryRange: $range, data.indices: ${data.indices}, ${range.first in data.indices} ${range.last in data.indices})" }
    }

    public companion object {
        public fun allocate(n: Int): MemoryArena = MemoryArena(ByteArray(n), 0..<n)
        public fun allocateInt(): MemoryArena = allocate(Int.SIZE_BYTES)
        public fun of(bytes: ByteArray): MemoryArena = MemoryArena(bytes, bytes.indices)
        public fun of(int: Int): MemoryArena = of(int.encodeToByteArray())
        public fun of(int: UInt): MemoryArena = of(int.toInt())
        public fun of(long: Long): MemoryArena = of(long.encodeToByteArray())
    }
}

public val MemoryArena.size: Int get() = end - start + 1
public val MemoryArena.start: Int get() = range.first
public val MemoryArena.end: Int get() = range.last

public fun MemoryArena.take(n: Int): MemoryArena {
    require(value = n <= size) { "n: $n, size: $size" }
    return MemoryArena(
        data = data,
        start = start,
        end = start + n - 1
    )
}

public fun MemoryArena.drop(n: Int): MemoryArena {
    require(value = n <= size) { "n: $n, size: $size" }
    return MemoryArena(
        data = data,
        start = start + n,
        end = end
    )
}

public fun MemoryArena.toByteArray(): ByteArray {
    val array = ByteArray(size)
    data.copyInto(
        destination = array,
        startIndex = start,
        endIndex = end + 1
    )
    return array
}

public inline fun MemoryArena.checkCapacity(
    n: Int,
    message: () -> String
) {
    require(size >= n) { message() }
}

public inline fun MemoryArena.ensureCapacity(
    n: Int,
    f: (Int) -> Int = { it.takeHighestOneBit() shl 1 }
): MemoryArena {
    if (size >= n) return this
    val new = MemoryArena.allocate(f(n))
    new.write(source = this)
    return new
}
