package ktproto.crypto.aes

import javax.crypto.spec.SecretKeySpec

public val AesKey.keySpec: SecretKeySpec get() = SecretKeySpec(bytes, "AES")
