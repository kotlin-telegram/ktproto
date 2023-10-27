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

internal external interface CryptoKey

internal external class Subtle {
    // https://developer.mozilla.org/en-US/docs/Web/API/SubtleCrypto/digest
    @JsName("digest")
    fun digestInterop(algorithm: dynamic, data: ByteArray): Promise<ArrayBuffer>
    // https://developer.mozilla.org/en-US/docs/Web/API/SubtleCrypto/importKey
    @JsName("importKey")
    fun importKeyInterop(
        format: String,
        key: dynamic,
        algorithm: dynamic,
        extractable: Boolean,
        usages: Array<String>
    ): Promise<CryptoKey>
    // https://developer.mozilla.org/en-US/docs/Web/API/SubtleCrypto/encrypt
    @JsName("encrypt")
    fun encryptInterop(algorithm: dynamic, key: CryptoKey, data: ByteArray): Promise<ArrayBuffer>
}

internal suspend fun Subtle.digest(algorithm: dynamic, data: ByteArray): ByteArray {
    val buffer = digestInterop(algorithm, data).await()
    return Uint8Array(buffer).unsafeCast<ByteArray>()
}

internal suspend fun Subtle.encrypt(algorithm: dynamic, key: CryptoKey, data: ByteArray): ByteArray {
    val buffer = encryptInterop(algorithm, key, data).await()
    return Uint8Array(buffer).unsafeCast<ByteArray>()
}

internal suspend fun Subtle.importKey(
    format: String,
    key: dynamic,
    algorithm: dynamic,
    extractable: Boolean,
    usages: List<String>
): CryptoKey = importKeyInterop(format, key, algorithm, extractable, usages.toTypedArray()).await()

internal val JsPlatform.crypto: Crypto get() = when (this) {
    JsPlatform.Browser -> window.asDynamic().crypto
    JsPlatform.Node -> eval("require")("crypto")
}.unsafeCast<Crypto?>() ?: throw UnsupportedOperationException("Web Crypto API not available")
