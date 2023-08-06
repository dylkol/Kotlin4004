package intel4004.instruction

import intel4004.Intel4004
import util.UNibble

class FIMInstruction(override var opcode: Int) : TwoStepInstruction {
    override val name = "FIM"
    private val rp = (opcode and 0x0e) shr 1 // RP contained in bits 5, 6 and 7
    var data = 0
    private var secondStepDone = false
    override fun addSecondByte(opcodeSecondByte: Int) {
        opcode = (opcode shl 4) or opcodeSecondByte
        data = opcodeSecondByte
        secondStepDone = true
    }
    override fun toString(): String = if (secondStepDone) "$name ${rp}P $data" else "$name ${rp}P ?"
    override fun execute(cpu: Intel4004) {
        if(!cpu.secondStep) {
            cpu.pc++
            return
        }
        cpu.indexRegisters[2*rp] = UNibble(data shr 4)
        cpu.indexRegisters[2*rp+1] = UNibble(data and 0x0f)
        cpu.pc++
    }
}