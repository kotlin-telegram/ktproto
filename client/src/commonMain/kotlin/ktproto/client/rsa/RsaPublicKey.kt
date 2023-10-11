package ktproto.client.rsa

import kotl.serialization.TL
import kotl.serialization.bytes.Bytes
import kotlinx.serialization.encodeToByteArray
import ktproto.crypto.sha.sha1
import ktproto.io.memory.MemoryArena
import ktproto.io.memory.drop
import ktproto.io.memory.scanLong
import kotl.serialization.bare.bare
import ktproto.crypto.rsa.RsaPublicKey as InternalRsaPublicKey

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

public fun RsaPublicKey(string: String): RsaPublicKey {
    val (n, e) = InternalRsaPublicKey(string)
    return RsaPublicKey(n, e)
}

public fun RsaPublicKey(bytes: ByteArray): RsaPublicKey {
    val (n, e) = InternalRsaPublicKey(bytes)
    return RsaPublicKey(n, e)
}

public suspend fun RsaPublicKey.fingerprint(): Long {
    val n = Bytes(n)
    val e = Bytes(e)
    val key = TLRsaPublicKey(n, e).bare
    val bytes = TL.encodeToByteArray(key)
    val sha1 = MemoryArena.of(bytes.sha1())
    return sha1.drop(n = 12).scanLong()
}
