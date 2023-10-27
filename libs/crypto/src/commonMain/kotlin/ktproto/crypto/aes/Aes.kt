package ktproto.crypto.aes

public expect suspend fun AesBlock.encrypted(key: AesKey): AesBlock
