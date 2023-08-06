package intel4004.instruction

import intel4004.Intel4004
import util.toBoolean

class JCNInstruction(override var opcode: Int): TwoStepInstruction {
    override val name = "JCN"
    private var addr = 0
    private val cond = opcode and 0x0f
    override fun addSecondByte(opcodeSecondByte: Int) {
        opcode = (opcode shl 8) or opcodeSecondByte
        addr = opcodeSecondByte
    }

    override fun toString() = "$name $cond $addr"
    override fun execute(cpu: Intel4004) {
        if(!cpu.secondStep) {
            cpu.pc++
            return
        }
        val invert = (cond shr 3).toBoolean()
        val accCond = ((cond shr 2) and 0x01).toBoolean() && cpu.acc.value == 0
        val carryCond = ((cond shr 1) and 0x01).toBoolean() && cpu.carry
        val testCond = (cond and 0x01).toBoolean() && !cpu.test

        val jump = (!invert && (accCond || carryCond || testCond)) ||
                   (invert && !(accCond || carryCond || testCond))

        if(jump && (cpu.pc.value and 0x0ff) < 0xff)
            cpu.pc = (cpu.pc and 0xf00) or addr
        else if(jump && (cpu.pc.value and 0x0ff) == 0xff)
            cpu.pc = (((cpu.pc shr 8) + 0x01) shl 8) or addr // Go to next page if PC is at final entry
        else cpu.pc++
    }
}