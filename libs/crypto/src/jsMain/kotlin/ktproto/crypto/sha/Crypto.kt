package ktproto.crypto.sha

import kotlinx.browser.window
import kotlinx.coroutines.await
import ktproto.stdlib.platform.JsPlatform
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import kotlin.js.Promise

internal external interface Crypto {
    val subtle: Subtle
}

internal external class Subtle {
    @JsName("digest")
    fun digestInterop(algorithm: String, data: ByteArray): Promise<ArrayBuffer>
}

internal suspend fun Subtle.digest(
    algorithm: String,
    data: ByteArray
): ByteArray {
    val buffer = digestInterop(algorithm, data).await()
    return Uint8Array(buffer).unsafeCast<ByteArray>()
}

internal val JsPlatform.crypto: Crypto get() = when (this) {
    JsPlatform.Browser -> window.asDynamic().crypto
    JsPlatform.Node -> eval("require")("crypto")
}.unsafeCast<Crypto?>() ?: throw UnsupportedOperationException("Web Crypto API not available")
