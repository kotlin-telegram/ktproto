package kotl.client.ktor.socket

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import ktproto.io.annotation.OngoingConnection
import ktproto.io.input.Input
import ktproto.io.memory.*
import ktproto.io.output.Output
import ktproto.transport.Transport
import ktproto.transport.exception.IOException
import ktproto.transport.exception.throwIO

@OngoingConnection
public suspend fun openKtorSocketTransport(
    hostname: String,
    port: Int
): Transport = runCatching {
    val manager = SelectorManager(Dispatchers.IO)
    val socket = aSocket(manager).tcp().connect(hostname, port)
    KtorSocketTransport(socket)
}.getOrElse { cause ->
    cause.throwIO()
}

@OngoingConnection
public class KtorSocketTransport(
    socket: Socket
) : Transport {
    private val readChannel = socket.openReadChannel()
    private val writeChannel = socket.openWriteChannel(autoFlush = true)

    override val input: Input = Input { destination ->
        runCatching { readToMemory(destination) }
            .getOrElse { cause -> cause.throwIO() }
    }
    override val output: Output = Output { source ->
        runCatching { writeFromMemory(source) }
            .getOrElse { cause -> cause.throwIO() }
    }

    private suspend fun readToMemory(destination: MemoryArena) {
        readChannel.readFully(destination.data, destination.start, destination.size)
    }
    private suspend fun writeFromMemory(source: MemoryArena) {
        writeChannel.writeFully(source.data, source.start, source.size)
    }
}
