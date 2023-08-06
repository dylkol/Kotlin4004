package intel4004.instruction

import util.UNibble
import util.toInt
import intel4004.Intel4004

class AccumulatorInstruction(override val opcode: Int) : Instruction {
    override val name = InstructionNames.ACCUMULATOR_INSTRUCTION_NAMES[opcode and 0x0f]

    override fun toString() = name
    override fun execute(cpu: Intel4004) {
        when(name) {
            // CLB - Clear Both
            "CLB" -> {
                cpu.carry = false
                cpu.acc = UNibble(0)
            }
            // CLC - Clear Carry
            "CLC" -> cpu.carry = false
            // IAC - Increment Accumulator
            "IAC" -> {
                cpu.acc++
                cpu.carry = cpu.acc.value == 0
            }
            // CMC - Complement Carry
            "CMC" -> cpu.carry = !cpu.carry
            // CMA - Complement Accumulator
            "CMA" -> cpu.acc = cpu.acc.inv()
            // RAL - Rotate Accumulator Left Through Carry
            "RAL" -> {
                val oldCarry = cpu.carry
                cpu.carry = ((cpu.acc) shr 3).toBoolean()
                cpu.acc = (cpu.acc shl 1) or oldCarry.toInt()
            }
            // RAR - Rotate Accumulator Right Through Carry
            "RAR" -> {
                val oldCarry = cpu.carry
                cpu.carry = ((cpu.acc) and 0x01).toBoolean()
                cpu.acc = (cpu.acc shr 1) or (oldCarry.toInt() shl 3)
            }
            // TCC - Transmit Carry and Clear
            "TCC" -> {
                cpu.acc = UNibble(cpu.carry.toInt())
                cpu.carry = false
            }
            // DAC - Decrement Accumulator
            "DAC" -> {
                cpu.carry = cpu.acc.value > 0 // No carry means borrow
                cpu.acc--
            }
            // TCS - Transfer Carry Subtract
            // Used for decimal subtraction
            "TCS" -> {
                if(!cpu.carry) cpu.acc = UNibble(9)
                else cpu.acc = UNibble(10)
                cpu.carry = false
            }
            // STC - Set Carry
            "STC" -> cpu.carry = true

            // DAA - Decimal Adjust Accumulator
            // Used for decimal addition
            "DAA" -> {
                if(cpu.acc.value > 9 || cpu.carry) {
                    cpu.acc = cpu.acc + 6
                    cpu.carry = true // Either accumulator overflows or carry was already set anyway
                }
            }
            // KBP - Keyboard Process
            // If only one acc bit is set, sets the acc to the position of the set bit, otherwise set to 15
            "KBP" -> {
                when(cpu.acc.value) {
                    0       -> cpu.acc = UNibble(0)
                    1       -> cpu.acc = UNibble(1)
                    2       -> cpu.acc = UNibble(2)
                    4       -> cpu.acc = UNibble(3)
                    8       -> cpu.acc = UNibble(4)
                    else    -> cpu.acc = UNibble(15)
                }
            }

            // DCL - Designate Command Line
            // Selects the RAM bank using the last three bits of the accumulator
            "DCL" -> {
                cpu.controlLines.cmRam.data = cpu.acc and 0x07
            }

            else -> {throw Exception("Invalid accumulator instruction")}
        }
        cpu.pc++
    }
}