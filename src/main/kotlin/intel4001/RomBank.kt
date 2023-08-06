package intel4001

import Clock
import Cycle
import Line4Bit
import LogType
import Logger
import util.UNibble
import java.io.File
import kotlin.math.max
import kotlin.math.min

class RomBank(amount: Int, val dataBus: Line4Bit, val clock: Clock, val logger: Logger) {
    val romArray = Array(max(1, min(amount, 16))){ Intel4001(it) }
    private var addressLow = UNibble(0)
    private var addressHigh = UNibble(0)
    private var currAddress: Int = 0
    private var currChip: Intel4001? = null
    private var selectedIO: Intel4001? = null
    private var opcode: Int = 0 // Kept for IO instructions

    private var twoStep = 0 // Count for 2-step instructions
    fun step() {
        // Enable the given IO port when SRC function is executed
        when (Clock.cycle) {
            Cycle.A1 -> {
                addressLow = dataBus.data
            }
            Cycle.A2 -> addressHigh = dataBus.data
            Cycle.A3 -> {
                if (dataBus.data.value < romArray.size)
                    currChip = romArray[dataBus.data.value]
            }
            Cycle.M1 -> {
                currAddress = ((addressHigh.value shl 4) or addressLow.value)
                // Opcode will be sent split into two 4 bit values, OPR and OPA
                if (currChip != null) {
                    dataBus.data = UNibble(currChip!!.data[currAddress] shr 4)
                    opcode = (dataBus.data.value shl 4)
                }
            }

            Cycle.M2 -> {
                if (twoStep > 0) twoStep = (twoStep+1)%3
                if (currChip != null) {
                    dataBus.data = UNibble(currChip!!.data[currAddress] and 0x0f)
                    opcode = opcode or dataBus.data.value

                    if ((((opcode shr 4) == 1) || ((opcode shr 4) == 4) || ((opcode shr 4) == 5) || ((opcode shr 4) == 7)
                        || (((opcode shr 4) == 2) && ((opcode and 1) == 0))) && twoStep == 0) {
                        twoStep = 1 // Two-step instruction, ignore next step so it does not get excused for other instructions
                    }
                }
            }

            Cycle.X1 -> {
                if (opcode == 0xea && twoStep != 2) {
                    // RDR instruction
                    selectedIO?.ioLines?.enabled = true // Tell input device to retrieve input, bit of a hacky solution
                }
            }
            Cycle.X2 -> {
                if ((opcode shr 4 == 0x02) && (opcode and 0x01) == 0x01 && twoStep != 2) {
                    // SRC instruction
                    if (dataBus.data.value < romArray.size)
                        selectedIO = romArray[dataBus.data.value]
                } else if (opcode == 0xe2 && twoStep != 2) {
                    // WRR instruction
                    selectedIO?.ioLines?.data = dataBus.data
                } else if (opcode == 0xea && twoStep != 2) {
                    // RDR instruction
                    dataBus.data = selectedIO?.ioLines?.data ?: dataBus.data
                }
            }
            Cycle.X3 -> {}
        }
    }

    fun loadFromFile(fileName: String) {
        val bytes = File(fileName).inputStream().readBytes()
        if(bytes.size > romArray.size*256) {
            logger.log(LogType.ERROR,"Not enough ROMs for file of size ${bytes.size} bytes.", true)
            return
        }
        val bytesAsInt = bytes.map { (it.toUByte().toInt()) }.toIntArray()
        for (i in romArray.indices) {
            if(bytesAsInt.size < (256*i)+1) {
                // All bytes in ROM
                break
            }
            val rom = romArray[i]
            bytesAsInt.copyInto(rom.data, 0, 256*i, min(256*(i+1), bytesAsInt.size))
        }
    }
}