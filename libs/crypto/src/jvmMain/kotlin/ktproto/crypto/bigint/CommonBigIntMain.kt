package ktproto.crypto.bigint

import ktproto.stdlib.bigint.toBigInt
import ktproto.stdlib.bytes.toBinaryString
import java.math.BigInteger
import kotlin.random.Random
import kotlin.random.nextUBytes

@OptIn(ExperimentalUnsignedTypes::class)
public fun main() {
    repeat(10_000_000) {
        val small = Random.nextLong()
        val int = small.toBigInt()
        val jint = BigInteger.valueOf(small)
        require(int.toUByteArray() contentEquals jint.toByteArray().toUByteArray()) { "$small: expected: ${int.toUByteArray().toBinaryString()}, was ${jint.toByteArray().toBinaryString()}" }
    }
    repeat(10_000_000) {
        val bytes = Random.nextUBytes((1..8).random())
        val int = bytes.toBigInt()
        val jint = BigInteger(bytes.toByteArray())
        val jintString = buildString {
            if (jint < BigInteger.ZERO) append('-')
            append("0x")
            append(jint.abs().toString(16))
        }
        require(int.toString() == jintString) { "${bytes.toBinaryString()}: expected: $jintString, was: $int" }
    }
//    repeat(10_000) {
//        val long = Long.MIN_VALUE
//        val bigInt = BigInteger.valueOf(long)
//        println("Java: ${bigInt.toString(16)}")
//        val bytes = bigInt.toByteArray()
//        print("Bytes: ")
//        println(bytes.joinToString(" ") { byte -> byte.toUByte().toString(radix = 2).padStart(8, '0') })
////        val kBigInt = bytes.toBigInt()
////        println("Kotlin: $kBigInt")
//    }
}
