package intel4004.instruction

import intel4004.Intel4004
import util.UInt12

class DirectJumpInstruction(override var opcode: Int): TwoStepInstruction {
    override val name = InstructionNames.DIRECT_JUMP_INSTRUCTION_NAMES[(opcode shr 4) and 0x01] // Decided by bit 4
    private var addr = UInt12(0)
    private var secondStepDone = false
    override fun addSecondByte(opcodeSecondByte: Int) {
        opcode = (opcode shl 8) or opcodeSecondByte
        addr = UInt12(opcode and 0x0fff) // Last 3 nibbles denote address
        secondStepDone = true
    }

    override fun toString(): String = if (secondStepDone) "$name $addr" else "$name ?"
    override fun execute(cpu: Intel4004) {
        if(!cpu.secondStep) {
            cpu.pc++
            return
        }
        if(name == "JMS") {
            cpu.stack[cpu.sp] = cpu.pc+1
            cpu.sp = (cpu.sp + 1) % 3
            cpu.pc = addr
        } else if(name == "JUN") {
            cpu.pc = addr
        }
    }
}