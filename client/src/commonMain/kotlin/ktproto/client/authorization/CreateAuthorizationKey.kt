package ktproto.client.authorization

import ktproto.client.MTProtoClient
import ktproto.client.rsa.RsaPublicKey

// fixme: move this function to kotel since
//  it contains telegram-specific data (Data Center ID)
public suspend fun createAuthorizationKey(
    client: MTProtoClient,
    dc: Int,
    keys: List<RsaPublicKey>
) {
    val (pq, p, q, publicKey, nonce, serverNonce) = initDH(client, keys)
    exchangeKeys(client, pq, p, q, publicKey, nonce, serverNonce, dc)
}
