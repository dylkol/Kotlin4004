package intel4002

import Clock
import Line4Bit
import Logger
import kotlin.math.max

class RamBank(val number: Int, amount: Int, val dataBus: Line4Bit, val clock: Clock, val logger: Logger) {
    val ramArray = Array(max(amount, 4)){ Intel4002(it) }
    private var currChip: Intel4002? = null // Denotes both output port and data chip selection
    private var currReg: Int = 0 // Selected register of curr RAM chip
    private var currNibble: Int = 0 // One out of 16 nibbles in a RAM register
    private var opcode: Int = 0 // Kept for IO instruction
    private var twoStep = 0 // Count for 2-step instructions
    fun step() {
        when(clock.cycle) {
            Cycle.M1 -> {
                opcode = (dataBus.data.value shl 4)
            }

            Cycle.M2 -> {
                if (twoStep > 0) twoStep = (twoStep+1)%3
                opcode = opcode or dataBus.data.value
                if ((((opcode shr 4) == 1) || ((opcode shr 4) == 4) || ((opcode shr 4) == 5) || ((opcode shr 4) == 7)
                    || (((opcode shr 4) == 2) && ((opcode and 1) == 0))) && twoStep == 0) {
                    twoStep = 1 // Two-step instruction, ignore next step so it does not get excused for other instructions
                }
            }

            Cycle.X2 -> {
                if ((opcode shr 4 == 0x02) && (opcode and 0x01) == 0x01 && twoStep != 2) {
                    // SRC instruction
                    if(dataBus.data.value shr 2 < ramArray.size) {
                        currChip = ramArray[dataBus.data.value shr 2]
                        currReg = dataBus.data.value and 0x03
                    }
                } else if (currChip != null) {
                    when {
                        opcode == 0xe0 && twoStep != 2 ->
                            currChip!!.data[currReg][currNibble] = dataBus.data             // WRM instruction
                        opcode == 0xe1 && twoStep != 2 ->
                            currChip!!.outputLines.data = dataBus.data                      // WMP instruction
                        (opcode shr 2) == 0x39 && twoStep != 2 ->
                            currChip!!.status[currReg][opcode and 0x03] = dataBus.data      // WRn instruction

                        (opcode == 0xe8 || opcode == 0xe9 || opcode == 0xeb) && twoStep != 2 ->
                            // SBM, RDM and ADM instructions, all send data nibble from RAM
                            dataBus.data = currChip!!.data[currReg][currNibble]
                        (opcode shr 2) == 0x3b && twoStep != 2 ->
                            dataBus.data = currChip!!.status[currReg][opcode and 0x03]      // RDn instruction
                    }
                }
            }

            Cycle.X3 -> {
                if ((opcode shr 4 == 0x02) && (opcode and 0x01) == 0x01 && twoStep != 2) {
                    // SRC instruction
                    currNibble = dataBus.data.value
                }
            }

            else -> {}
        }
    }
}
