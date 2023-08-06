package intel4004.instruction

import intel4004.Intel4004
import util.toInt

class IOReadInstruction(override val opcode: Int) : Instruction {
    override val name = InstructionNames.IO_INSTRUCTION_NAMES[(opcode and 0x0f)] // Instruction decided by last nibble
    init {
        if (name == "UNK") throw Exception("Invalid IO instruction")
    }
    override fun toString() = name
    override fun execute(cpu: Intel4004) {
        when (name) {
            "SBM" -> {
                val result = cpu.acc - cpu.dataBus.data - cpu.carry.toInt()
                cpu.carry = cpu.acc.value - cpu.dataBus.data.value  - cpu.carry.toInt() >= 0 // Bit is set if no borrow
                cpu.acc = result
            }
            "RDM", "RDR", "RD0", "RD1", "RD2", "RD3" -> {
                cpu.acc = cpu.dataBus.data
            }
            "ADM" -> {
                val result = cpu.dataBus.data + cpu.acc + cpu.carry.toInt()
                cpu.carry = cpu.dataBus.data.value + cpu.acc.value + cpu.carry.toInt() > 15
                cpu.acc = result
            }
        }
        cpu.pc++
    }
}