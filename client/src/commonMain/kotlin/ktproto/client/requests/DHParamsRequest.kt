package ktproto.client.requests

import kotl.serialization.annotation.Crc32
import kotl.serialization.annotation.TLRpc
import kotl.serialization.bytes.Bytes
import kotl.serialization.int.Int128
import kotlinx.serialization.Serializable
import ktproto.client.MTProtoClient
import ktproto.client.serialization.MTProtoRequest
import ktproto.client.serialization.execute

// req_DH_params#d712e4be nonce:int128 server_nonce:int128 p:bytes q:bytes public_key_fingerprint:long encrypted_data:bytes = Server_DH_Params
@Serializable
@TLRpc(crc32 = 0xd712e4be_u)
public data class TLDHParamsRequest(
    val nonce: Int128,
    val serverNonce: Int128,
    val p: Bytes,
    val q: Bytes,
    val publicKeyFingerprint: Long,
    val encryptedData: Bytes
) : MTProtoRequest<TLDHParamsResponse>

// server_DH_params_ok#d0e8075c nonce:int128 server_nonce:int128 encrypted_answer:bytes = Server_DH_Params
@Serializable
@Crc32(value = 0xd0e8075c_u)
public data class TLDHParamsResponse(
    val nonce: Int128,
    val serverNonce: Int128,
    val encryptedAnswer: Bytes
)

public suspend fun MTProtoClient.getDHParams(
    nonce: Int128,
    serverNonce: Int128,
    p: Bytes,
    q: Bytes,
    publicKeyFingerprint: Long,
    encryptedData: Bytes
): TLDHParamsResponse = execute(
    request = TLDHParamsRequest(
        nonce = nonce,
        serverNonce = serverNonce,
        p = p,
        q = q,
        publicKeyFingerprint = publicKeyFingerprint,
        encryptedData = encryptedData
    )
)
