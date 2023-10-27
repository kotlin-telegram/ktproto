package ktproto.crypto.sha

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import platform.CoreCrypto.CC_SHA256
import platform.CoreCrypto.CC_SHA256_DIGEST_LENGTH

@OptIn(ExperimentalForeignApi::class)
public actual suspend fun ByteArray.sha256(): ByteArray {
    val digest = UByteArray(CC_SHA256_DIGEST_LENGTH)
    this.usePinned { input ->
        digest.usePinned { digest ->
            CC_SHA256(
                input.addressOf(index = 0),
                this.size.convert(),
                digest.addressOf(index = 0)
            )
        }
    }
    return digest.toByteArray()
}
