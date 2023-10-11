package ktproto.client.rsa

import kotl.serialization.bytes.Bytes
import kotlinx.serialization.Serializable

@Serializable
public class TLRsaPublicKey(
    public val n: Bytes,
    public val e: Bytes
)
