package ktproto.crypto.aes

import kotlin.jvm.JvmInline

@JvmInline
public value class AesBlock(
    public val bytes: ByteArray
) {
    init {
        require(bytes.size == SIZE_BYTES) { "size: ${bytes.size}, required: $SIZE_BYTES" }
    }

    public companion object {
        public const val SIZE_BYTES: Int = 16
    }
}
