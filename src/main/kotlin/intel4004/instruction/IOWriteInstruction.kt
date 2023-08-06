package intel4004.instruction

import intel4004.Intel4004

class IOWriteInstruction(override val opcode: Int) : Instruction {
    override val name = InstructionNames.IO_INSTRUCTION_NAMES[(opcode and 0x0f)] // Instruction decided by last nibble
    init {
        if (name == "UNK") throw Exception("Invalid IO instruction")
    }
    override fun toString() = name
    override fun execute(cpu: Intel4004) {
        cpu.dataBus.data = cpu.acc
        cpu.pc++
    }
}