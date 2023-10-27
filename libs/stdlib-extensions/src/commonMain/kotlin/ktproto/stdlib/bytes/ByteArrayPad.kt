package ktproto.stdlib.bytes

import kotlin.jvm.JvmInline

@JvmInline
public value class ByteArrayPad(
    public val upstream: ByteArray
) {
    public operator fun get(index: Int): Byte =
        if (index in upstream.indices) {
            upstream[index]
        } else {
            0
        }
}
