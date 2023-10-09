package kotl.client.ktor.websocket

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import ktproto.io.annotation.OngoingConnection
import ktproto.io.input.Input
import ktproto.io.memory.*
import ktproto.io.output.Output
import ktproto.transport.Transport
import kotlin.math.max

@OngoingConnection
public suspend fun connectKtorWebsocketTransport(
    httpClient: HttpClient,
    urlString: String,
    scope: CoroutineScope,
    request: HttpRequestBuilder.() -> Unit = {}
): Transport {
    val deferred = CompletableDeferred<Transport>()

    scope.launch {
        ktorWebsocketTransport(httpClient, urlString, request) { transport ->
            deferred.complete(transport)
            awaitCancellation()
        }
    }

    return deferred.await()
}

@OptIn(OngoingConnection::class)
public suspend inline fun ktorWebsocketTransport(
    httpClient: HttpClient,
    urlString: String,
    crossinline request: HttpRequestBuilder.() -> Unit = {},
    crossinline block: suspend (KtorWebsocketTransport) -> Unit
) {
    httpClient.config {
        install(WebSockets)
    }.webSocket(
        urlString = urlString,
        request = {
            url {
                protocol = if (it.protocol.isSecure()) URLProtocol.WSS else URLProtocol.WS
            }
            header(HttpHeaders.SecWebSocketProtocol, "binary")
            request()
        }
    ) {
        val transport = KtorWebsocketTransport(webSocket = this)
        block(transport)
    }
}

@OngoingConnection
public class KtorWebsocketTransport(
    private val webSocket: DefaultClientWebSocketSession
) : Transport {
    override val input: Input = Input { destination -> readToMemory(destination) }
    override val output: Output = Output { source -> writeFromMemory(source) }

    private var remaining: MemoryArena = MemoryArena.allocate(n = 0)

    private suspend fun readToMemory(destination: MemoryArena) {
        val cachedMemory = remaining

        val dataMemory = if (cachedMemory.size >= destination.size) {
            cachedMemory
        } else {
            val socketBytes = webSocket.incoming.receive().data
            MemoryArena
                .allocate(n = cachedMemory.size + socketBytes.size)
                .write(cachedMemory)
                .write(socketBytes)
        }

        val maxSize = max(destination.size, dataMemory.size)
        this.remaining = dataMemory.drop(maxSize)

        if (dataMemory.size < destination.size) {
            val remaining = destination.write(dataMemory)
            return readToMemory(remaining)
        }

        destination.write(dataMemory.take(destination.size))
    }

    private suspend fun writeFromMemory(source: MemoryArena) {
        if (source.size <= webSocket.maxFrameSize) {
            return webSocket.outgoing.send(
                element = Frame.Binary(
                    fin = true,
                    data = source.toByteArray()
                )
            )
        }

        val maxFrame = source.take(webSocket.maxFrameSize.toInt())
        webSocket.outgoing.send(
            element = Frame.Binary(
                fin = false,
                data = maxFrame.toByteArray()
            )
        )
        webSocket.flush()
        return writeFromMemory(source.drop(webSocket.maxFrameSize.toInt()))
    }
}
