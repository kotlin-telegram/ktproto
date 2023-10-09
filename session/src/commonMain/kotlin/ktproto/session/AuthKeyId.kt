package ktproto.session

import ktproto.io.memory.MemoryArena
import kotlin.jvm.JvmInline

@JvmInline
public value class AuthKeyId(public val bits64: MemoryArena) {
    public companion object {
        public const val SIZE_BYTES: Int = 8
    }
}
