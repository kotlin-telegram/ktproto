package ktproto.stdlib.platform

public sealed interface JsPlatform {
    public data object Node : JsPlatform
    public data object Browser : JsPlatform
}

public val jsRuntime: JsPlatform by lazy {
    if (js("typeof window") === "undefined") {
        JsPlatform.Node
    } else {
        JsPlatform.Browser
    }
}
