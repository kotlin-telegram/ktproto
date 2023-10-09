package ktproto.io.memory

public fun List<MemoryArena>.flatten(): MemoryArena {
    val size = sumOf { it.size }
    val newArena = MemoryArena.allocate(size)
    fold(newArena) { arena, current -> arena.write(current) }
    return newArena
}
