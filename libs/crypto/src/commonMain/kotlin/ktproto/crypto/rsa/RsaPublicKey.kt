package ktproto.crypto.rsa

import ktproto.crypto.asn1.parseAsn1Der
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

public data class RsaPublicKey(
    public val n: ByteArray,
    public val e: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RsaPublicKey) return false

        if (!n.contentEquals(other.n)) return false
        if (!e.contentEquals(other.e)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = n.contentHashCode()
        result = 31 * result + e.contentHashCode()
        return result
    }
}

@OptIn(ExperimentalEncodingApi::class)
public fun RsaPublicKey(string: String): RsaPublicKey {
    val publicKeyString = string
        .replace("-----BEGIN RSA PUBLIC KEY-----\n", "")
        .replace("-----END RSA PUBLIC KEY-----", "")
        .replace("\n", "")
        .trim()

    val publicKeyBytes = Base64.decode(publicKeyString)
    return RsaPublicKey(publicKeyBytes)
}

public fun RsaPublicKey(bytes: ByteArray): RsaPublicKey {
    val asn1 = try {
        parseAsn1Der(bytes)
    } catch (throwable: Throwable) {
        throwable.printStackTrace()
        error("Invalid public key. Cause: ${throwable.message}")
    }

    val modulus = asn1.children.orEmpty().getOrNull(index = 0)?.bytes
    val publicExponent = asn1.children.orEmpty().getOrNull(index = 1)?.bytes

    if (modulus == null || publicExponent == null) {
        error("Invalid public key")
    }

    return RsaPublicKey(
        // removing sign-byte
        n = modulus.drop(1).toByteArray(),
        e = publicExponent
    )
}
