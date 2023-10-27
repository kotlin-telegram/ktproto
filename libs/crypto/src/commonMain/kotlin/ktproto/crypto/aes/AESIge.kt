package ktproto.crypto.aes

import ktproto.stdlib.bytes.xor
import kotlin.experimental.xor

public suspend fun ByteArray.encryptAesIge(
    key: AesKey,
    iv: AesIV
): ByteArray {
    require(this.size % AesBlock.SIZE_BYTES == 0) {
        "Input data is not correctly padded"
    }

    val n = this.size / AesBlock.SIZE_BYTES
    val encryptedBytes = ByteArray(this.size)

    var previousBlock = iv.bytes.copyOfRange(0, 16)
    var previousCipher = iv.bytes.copyOfRange(16, 32)

    repeat(n) { i ->
        val plaintextBlock = this.copyOfRange(i * 16, (i + 1) * 16)
        val xorResult = plaintextBlock xor previousCipher
        val plainBlock = AesBlock(xorResult)
        val encryptedBlock = plainBlock.encrypted(key)
        val ciphertextBlock = encryptedBlock.bytes xor previousBlock
        ciphertextBlock.copyInto(encryptedBytes, i * 16)
        previousBlock = plaintextBlock
        previousCipher = ciphertextBlock
    }

    return encryptedBytes
}
