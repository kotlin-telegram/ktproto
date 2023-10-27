package ktproto.crypto.aes

import ktproto.crypto.sha.crypto
import ktproto.crypto.sha.encrypt
import ktproto.crypto.sha.importKey
import ktproto.stdlib.platform.jsRuntime

public actual suspend fun AesBlock.encrypted(key: AesKey): AesBlock {
    @Suppress("unused")
    val algorithm = object {
        @JsName("name")
        val name = "AES-CBC"
        @JsName("iv")
        val iv = ByteArray(size = 16)
    }

    val imported = jsRuntime.crypto.subtle.importKey(
        format = "raw",
        key = key.bytes,
        algorithm = "AES-CBC",
        extractable = false,
        usages = listOf("encrypt")
    )

    val paddedBytes = jsRuntime.crypto
        .subtle
        .encrypt(algorithm, imported, this.bytes)

    // Discards the last 16 bytes with padding
    val bytes = ByteArray(AesBlock.SIZE_BYTES) { paddedBytes[it] }

    return AesBlock(bytes)
}
