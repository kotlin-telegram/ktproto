package ktproto.crypto.sha

import ktproto.stdlib.platform.jsRuntime

public actual suspend fun ByteArray.sha1(): ByteArray =
    jsRuntime.crypto.subtle.digest("SHA-1", this)
