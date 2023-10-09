package ktproto.io.output

import ktproto.io.annotation.OngoingConnection
import ktproto.io.memory.MemoryArena

@OngoingConnection
public fun interface Output {
    public suspend fun write(source: MemoryArena)
}
