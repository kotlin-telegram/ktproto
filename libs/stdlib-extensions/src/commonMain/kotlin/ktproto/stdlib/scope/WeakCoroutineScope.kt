package ktproto.stdlib.scope

import kotlinx.coroutines.*

/**
 * Cancels the scope after [block] execution
 */
public suspend inline fun <T> weakCoroutineScope(
    crossinline block: suspend CoroutineScope.() -> T
): T = coroutineScope {
    val scope = this + Job()
    try {
        block(scope)
    } finally {
        scope.cancel()
    }
}
