import ktproto.stdlib.bytes.decodeInt
import ktproto.stdlib.bytes.encodeToByteArray
import ktproto.stdlib.int.BigInt
import kotlin.random.Random
import kotlin.test.Test

class BigIntTest {
    @Test
    fun `creationOfBbigint`() {
//        val number = Random.nextInt()
        val number = 1024
        val bytes = number.encodeToByteArray()
        println(bytes)
        val bigInt = BigInt.ofLE(bytes)
        println(bigInt)
        val decoded = bigInt.toByteArrayLE().decodeInt()
        println(decoded)
        require(number == decoded) { "number: $number, decoded: $decoded" }
    }
}
