package ktproto.crypto.sha

import kotlinx.coroutines.test.runTest
import ktproto.crypto.aes.AesIV
import ktproto.crypto.aes.AesKey
import ktproto.crypto.aes.encryptAesIge
import ktproto.stdlib.bytes.padEnd
import ktproto.stdlib.int.nearestMultipleOf
import kotlin.test.Test

class TestAes {

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testAes256Block() = runTest {
        val data = "TEST".encodeToByteArray()
        val padded = data.padEnd(data.size.nearestMultipleOf(n = 16))
        val key = AesKey.Bits256(ByteArray(size = 32))
        val result = padded.encryptAesIge(key, AesIV.Zero)
        val expected = "2971b020a822d866bd58200c2584cac3".hexToByteArray()
        require(result contentEquals expected)
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testAes256Ide() = runTest {
        val data = "TEST".repeat(n = 10).encodeToByteArray()
        val padded = data.padEnd(data.size.nearestMultipleOf(n = 16))
        val key = AesKey.Bits256(ByteArray(size = 32))
        val result = padded.encryptAesIge(key, AesIV.Zero)
        val expected = "ba4d0f84256c348c44db134d7c8b6e46d107a8c504ff15a99b8169228a07637d9c1eaae7c3cbbfcfe5d726dec14ec23a".hexToByteArray()
        require(result contentEquals expected)
    }
}
