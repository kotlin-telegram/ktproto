package ktproto.stdlib.int

public fun Int.nearestMultipleOf(n: Int): Int {
    return if (this <= 0) 0 else ((this - 1) / n + 1) * n
}
