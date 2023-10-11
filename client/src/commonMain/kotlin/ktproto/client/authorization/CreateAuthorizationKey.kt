package ktproto.client.authorization

import ktproto.crypto.factorization.PollardRhoBrent
import kotl.serialization.int.Int128
import ktproto.client.MTProtoClient
import ktproto.client.requests.requestPQ
import ktproto.client.rsa.RsaPublicKey
import ktproto.client.rsa.fingerprint
import ktproto.stdlib.bytes.decodeLong
import kotlin.random.Random

public suspend fun createAuthorizationKey(
    client: MTProtoClient,
    keys: List<RsaPublicKey>
) {
    val (p, q, publicKey, serverNonce) = initDH(client, keys)

}

private data class InitDHResult(
    val p: ULong,
    val q: ULong,
    val publicKey: RsaPublicKey,
    val serverNonce: Int128
)

private suspend fun initDH(
    client: MTProtoClient,
    keys: List<RsaPublicKey>
): InitDHResult {
    // 1)
    val nonce = Random.nextInt128()
    val response = client.requestPQ(nonce)

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
    return InitDHResult(p, q, publicKey, response.serverNonce)
}

private fun Random.nextInt128() = Int128(
    data = intArrayOf(
        nextInt(),
        nextInt(),
        nextInt(),
        nextInt()
    )
)
