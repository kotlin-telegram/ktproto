package ktproto.io.memory

public fun MemoryArena.write(source: MemoryArena): MemoryArena {
    checkCapacity(source.size) { "Insufficient size in the current area (this.size: ${this.size}, source.size: ${source.size})" }

    source.data.copyInto(
        destination = data,
        destinationOffset = start,
        startIndex = source.start,
        endIndex = source.end + 1
    )

    return MemoryArena(
        data = data,
        start = start + source.size,
        end = end
    )
}

public fun MemoryArena.write(array: ByteArray): MemoryArena =
    write(MemoryArena.of(array))

public fun MemoryArena.write(int: Int): MemoryArena =
    write(MemoryArena.of(int))

public fun MemoryArena.write(int: UInt): MemoryArena =
    write(MemoryArena.of(int))
