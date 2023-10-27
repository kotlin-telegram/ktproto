package ktproto.crypto.aes

import kotlin.jvm.JvmInline

@JvmInline
public value class AesIV(public val bytes: ByteArray) {
    init {
        require(bytes.size == SIZE_BYTES)
    }

    public companion object {
        public const val SIZE_BYTES: Int = 32
        public val Zero: AesIV = AesIV(ByteArray(SIZE_BYTES))
    }
}
