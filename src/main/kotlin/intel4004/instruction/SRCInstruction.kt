package intel4004.instruction

import intel4004.Intel4004
import util.UNibble

class SRCInstruction(override var opcode: Int) : Instruction {
    override val name = "SRC"
    private val rpNum = (opcode and 0x0e) shr 1 // RP contained in bits 5, 6 and 7
    private var addr = 0
    var secondStep = false // Different from regular two step instructions as the steps here are consecutive at X2 and X3
    override fun toString() = "$name ${rpNum}P"
    override fun execute(cpu: Intel4004) {
        if(!secondStep) {
            // Send over first nibble of address
            addr = (cpu.indexRegisters[2*rpNum].value shl 4) or cpu.indexRegisters[2*rpNum+1].value
            cpu.dataBus.data = UNibble(addr shr 4)
            secondStep = true
        } else {
            // Send over second nibble of address
            cpu.dataBus.data = UNibble(addr and 0x0f)
            cpu.pc++
        }
    }
}