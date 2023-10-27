package ktproto.crypto.aes

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.crypto.Cipher

public actual suspend fun AesBlock.encrypted(key: AesKey): AesBlock =
    withContext(Dispatchers.IO) {
        val cipher = Cipher.getInstance("AES/ECB/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key.keySpec)
        val bytes = cipher.doFinal(bytes)
        AesBlock(bytes)
    }
