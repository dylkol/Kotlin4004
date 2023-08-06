package intel4004.instruction

import intel4004.Intel4004
import util.UNibble

class FINInstruction(override var opcode: Int) : TwoStepInstruction {
    override val name = "FIN"
    private val rpNum = (opcode and 0x0e) shr 1 // RP contained in bits 5, 6 and 7
    var data = 0
    override fun addSecondByte(opcodeSecondByte: Int) {
        opcode = (opcode shl 4) or opcodeSecondByte
        data = opcodeSecondByte
    }
    override fun toString() = "$name ${rpNum}P"
    override fun execute(cpu: Intel4004) {
        if(!cpu.secondStep) {
            // Prepare RP0 to be sent over as an address in the first step
            val addr = (cpu.indexRegisters[0] shl 4).value or cpu.indexRegisters[1].value
            if((cpu.pc.value and 0x0ff) < 0xff)
                cpu.fetchAddr = (cpu.pc and 0xf00) or addr
            else if((cpu.pc.value and 0x0ff) == 0xff)
                cpu.fetchAddr = (((cpu.pc shr 8) + 0x01) shl 8) or addr // Go to next page if PC is at final entry
        } else {
            // Put fetched data into designated RP in second step
            cpu.indexRegisters[2*rpNum] = UNibble(data shr 4)
            cpu.indexRegisters[2*rpNum+1] = UNibble(data and 0x0f)
            cpu.pc++
        }
    }
}