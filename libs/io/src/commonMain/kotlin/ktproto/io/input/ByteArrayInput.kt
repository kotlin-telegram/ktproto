package ktproto.io.input

import ktproto.io.annotation.OngoingConnection
import ktproto.io.memory.*

@OptIn(OngoingConnection::class)
public class ByteArrayInput(private var source: MemoryArena) : Input {

    override suspend fun read(destination: MemoryArena) {
        if (destination.size > source.size)
            throw IndexOutOfBoundsException()
        destination.write(source.take(destination.size))
        source = source.drop(destination.size)
    }
}
