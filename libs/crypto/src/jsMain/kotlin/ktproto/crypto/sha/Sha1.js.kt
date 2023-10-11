package ktproto.crypto.sha

import ktproto.stdlib.platform.platform

public actual suspend fun ByteArray.sha1(): ByteArray =
    platform.crypto.subtle.digest("SHA-1", this)
