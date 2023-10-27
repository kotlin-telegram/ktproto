package ktproto.client.ktor

import io.ktor.client.*
import io.ktor.client.plugins.logging.*
import ktproto.client.authorization.createAuthorizationKey
import ktproto.client.ktor.socket.ktorSocketTransport
import ktproto.client.plain.plainMTProtoClient
import ktproto.client.rsa.RsaPublicKey
import ktproto.io.annotation.OngoingConnection
import ktproto.stdlib.scope.weakCoroutineScope

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

// Fingerprint: d09d1d85de64fd85
private val productionKey = RsaPublicKey("""
    -----BEGIN RSA PUBLIC KEY-----
    MIIBCgKCAQEA6LszBcC1LGzyr992NzE0ieY+BSaOW622Aa9Bd4ZHLl+TuFQ4lo4g
    5nKaMBwK/BIb9xUfg0Q29/2mgIR6Zr9krM7HjuIcCzFvDtr+L0GQjae9H0pRB2OO
    62cECs5HKhT5DZ98K33vmWiLowc621dQuwKWSQKjWf50XYFw42h21P2KXUGyp2y/
    +aEyZ+uVgLLQbRA1dEjSDZ2iGRy12Mk5gpYc397aYp438fsJoHIgJ2lgMv5h7WY9
    t6N/byY9Nw9p21Og3AoXSL2q/2IJ1WRUhebgAdGVMlV1fkuOQoEzR7EdpqtQD9Cs
    5+bfo3Nhmcyvk5ftB0WkJ9z6bNZ7yxrP8wIDAQAB
    -----END RSA PUBLIC KEY-----
""".trimIndent())

// Fingerprint: b25898df208d2603
private val testKey = RsaPublicKey("""
    -----BEGIN RSA PUBLIC KEY-----
    MIIBCgKCAQEAyMEdY1aR+sCR3ZSJrtztKTKqigvO/vBfqACJLZtS7QMgCGXJ6XIR
    yy7mx66W0/sOFa7/1mAZtEoIokDP3ShoqF4fVNb6XeqgQfaUHd8wJpDWHcR2OFwv
    plUUI1PLTktZ9uW2WE23b+ixNwJjJGwBDJPQEQFBE+vfmH0JP503wr5INS1poWg/
    j25sIWeYPHYeOrFp/eXaqhISP6G+q2IeTaWTXpwZj4LzXq5YOpk4bYEQ6mvRq7D1
    aHWfYmlEGepfaYR8Q0YqvvhYtMte3ITnuSJs171+GDqpdKcSwHnd6FudwGO4pcCO
    j4WcDuXc2CTHgH8gFTNhp/Y8/SpDOhvn9QIDAQAB
    -----END RSA PUBLIC KEY-----
""".trimIndent())

private val keys = listOf(productionKey, testKey)

@OngoingConnection
private suspend fun main(): Unit = weakCoroutineScope {
    val client = plainMTProtoClient(
        scope = this,
        transport = ktorSocketTransport(
            hostname = "149.154.167.51",
            port = 443
        )
    )
    println("Hostname: 149.154.167.51")
    println("Port: 443")
    repeat(10) {
        createAuthorizationKey(client, 2, keys)
    }
}
