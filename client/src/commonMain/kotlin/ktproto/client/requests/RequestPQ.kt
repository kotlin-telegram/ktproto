package ktproto.client.requests

import kotl.serialization.annotation.Crc32
import kotl.serialization.annotation.TLRpc
import kotl.serialization.annotation.TLSize
import kotl.serialization.int.Int128
import kotlinx.serialization.Serializable
import ktproto.client.serialization.MTProtoRequestContainer

// req_pq_multi#be7e8ef1 nonce:int128 = ResPQ;
@Serializable
@TLRpc(crc32 = 0xbe7e8ef1_u)
public data class TLRequestPQ(
    public val nonce: Int128
) : MTProtoRequestContainer<TLResponsePQ>

// resPQ#05162463 nonce:int128 server_nonce:int128 pq:string server_public_key_fingerprints:Vector long = ResPQ;
@Serializable
@Crc32(value = 0x05162463_u)
public data class TLResponsePQ(
    public val nonce: Int128,
    public val serverNonce: Int128,
    public val pq: String,
    public val serverPublicKey: List<Long>
)
