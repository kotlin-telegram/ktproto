package ktproto.session

import kotl.time.Clock
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ktproto.io.memory.MemoryArena
import kotlin.math.absoluteValue
import kotlin.random.Random

/**
 * A message is rejected over 300 seconds after it is created or 30 seconds before it is created (this is needed to protect from replay attacks). In this situation, it must be re-sent with a different identifier (or placed in a container with a higher identifier).
 */
public fun interface MessageIdProvider {
    public fun nextMessageId(): MessageId
}

public fun messageIdProvider(clock: Clock): MessageIdProvider =
    DefaultMessageIdProvider(clock)

private class DefaultMessageIdProvider(private val clock: Clock) : MessageIdProvider {
    private var lastMessageId: Long = 0

    override fun nextMessageId(): MessageId {
        val millis = clock.currentTimeMillis()
        val leftShift = Int.SIZE_BITS - MILLIS_BITS
        val noise = (Random.nextInt() shr MILLIS_BITS).absoluteValue.toLong()
        val messageId = millis shl leftShift or noise

        if (messageId <= lastMessageId) {
            return nextMessageId()
        }

        lastMessageId = messageId

        return MessageId(MemoryArena.of(lastMessageId))
    }

    companion object {
        const val MILLIS_BITS = 10
    }
}

public fun main() {

}
