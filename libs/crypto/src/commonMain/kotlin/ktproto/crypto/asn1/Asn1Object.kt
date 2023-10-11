package ktproto.crypto.asn1

public sealed interface Asn1Object {
    public val bytes: ByteArray?
    public val children: List<Asn1Object>?

    public data class Container(
        override val children: List<Asn1Object>
    ) : Asn1Object {
        override val bytes: Nothing? = null
    }

    public data class Value(
        override val bytes: ByteArray?
    ) : Asn1Object {
        override val children: Nothing? = null

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Value) return false

            if (bytes != null) {
                if (other.bytes == null) return false
                if (!bytes.contentEquals(other.bytes)) return false
            } else if (other.bytes != null) return false

            return true
        }

        override fun hashCode(): Int {
            return bytes?.contentHashCode() ?: 0
        }
    }
}
