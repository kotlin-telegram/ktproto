package ktproto.client.requests

import kotl.serialization.annotation.Crc32
import kotl.serialization.annotation.TLRpc
import kotl.serialization.bytes.Bytes
import kotl.serialization.int.Int128
import kotlinx.serialization.Serializable
import ktproto.client.MTProtoClient
import ktproto.client.serialization.MTProtoRequest
import ktproto.client.serialization.execute

// req_pq_multi#be7e8ef1 nonce:int128 = ResPQ;
@Serializable
@TLRpc(crc32 = 0xbe7e8ef1_u)
public data class TLRequestPQ(
    public val nonce: Int128
) : MTProtoRequest<TLResponsePQ>

// resPQ#05162463 nonce:int128 server_nonce:int128 pq:bytes server_public_key_fingerprints:Vector long = ResPQ;
@Serializable
@Crc32(value = 0x05162463_u)
public data class TLResponsePQ(
    public val nonce: Int128,
    public val serverNonce: Int128,
    public val pq: Bytes,
    public val serverPublicKeyFingerprints: List<Long>
)

public suspend fun MTProtoClient.requestPQ(
    nonce: Int128
): TLResponsePQ = execute(TLRequestPQ(nonce))
