package ktproto.stdlib.bit

import kotlin.jvm.JvmInline

@JvmInline
public value class Bit(public val enabled: Boolean) {
    public fun toInt(): Int = if (enabled) 1 else 0

    override fun toString(): String = "Bit[${toInt()}]"

    public companion object {
        public val Enabled: Bit = true.bit
        public val Disabled: Bit = false.bit
    }
}

public val Boolean.bit: Bit get() = Bit(enabled = this)
