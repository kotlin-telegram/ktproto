import kotlin.math.absoluteValue
import kotlin.random.Random

private fun main() {
    val time = System.currentTimeMillis()
    val noise = (Random.nextInt() shr 10).absoluteValue.toLong()
    val messageId = time shl 22 or noise

    println(messageId.toULong().toString(radix = 2).padStart(length = 64, padChar = '0'))
}

// 11000101100000011101011000110011001111111
