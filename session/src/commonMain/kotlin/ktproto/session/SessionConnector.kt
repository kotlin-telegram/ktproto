package ktproto.session

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ktproto.io.annotation.OngoingConnection


/**
 * This class automatically handles and relaunches session when closed
 */
@OptIn(OngoingConnection::class)
public class SessionConnector(
    private val factory: Factory,
    private val scope: CoroutineScope
) {
    private val mutex = Mutex()

    private var session: MTProtoSession? = null
    private val messages = MutableSharedFlow<Result<MTProtoSession.Message>>()

    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun ensureSessionActive(): MTProtoSession = mutex.withLock {
        var session = session

        if (
            session == null ||
            session.incoming.isClosedForReceive &&
            session.outgoing.isClosedForSend
        ) {
            session = factory.connect()
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

    /**
     * When [transform] returns not-null value, that value is returned
     */
    public suspend fun <T : Any> sendRequest(
        request: MTProtoSession.Message,
        transform: (MTProtoSession.Message) -> T?
    ): T = coroutineScope {
        val session = ensureSessionActive()
        val response = async(start = CoroutineStart.UNDISPATCHED) {
            messages
                .mapNotNull { transform(it.getOrThrow()) }
                .first()
        }
        session.outgoing.send(request)
        response.await()
    }

    public fun interface Factory {
        public suspend fun connect(): MTProtoSession
    }
}
