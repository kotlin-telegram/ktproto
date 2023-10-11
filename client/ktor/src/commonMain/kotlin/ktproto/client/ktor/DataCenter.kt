package ktproto.client.ktor

// todo: move file to kotel, since it's telegram, not MTProto
//  ktproto may also be used for TON (lib name kton)

internal expect fun isJS(): Boolean

public interface DataCenter {
    public val name: String
    public val isTest: Boolean
}

public fun DataCenter.websocketUrl(
    isSecure: Boolean = true,
    includeCORS: Boolean = isJS()
): String = url(
    isWebSocket = true,
    isSecure, includeCORS
)

public fun DataCenter.url(
    isWebSocket: Boolean,
    isSecure: Boolean = true,
    includeCORS: Boolean = isJS(),
): String = buildString {
    append("http")
    if (isSecure) append('s')
    append("://$name.web.telegram.org")
    if (isSecure) append(":443") else append(":80")
    append("/api")
//    if (includeCORS)
        append('w')
    if (isWebSocket) append('s')
    if (isTest) append("_test")
}
