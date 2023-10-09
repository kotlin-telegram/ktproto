package ktproto.session.encrypted

import ktproto.io.memory.*
import ktproto.session.AuthKeyId
import kotlin.jvm.JvmInline

public data class MTProtoEnvelope(
    public val authKeyId: AuthKeyId,
    public val messageKey: MessageKey,
    public val encryptedData: MemoryArena
) {
    public fun toByteArray(): ByteArray = listOf(
        authKeyId.bits64,
        messageKey.bits128,
        encryptedData
    ).flatten().data

    public companion object {
        public fun of(memory: MemoryArena): MTProtoEnvelope =
            MTProtoEnvelope(
                authKeyId = AuthKeyId(memory.take(AuthKeyId.SIZE_BYTES)),
                messageKey = MessageKey(memory.drop(AuthKeyId.SIZE_BYTES).take(MessageKey.SIZE_BYTES)),
                encryptedData = memory.drop(n = AuthKeyId.SIZE_BYTES + MessageKey.SIZE_BYTES)
            )
    }

    @JvmInline
    public value class MessageKey(public val bits128: MemoryArena) {
        public companion object {
            public const val SIZE_BYTES: Int = 16
        }
    }
}
