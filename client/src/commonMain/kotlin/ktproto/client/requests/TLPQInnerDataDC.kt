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

// p_q_inner_data#83c95aec pq:string p:string q:string nonce:int128 server_nonce:int128 new_nonce:int256 = P_Q_inner_data;
@Deprecated(
    message = "This constructor was deprecated by Telegram",
    replaceWith = ReplaceWith(
        expression = "TLPQInnerDataDC(pq = pq, p = p, q = q, nonce = nonce, serverNonce = serverNonce, newNonce = newNonce, dc = )",
        imports = ["ktproto.client.requests.TLPQInnerDataDC"]
    )
)
@Serializable
@Crc32(value = 0x83c95aec_u)
public data class TLPQInnerData(
    val pq: Bytes,
    val p: Bytes,
    val q: Bytes,
    val nonce: Int128,
    val serverNonce: Int128,
    val newNonce: Int128
)
