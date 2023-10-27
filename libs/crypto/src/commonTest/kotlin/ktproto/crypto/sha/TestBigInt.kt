package ktproto.crypto.sha

import kotlinx.coroutines.test.runTest
import ktproto.stdlib.bigint.modPow
import ktproto.stdlib.bytes.encodeToByteArray
import kotlin.test.Test

class TestBigInt {
    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testRsa() = runTest {
        val bigint = 1_000.encodeToByteArray().bi
        val exponent = byteArrayOf(0x01, 0x00, 0x01).bi
        val modulus = ByteArray(256) { 1 }.bi

        val result = bigint.modPow(exponent, modulus).toByteArray().toHexString()
        println(result)
        require(result == "00dcf3602fb8078f2860fe1c9276ab38c2238fde7188b300f2eaddb17ba733e0bed34fb4ebcc945748349e50386d90f6a46ae769814287e44d34407a72d95657dc5e963897d5fc73413118df10fdc8a6c4cfd0fbe1c7fff2a8b42746e3681dbb7f84ae5d37b9727b9da9ef023b4ca2cbd70e09c444b9f60a244be6e45330c5d822aa8a9e6dd2879e013a1fb136746648c55db7fc441175191d2e401fc84d33e5597f1a1ac402a054c2e58954de0f79c96efb10bee0fdabf0519b4cbbe3a0bac65039d4592f3a8303eebee2aae44c0dd0002402c64df6738e81d74993ae34dd31541ebbe6d0c11fd6dbd006379bb33292891a0c7dcc5093d265d243365c14bec4")
    }
}
