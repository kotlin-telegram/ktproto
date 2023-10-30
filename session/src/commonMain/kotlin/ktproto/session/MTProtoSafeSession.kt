package ktproto.session

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ktproto.io.annotation.OngoingConnection
import ktproto.transport.exception.throwIO


/**
 * TODO: fix error-reporting, now it does not update stacktrace
 *
 * This class automatically handles and relaunches session when closed
 */
@OptIn(OngoingConnection::class)
public class MTProtoSafeSession(
    private val connector: MTProtoSession.Connector,
    private val scope: CoroutineScope
) {
    private val requestsScope = scope + SupervisorJob()
    private val mutex = Mutex()

    private var session: MTProtoSession? = null
    private val messages = MutableSharedFlow<Result<MTProtoSession.Message>>()
    private var lastScope = scope + Job()

    // todo: connection not before trying to make request
    //  but on a separate background coroutine that is constantly
    //  monitoring job state
    // todo v2: or maybe not
    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun ensureSessionActive(): MTProtoSession = mutex.withLock {
        var session = session

        if (
            session == null ||
            session.incoming.isClosedForReceive &&
            session.outgoing.isClosedForSend
        ) {
            lastScope.cancel()
            lastScope = scope + Job()
            session = connector.connect(lastScope)
            collectMessages(session)
        }

        this.session = session
        session
    }

    private suspend fun collectMessages(session: MTProtoSession) {
        scope.launch {
            try {
                for (message in session.incoming) {
                    messages.emit(Result.success(message))
                }
            } catch (throwable: Throwable) {
                messages.emit(Result.failure(throwable))
            }
        }
    }

    public suspend fun connect() {
        ensureSessionActive()
    }

    /**
     * When [transform] returns not-null value, that value is returned
     */
    public suspend fun <T : Any> sendRequest(
        request: MTProtoSession.Message,
        transform: (MTProtoSession.Message) -> T?
    ): T {
        val session = ensureSessionActive()
        val response = requestsScope.async(start = CoroutineStart.UNDISPATCHED) {
            messages
                .mapNotNull { result ->
                    result.map { message ->
                        transform(message) ?: return@mapNotNull null
                    }
                }
                .first()
        }
        session.outgoing.send(request)
        val result = response.await().getOrElse { throwable -> throwable.throwIO() }
        return result
    }
}
