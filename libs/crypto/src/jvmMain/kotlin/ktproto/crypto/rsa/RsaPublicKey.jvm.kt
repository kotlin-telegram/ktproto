package ktproto.crypto.rsa

import java.math.BigInteger
import java.security.spec.RSAPublicKeySpec

public val RsaPublicKey.keySpec: RSAPublicKeySpec get() =
    RSAPublicKeySpec(BigInteger(modulus), BigInteger(publicExponent))
