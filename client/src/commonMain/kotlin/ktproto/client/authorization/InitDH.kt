package ktproto.client.authorization

import kotl.serialization.bytes.Bytes
import kotl.serialization.int.Int128
import ktproto.client.MTProtoClient
import ktproto.client.requests.getPQ
import ktproto.client.rsa.RsaPublicKey
import ktproto.client.rsa.fingerprint
import ktproto.crypto.factorization.PollardRhoBrent
import ktproto.stdlib.bigint.toBigInt
import ktproto.stdlib.bytes.decodeLong
import ktproto.stdlib.random.nextInt128
import kotlin.random.Random

internal data class InitDHResult(
    val pq: Bytes,
    val p: Bytes,
    val q: Bytes,
    val publicKey: RsaPublicKey,
    val nonce: Int128,
    val serverNonce: Int128
)

internal suspend fun initDH(
    client: MTProtoClient,
    keys: List<RsaPublicKey>
): InitDHResult {
    // 1)
    val nonce = Random.nextInt128()
    val response = client.getPQ(nonce)

    require(response.nonce.data.contentEquals(nonce.data)) {
        "Server responded with invalid nonce (actual: $nonce, expected: ${response.nonce})"
    }
    require(response.pq.payload.size <= 8) {
        "Resulted payload size is more than is supported by ktproto. This should never happen, but if it did, then that is not your fault, just report it to https://github.com/ktproto/issues"
    }

    // 2)
    val publicKey = keys.firstOrNull { key ->
        key.fingerprint() in response.serverPublicKeyFingerprints
    } ?: error("Couldn't match any server_public_key_fingerprints. Response: ${response.serverPublicKeyFingerprints}, Expected: ${keys.map { key -> key.fingerprint() }}")

    // 3)
    val pq = response.pq.payload
        .apply(ByteArray::reverse)
        .decodeLong()
        .toULong()

    val (p, q) = PollardRhoBrent.factorize(pq)
    val pBytes = Bytes(p.toBigInt().toByteArray())
    val qBytes = Bytes(q.toBigInt().toByteArray())
    return InitDHResult(response.pq, pBytes, qBytes, publicKey,nonce, response.serverNonce)
}
