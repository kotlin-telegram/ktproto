package ktproto.client.authorization

import kotl.serialization.TL
import kotl.serialization.bytes.Bytes
import kotl.serialization.int.Int128
import kotlinx.serialization.encodeToByteArray
import ktproto.client.MTProtoClient
import ktproto.client.requests.TLPQInnerDataDC
import ktproto.client.requests.getDHParams
import ktproto.client.rsa.RsaPublicKey
import ktproto.client.rsa.fingerprint
import ktproto.crypto.aes.AesIV
import ktproto.crypto.aes.AesKey
import ktproto.crypto.aes.encryptAesIge
import ktproto.crypto.sha.sha256
import ktproto.stdlib.bigint.toBigInt
import ktproto.stdlib.bytes.padStart
import ktproto.stdlib.bytes.xor
import ktproto.stdlib.random.nextInt128
import kotlin.random.Random

@OptIn(ExperimentalStdlibApi::class)
internal suspend fun exchangeKeys(
    client: MTProtoClient,
    pq: Bytes,
    p: Bytes,
    q: Bytes,
    serverPublicKey: RsaPublicKey,
    nonce: Int128,
    serverNonce: Int128,
    dc: Int
) {
    println("Server Key Fingerprint: ${serverPublicKey.fingerprint().toHexString()} (Production)")
    // 4)
    val newNonce = Random.nextInt128()

    val request = TLPQInnerDataDC(
        pq = pq,
        p = p,
        q = q,
        nonce = nonce,
        serverNonce = serverNonce,
        newNonce = newNonce,
        dc = dc
    )

    println("p_q_inner_data_dc: $request")

    val data = TL.encodeToByteArray(request)
    val encryptedData = rsaPad(data, serverPublicKey)

    val params = client.getDHParams(
        nonce = nonce,
        serverNonce = serverNonce,
        p = p,
        q = q,
        publicKeyFingerprint = serverPublicKey.fingerprint(),
        encryptedData = Bytes(encryptedData)
    )
    println(params)
}

private suspend fun rsaPad(data: ByteArray, publicKey: RsaPublicKey): ByteArray {
    // 4.1)
    require(data.size <= 144) { "One has to check that data is not longer than 144 bytes. https://core.telegram.org/mtproto/auth_key" }

    // data_with_padding := data + random_padding_bytes;
    // -- where random_padding_bytes are chosen so that the
    // resulting length of data_with_padding is precisely 192 bytes,
    // and data is the TL-serialized data to be encrypted as before.
    val paddingSize = 192 - data.size
    val dataWithPadding = data + Random.nextBytes(paddingSize)

    // data_pad_reversed := BYTE_REVERSE(data_with_padding);
    // -- is obtained from data_with_padding by reversing the byte order.
    val dataPadReversed = dataWithPadding.reversedArray()

    while (true) {
        // a random 32-byte temp_key is generated.
        val tempKey = Random
            .nextBytes(AesKey.Bits256.SIZE_BYTES)
            .let(AesKey::Bits256)

        // data_with_hash := data_pad_reversed + SHA256(temp_key + data_with_padding);
        // -- after this assignment, data_with_hash is exactly 224 bytes long.
        val dataWithHash = dataPadReversed + (tempKey.bytes + dataWithPadding).sha256()
        // aes_encrypted := AES256_IGE(data_with_hash, temp_key, 0);
        // -- AES256-IGE encryption with zero IV.
        val aesEncrypted = dataWithHash.encryptAesIge(tempKey, AesIV.Zero)
        // temp_key_xor := temp_key XOR SHA256(aes_encrypted);
        // -- adjusted key, 32 bytes
        val tempKeyXor = tempKey.bytes xor aesEncrypted.sha256()
        // key_aes_encrypted := temp_key_xor + aes_encrypted;
        // -- exactly 256 bytes (2048 bits) long
        val keyAesEncrypted = tempKeyXor + aesEncrypted

        val publicKeyModulusInt = publicKey.modulus.toBigInt(signed = false)
        val publicKeyExponentInt = publicKey.publicExponent.toBigInt(signed = false)
        val keyAesEncryptedInt = keyAesEncrypted.toBigInt(signed = false)

        // The value of key_aes_encrypted is compared with the RSA-modulus
        // of server_pubkey as a big-endian 2048-bit (256-byte) unsigned
        // integer. If key_aes_encrypted turns out to be greater
        // than or equal to the RSA modulus, the previous steps
        // starting from the generation of new random temp_key are
        // repeated. Otherwise the final step is performed:
        if (keyAesEncryptedInt >= publicKeyModulusInt) continue

        // encrypted_data := RSA(key_aes_encrypted, server_pubkey);
        // -- 256-byte big-endian integer is elevated to the requisite power
        // from the RSA public key modulo the RSA modulus
        val encryptedData = keyAesEncryptedInt.modPow(
            exponent = publicKeyExponentInt,
            modulus = publicKeyModulusInt
        ).toByteArray()

        // and the result is
        // stored as a big-endian integer consisting of exactly 256 bytes
        // (with leading zero bytes if required).
        return if (encryptedData.size < 256) {
            encryptedData.padStart(desiredLength = 256)
        } else {
            encryptedData.drop(n = encryptedData.size - 256).toByteArray()
        }
    }
}
