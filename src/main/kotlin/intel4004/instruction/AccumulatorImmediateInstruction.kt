package intel4004.instruction

import util.UNibble
import intel4004.Intel4004

class AccumulatorImmediateInstruction(override val opcode: Int) : Instruction {
    override val name = InstructionNames.ACCUMULATOR_IMMEDIATE_INSTRUCTION_NAMES[(opcode shr 4) and 0x01] // Decided by bit 4

    val data = UNibble(opcode and 0x0f)

    override fun toString() = "$name $data"
    override fun execute(cpu: Intel4004) {
        if(name == "BBL") {
            cpu.sp = (cpu.sp-1 + 3) % 3
            cpu.pc = cpu.stack[cpu.sp]
            cpu.acc = data
        } else if(name == "LDM") {
            cpu.acc = data
            cpu.pc++
        }
    }
}