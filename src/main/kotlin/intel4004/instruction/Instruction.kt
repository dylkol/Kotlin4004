package intel4004.instruction

import intel4004.Intel4004

interface Instruction {
    val opcode: Int
    val name: String
    fun execute(cpu: Intel4004)
    override fun toString(): String
}

interface TwoStepInstruction: Instruction {
    override var opcode: Int
    fun addSecondByte(opcodeSecondByte: Int)
}

class NOPInstruction : Instruction {
    override val opcode = 0
    override val name = "NOP"
    override fun execute(cpu: Intel4004) {
        cpu.pc++
    }
    override fun toString() = name
}