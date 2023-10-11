package ktproto.crypto.factorization

import kotlin.math.min
import kotlin.random.Random
import kotlin.random.nextULong

public object PollardRhoBrent {
    // Code was rewritten to Kotlin from CPP:
    // https://github.com/tdlib/td/blob/master/tdutils/td/utils/crypto.cpp#L103
    public fun factorize(pq: ULong): Pair<ULong, ULong> {
        if (pq and 1uL == 0uL) {
            return 2uL to pq / 2uL
        }

        var g = 0uL

        var i = 0
        var iter = 0

        while (i < 3 || iter < 1000) {
            val q = Random.nextULong(from = 17uL, until = 32u) % (pq - 1uL)
            var x = Random.nextULong() % (pq - 1uL)
            var y = x
            val lim = 1 shl (min(5, i) + 18)
            for (j in 1..<lim) {
                iter++
                x = pqAddMul(q, x, x, pq)
                val z = if (x < y) pq + x - y else x - y
                g = gcd(z, pq)
                if (g != 1uL) break
                if (j and (j - 1) == 0) {
                    y = x
                }
            }
            if (g > 1uL && g < pq) {
                break
            }
            i++
        }

        val p: ULong
        val q: ULong

        if (g == 0uL) return 1uL to pq

        val other = pq / g

        if (other < g) {
            p = other
            q = g
        } else {
            p = g
            q = other
        }

        return p to q
    }
}

private fun pqAddMul(
    c: ULong,
    a: ULong,
    b: ULong,
    pq: ULong
): ULong {
    var cVar = c
    var aVar = a
    var bVar = b

    while (bVar != 0uL) {
        if ((bVar and 1uL) == 1uL) {
            cVar += aVar
            if (cVar >= pq) {
                cVar -= pq
            }
        }
        aVar += aVar
        if (aVar >= pq) {
            aVar -= pq
        }
        bVar = bVar shr 1
    }

    return cVar
}

private tailrec fun gcd(a: ULong, b: ULong): ULong {
    if (b == 0uL) return a
    return gcd(b, a % b)
}

private inline fun repeat(n: ULong, block: (ULong) -> Unit) {
    for (i in 0uL..<n) {
        block(i)
    }
}
