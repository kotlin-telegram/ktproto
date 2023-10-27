package ktproto.crypto.aes

import kotlinx.cinterop.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.CoreCrypto.*

@OptIn(ExperimentalForeignApi::class)
public actual suspend fun AesBlock.encrypted(key: AesKey): AesBlock = withContext(Dispatchers.Default) {
    bytes.usePinned { inputBytes ->
        key.bytes.usePinned { keyBytes ->
            memScoped {
                val inputSize = bytes.size

                val dataOut = allocArray<UByteVar>(inputSize)
                val dataOutMoved = alloc<ULongVar>()

                val status = CCCrypt(
                    op = kCCEncrypt,
                    alg = kCCAlgorithmAES,
                    options = kCCOptionECBMode,
                    key = keyBytes.addressOf(index = 0),
                    keyLength = key.bytes.size.convert(),
                    iv = null,
                    dataIn = inputBytes.addressOf(index = 0),
                    dataInLength = inputSize.convert(),
                    dataOut = dataOut,
                    dataOutAvailable = inputSize.convert(),
                    dataOutMoved = dataOutMoved.ptr
                )

                require(dataOutMoved.value.toInt() == inputSize) { "Was moved: ${dataOutMoved.value}, expected: ${bytes.size}" }
                require(status == kCCSuccess) { "Data was not encrypted" }

                val bytes = dataOut.readBytes(inputSize)
                AesBlock(bytes)
            }
        }
    }
}
