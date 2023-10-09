package ktproto.client.authorization

import kotl.serialization.int.Int128
import ktproto.client.MTProtoClient
import ktproto.client.requests.TLRequestPQ
import ktproto.client.serialization.execute
import kotlin.random.Random

public suspend fun createAuthorizationKey(
    client: MTProtoClient
) {
    val nonce = Random.nextInt128()
    val request = client.execute(TLRequestPQ(nonce))
    require(request.nonce.data.contentEquals(nonce.data)) {
        "Server responded with invalid nonce (actual: $nonce, expected: ${request.nonce})"
    }
    println(request)
}

private fun Random.nextInt128() = Int128(
    data = intArrayOf(
        nextInt(),
        nextInt(),
        nextInt(),
        nextInt()
    )
)
