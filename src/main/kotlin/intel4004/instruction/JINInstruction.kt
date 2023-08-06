package intel4004.instruction

import intel4004.Intel4004

class JINInstruction(override val opcode: Int) : Instruction {
    override val name = "JIN"
    private val rpNum = (opcode and 0x0e) shr 1 // RP contained in bits 5, 6 and 7
    override fun toString() = "$name ${rpNum}P"
    override fun execute(cpu: Intel4004) {
        val addr = (cpu.indexRegisters[2*rpNum].value shl 4) or cpu.indexRegisters[2*rpNum+1].value
        if((cpu.pc.value and 0x0ff) < 0xff)
            cpu.pc = (cpu.pc and 0xf00) or addr
        else if((cpu.pc.value and 0x0ff) == 0xff)
            cpu.pc = (((cpu.pc shr 8) + 0x01) shl 8) or addr // Go to next page if PC is at final entry
    }
}