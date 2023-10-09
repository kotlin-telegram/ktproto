package kotl.crypto.dh


import ktproto.stdlib.int.*
import kotlin.random.Random

public object PollardRhoBrent {
    public fun factorize(pq: BigInt): Pair<BigInt, BigInt> {
        if (pq % 2.bi == 0.bi) return Pair(2.bi, pq / 2.bi)

        var y = Random.nextBigInt(1.bi, pq - 1.bi)
        var c = Random.nextBigInt(1.bi, pq - 1.bi)
        var m = Random.nextBigInt(1.bi, pq - 1.bi)

        var g = 1.bi
        var r = 1.bi
        var q = 1.bi

        var ys: BigInt = 0.bi

        while (g == 1.bi) {
            val x = y
            repeat(r) {
                y = ((y * y) % pq + c) % pq
            }
            var k = 0.bi
            while (k < r && g == 1.bi) {
                ys = y
                repeat(min(m, r - k)) {
                    y = ((y * y) % pq + c) % pq
                    q = q * (abs(x - y)) % pq
                }
                g = gcd(q, pq)
                k += m
            }
            r *= r
        }

        if (g == pq) {
            while (true) {
                ys = ((ys * ys) % pq + c) % pq
                g = gcd(abs(y - ys), pq)
                if (g > 1.bi) break
            }
        }

        // p, q
        return g to pq / g
    }

    private tailrec fun gcd(a: BigInt, b: BigInt): BigInt {
        if (b == 0.bi) return a
        return gcd(b, a % b)
    }
}
