package ktproto.crypto.sha

import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class TestSha1 {
    @Test
    fun sha1() {
        runTest {
            val bytes = "TEST".encodeToByteArray().sha1()
            val hex = bytes.joinToString(separator = "") { it.toUByte().toString(radix = 16).padStart(2, '0') }
            require(hex == "984816fd329622876e14907634264e6f332e9fb3")
        }
    }
}
