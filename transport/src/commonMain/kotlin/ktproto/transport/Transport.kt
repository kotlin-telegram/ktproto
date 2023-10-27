package ktproto.transport

import kotlinx.coroutines.CoroutineScope
import ktproto.io.annotation.OngoingConnection
import ktproto.io.input.Input
import ktproto.io.output.Output

/**
 * A real-world transport protocol such as TCP, UDP, Http
 */
@OngoingConnection
public interface Transport {
    public val input: Input
    public val output: Output

    public fun interface Connector {
        public suspend fun connect(scope: CoroutineScope): Transport
    }
}
