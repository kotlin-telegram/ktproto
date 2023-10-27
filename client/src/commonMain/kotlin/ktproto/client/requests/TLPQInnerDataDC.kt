package ktproto.client.requests

import kotl.serialization.annotation.Crc32
import kotl.serialization.bytes.Bytes
import kotl.serialization.int.Int128
import kotlinx.serialization.Serializable

// p_q_inner_data_dc#a9f55f95 pq:bytes p:bytes q:bytes nonce:int128 server_nonce:int128 new_nonce:int256 dc:int = P_Q_inner_data;
@Serializable
@Crc32(value = 0xa9f55f95_u)
public data class TLPQInnerDataDC(
    val pq: Bytes,
    val p: Bytes,
    val q: Bytes,
    val nonce: Int128,
    val serverNonce: Int128,
    val newNonce: Int128,
    val dc: Int
)
