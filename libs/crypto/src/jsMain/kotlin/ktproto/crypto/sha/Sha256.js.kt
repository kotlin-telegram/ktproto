package ktproto.crypto.sha

import ktproto.stdlib.platform.jsRuntime

public actual suspend fun ByteArray.sha256(): ByteArray =
    jsRuntime.crypto.subtle.digest("SHA-256", this)
