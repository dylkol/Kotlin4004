package intel4004.instruction

import intel4004.Intel4004

class ISZInstruction(override var opcode: Int): TwoStepInstruction {
    override val name = "ISZ"
    private var addr = 0
    private val regNum = opcode and 0x0f
    private var secondStepDone = false
    override fun addSecondByte(opcodeSecondByte: Int) {
        opcode = (opcode shl 8) or opcodeSecondByte
        addr = opcodeSecondByte
        secondStepDone = true
    }

    override fun toString() = if(secondStepDone) "$name $regNum $addr" else "$name $regNum ?"
    override fun execute(cpu: Intel4004) {
        if(!cpu.secondStep) {
            cpu.pc++
            return
        }

        cpu.indexRegisters[regNum]++

        if(cpu.indexRegisters[regNum].value != 0) {
            if ((cpu.pc.value and 0x0ff) < 0xff)
                cpu.pc = (cpu.pc and 0xf00) or addr
            else if ((cpu.pc.value and 0x0ff) == 0xff)
                cpu.pc = (((cpu.pc shr 8) + 0x01) shl 8) or addr // Go to next page if PC is at final entry
        } else {
            // Skip if zero
            cpu.pc++
        }
    }
}