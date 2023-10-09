package kotl.client.ktor

import io.ktor.client.*
import io.ktor.client.plugins.logging.*
import kotl.client.ktor.socket.openKtorSocketTransport
import kotl.serialization.int.Int128
import kotlinx.coroutines.*
import ktproto.client.authorization.createAuthorizationKey
import ktproto.client.plain.plainMTProtoClient
import ktproto.client.requests.TLRequestPQ
import ktproto.client.serialization.execute
import ktproto.io.annotation.OngoingConnection
import ktproto.stdlib.scope.weakCoroutineScope
import kotlin.random.Random

private object DC : DataCenter {
    override val name: String = "pluto"
    override val isTest: Boolean = false
}

private val httpClient = HttpClient {
    Logging {
        level = LogLevel.ALL
        logger = object : Logger {
            override fun log(message: String) = println(message)
        }
    }
}

@OngoingConnection
private suspend fun main(): Unit = weakCoroutineScope {
    val transport = openKtorSocketTransport(
        hostname = "149.154.167.51",
        port = 443
    )
    val client = plainMTProtoClient(
        transport = transport,
        scope = this
    )
    createAuthorizationKey(client)
}
