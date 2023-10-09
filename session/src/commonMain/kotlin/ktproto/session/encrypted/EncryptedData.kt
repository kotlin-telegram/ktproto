package ktproto.session.encrypted

import ktproto.io.memory.MemoryArena
import ktproto.io.memory.flatten
import ktproto.session.MessageId
import kotlin.jvm.JvmInline

public data class EncryptedData(
    public val salt: Salt,
    public val sessionId: SessionId,
    public val messageId: MessageId,
    public val seqNo: SeqNo,
    public val dataLength: DataLength,
    public val data: MemoryArena,
    public val padding: MemoryArena
) {
    public fun toByteArray(): ByteArray = listOf(
        salt.bits64, sessionId.bits64,
        messageId.bits64, seqNo.bits32,
        dataLength.bits32, data, padding
    ).flatten().data

    @JvmInline
    public value class DataLength(public val bits32: MemoryArena) {
        public companion object {
            public const val SIZE_BYTES: Int = 4
        }
    }
}
