package intel4004.instruction

import util.UNibble
import util.toInt
import intel4004.Intel4004

class IndexRegInstruction(override val opcode: Int) : Instruction {
    override val name = if(opcode shr 7 == 1) {
        // Index register to accumulator instructions
        InstructionNames.INDEX_REG_ACC_INSTRUCTION_NAMES[(opcode and 0x30) shr 4] // Instruction decided by bits 3 and 4
    } else {
        // One step instructions involving only index register, only includes INC
        "INC"
    }
    private val regNum = UNibble(opcode and 0x0f)
    override fun toString() = "$name $regNum"
    override fun execute(cpu: Intel4004) {
        val reg = cpu.indexRegisters[regNum.value]
        when(name) {
            // ADD - Add Register to Accumulator with Carry
            "ADD" -> {
                val result = reg + cpu.acc + cpu.carry.toInt()
                cpu.carry = reg.value + cpu.acc.value + cpu.carry.toInt() > 15
                cpu.acc = result
            }
            // SUB - Subtract Register from Accumulator with Borrow
            "SUB" -> {
                val result = cpu.acc - reg - cpu.carry.toInt()
                cpu.carry = cpu.acc.value - reg.value  - cpu.carry.toInt() >= 0 // Bit is set if no borrow
                cpu.acc = result
            }
            // LD - Load Accumulator
            "LD" -> {
                cpu.acc = UNibble(reg.value)
            }
            // XCH - Exchange Register and Accumulator
            "XCH" -> {
                val oldAcc = cpu.acc
                cpu.acc = UNibble(reg.value)
                cpu.indexRegisters[regNum.value] = UNibble(oldAcc.value)
            }

            "INC" -> {
                cpu.indexRegisters[regNum.value]++
            }

            else -> {throw Exception("Invalid index register-accumulator instruction")}
        }
        cpu.pc++
    }
}