# ktproto - Kotlin Library for MTProto Protocol

⚠️ ALL OF THIS IS WORK IN PROGRESS ⚠️

`ktproto` is a Kotlin library designed to simplify working with Telegram's MTProto protocol. This library provides the tools you need to establish connections, perform authentication, and interact with the Telegram API using the MTProto protocol.

## Features

- Establish connections to Telegram's servers.
- Interact with the Telegram API using MTProto protocol.
- Built-in integration with TL (Maintained in a separate repo: https://github.com/kotlin-telegram/ktproto)

## Usage

```kotlin
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
    repeat(10) {
        createAuthorizationKey(client, keys)
    }
    // Sending encrypted requests are not supported ATM
}
```
