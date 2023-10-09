package ktproto.session.encrypted

import ktproto.io.memory.MemoryArena
import kotlin.jvm.JvmInline

@JvmInline
public value class SeqNo(public val bits32: MemoryArena) {
    public companion object {
        public const val SIZE_BYTES: Int = 4
    }
}
