package ktproto.session.plain

import ktproto.io.memory.*
import ktproto.session.AuthKeyId
import ktproto.session.MessageId
import kotlin.jvm.JvmInline
import ktproto.session.AuthKeyId as AuthKeyIdType

public class MTProtoPlainEnvelope(
    public val authKeyId: AuthKeyIdType = AuthKeyId,
    public val messageId: MessageId,
    public val dataLength: DataLength,
    public val data: MemoryArena
) {
    public fun toByteArray(): ByteArray = listOf(
        authKeyId.bits64, messageId.bits64,
        dataLength.bits32, data
    ).flatten().data


    @JvmInline
    public value class DataLength(public val bits32: MemoryArena) {
        public companion object {
            public const val SIZE_BYTES: Int = Int.SIZE_BYTES
        }
    }

    public companion object {
        public val AuthKeyId: AuthKeyIdType = AuthKeyIdType(
            bits64 = MemoryArena.allocate(AuthKeyIdType.SIZE_BYTES)
        )

        public fun of(memory: MemoryArena): MTProtoPlainEnvelope {
            val authKeyId: AuthKeyId
            val messageId: MessageId
            val dataLength: DataLength

            val data = memory
                .readMemory(AuthKeyIdType.SIZE_BYTES) { read ->
                    authKeyId = AuthKeyIdType(read)
                }.readMemory(MessageId.SIZE_BYTES) { read ->
                    messageId = MessageId(read)
                }.readMemory(DataLength.SIZE_BYTES) { read ->
                    dataLength = DataLength(read)
                }

            return MTProtoPlainEnvelope(authKeyId, messageId, dataLength, data)
        }
    }
}
