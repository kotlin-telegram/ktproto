package ktproto.crypto.aes

import kotlin.jvm.JvmInline

public sealed interface AesKey {
    public val bytes: ByteArray

    @JvmInline
    public value class Bits256(public override val bytes: ByteArray) : AesKey {
        init {
            require(bytes.size == SIZE_BYTES)
        }

        public companion object {
            public const val SIZE_BYTES: Int = 32
        }
    }
}
