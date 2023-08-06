package util

fun Boolean.toInt(): Int = if (this) 1 else 0
fun Boolean.toByte(): Byte = if (this) 1 else 0

fun Number.toBoolean() = this != 0

// A 4 bit unsigned value
@JvmInline
value class UNibble(val value: Int) {
    init {
        require(value in 0..15) {"Value does not fit into nibble"}
    }

    operator fun unaryPlus() = UNibble(value)
    operator fun unaryMinus() = UNibble(16-value)

    operator fun inc() = UNibble((value+1)%16)
    operator fun dec() = UNibble((value+(16-1))%16) // Subtraction is addition plus a negated value

    operator fun plus(b: Int) = UNibble((value+b)%16)
    operator fun plus(b: UNibble) = UNibble((value+b.value)%16)

    operator fun minus(b: Int) = UNibble((value+(16-b))%16)
    operator fun minus(b: UNibble) = UNibble((value+(16-b.value))%16)

    operator fun times(b: Int) = UNibble((value*b)%16)
    operator fun times(b: UNibble) = UNibble((value*b.value)%16)

    operator fun div(b: Int) = UNibble((value/b))
    operator fun div(b: UNibble) = UNibble((value/b.value))

    operator fun rem(b: Int) = UNibble((value%b))
    operator fun rem(b: UNibble) = UNibble((value%b.value))


    fun inv() = UNibble((15-value))

    infix fun shl(b: Int): UNibble = UNibble((value shl b) and 0x0f)
    infix fun shl(b: UNibble): UNibble = UNibble((value shl b.value) and 0x0f)
    infix fun shr(b: Int): UNibble = UNibble((value shr b) and 0x0f)
    infix fun shr(b: UNibble): UNibble = UNibble((value shr b.value) and 0x0f)

    infix fun and(b: Int): UNibble = UNibble((value and b))
    infix fun and(b: UNibble): UNibble = UNibble((value and b.value))

    infix fun or(b: Int): UNibble = UNibble((value or b) and 0x0f)
    infix fun or(b: UNibble): UNibble = UNibble((value or b.value) and 0x0f)

    infix fun xor(b: Int): UNibble = UNibble((value xor b) and 0x0f)
    infix fun xor(b: UNibble): UNibble = UNibble((value xor b.value) and 0x0f)

    fun toBoolean(): Boolean = value != 0
    override fun toString(): String = value.toString()
}

// A 12 bit unsigned value
@JvmInline
value class UInt12(val value: Int) {
    init {
        require(value in 0..0xffe) {"Value does not fit into 12 bits"}
    }

    operator fun unaryPlus() = UInt12(value)
    operator fun unaryMinus() = UInt12(0xfff-value)

    operator fun inc() = UInt12((value+1)%0xfff)
    operator fun dec() = UInt12((value+(0xfff-1))%0xfff) // Subtraction is addition plus a negated value

    operator fun plus(b: Int) = UInt12((value+b)%0xfff)
    operator fun plus(b: UInt12) = UInt12((value+b.value)%0xfff)

    operator fun minus(b: Int) = UInt12((value+(0xfff-b))%0xfff)
    operator fun minus(b: UInt12) = UInt12((value+(0xfff-b.value))%0xfff)

    operator fun times(b: Int) = UInt12((value*b)%0xfff)
    operator fun times(b: UInt12) = UInt12((value*b.value)%0xfff)

    operator fun div(b: Int) = UInt12((value/b))
    operator fun div(b: UInt12) = UInt12((value/b.value))

    operator fun rem(b: Int) = UInt12((value%b))
    operator fun rem(b: UInt12) = UInt12((value%b.value))

    infix fun or(b: Int): UInt12 = UInt12((value or b) and 0xfff)
    infix fun or(b: UInt12): UInt12 = UInt12((value or b.value))

    infix fun and(b: Int): UInt12 = UInt12((value and b))
    infix fun and(b: UInt12): UInt12 = UInt12((value and b.value))

    infix fun shl(b: Int): UInt12 = UInt12((value shl b) and 0xfff)
    infix fun shl(b: UInt12): UInt12 = UInt12((value shl b.value) and 0xfff)
    infix fun shr(b: Int): UInt12 = UInt12((value shr b) and 0xfff)
    infix fun shr(b: UInt12): UInt12 = UInt12((value shr b.value) and 0xfff)
    override fun toString(): String = value.toString()
}
