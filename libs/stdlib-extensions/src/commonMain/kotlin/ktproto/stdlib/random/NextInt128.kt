package ktproto.stdlib.random

import kotl.serialization.int.Int128
import kotlin.random.Random

public fun Random.nextInt128(): Int128 = Int128(
    data = intArrayOf(
        nextInt(),
        nextInt(),
        nextInt(),
        nextInt()
    )
)
