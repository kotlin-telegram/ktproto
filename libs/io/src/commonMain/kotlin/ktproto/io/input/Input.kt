package ktproto.io.input

import ktproto.io.annotation.OngoingConnection
import ktproto.io.memory.MemoryArena

@OngoingConnection
public fun interface Input {
    public suspend fun read(destination: MemoryArena)
}
