package ktproto.crypto.sha

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

public actual suspend fun ByteArray.sha1(): ByteArray {
    val input = this
    return withContext(Dispatchers.Default) {
        val messageDigest = java.security.MessageDigest.getInstance("SHA-1")
        val hashBytes = messageDigest.digest(input)
        hashBytes
    }
}
