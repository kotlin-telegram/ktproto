package ktproto.transport

import ktproto.stdlib.bytes.decodeInt
import ktproto.transport.exception.TransportException
import kotlin.math.absoluteValue

internal fun ByteArray.throwTransportExceptions() {
    if (size != 4) return
    val int = decodeInt()
    if (int < 0) {
        throw TransportException(code = int.absoluteValue)
    }
}
