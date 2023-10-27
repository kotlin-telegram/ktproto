package ktproto.crypto.sha

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest

public actual suspend fun ByteArray.sha256(): ByteArray {
    val input = this
    return withContext(Dispatchers.Default) {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hashBytes = messageDigest.digest(input)
        hashBytes
    }
}
