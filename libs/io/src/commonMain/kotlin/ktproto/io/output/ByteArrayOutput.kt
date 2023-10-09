package ktproto.io.output

import ktproto.io.annotation.OngoingConnection
import ktproto.io.memory.*

@OptIn(OngoingConnection::class)
public class ByteArrayOutput : Output {
    private var memory = MemoryArena.allocate(n = 32)

    private fun ensureCapacity(n: Int) {
        if (memory.size >= n) return
        val allocated = MemoryArena.allocate(n = n.takeHighestOneBit() shl 1)
        allocated.write(memory)
        memory = allocated
    }

    override suspend fun write(source: MemoryArena) {
        ensureCapacity(source.size)
        memory.write(source)
    }

    public fun toByteArray(): ByteArray = memory.toByteArray()
}
