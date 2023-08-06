package intel4004

import Clock
import ControlLines
import Line4Bit
import Logger
import intel4004.instruction.*
import util.UNibble
import util.UInt12
import util.toInt

class Intel4004(val dataBus: Line4Bit, val controlLines: ControlLines, val clock: Clock, val logger: Logger) {
    // Index registers
    val indexRegisters = Array(16){UNibble(0)}

    // Accumulator
    var acc = UNibble(0)

    // Program counter
    var pc = UInt12(0)

    // Stack registers
    val stack = Array(3){UInt12(0)}

    // Stack pointer - not really a register, as it cannot be manipulated manually, only when using subroutine
    // instructions. As there are only 3 entries in the stack, it is simply a number that goes from 0 to 2
    // and then loops back around to 0.
    var sp = 0

    // Flags
    var carry = false
    var test = false

    // Curr opcode
    private var opr = UNibble(0)
    private var opa = UNibble(0)

    private var currInstr: Instruction = NOPInstruction()

    var secondStep = false // For 2-step instructions

    var fetchAddr: UInt12? = null // For fetching data from ROM

    fun decodeInstruction(opcode: Int): Instruction {
        // Instructions are put into several classes based on their parameters and opcode similarity
        logger.log(LogType.DEBUG, "Decoding instruction %02X".format(opcode), true)
        return when {
            opcode         == 0x00 -> NOPInstruction()
            (opcode shr 4) == 0x0f && opcode < 0xfe -> AccumulatorInstruction(opcode)   // Opcode: 1111 xxxx, does not go above 0xfd

            (opcode shr 4) == 0x06 -> IndexRegInstruction(opcode)                       // Opcode: 0110 RRRR for INC

            (opcode shr 4) == 0x07 -> ISZInstruction(opcode)                            // Opcode: 0111 RRRR

            (opcode shr 4) == 0x01 -> JCNInstruction(opcode)                            // Opcode: 0001 CCCC

            ((opcode shr 3) == 0x1c) -> IOWriteInstruction(opcode)                      // Opcode: 1110 0xxx
            ((opcode shr 3) == 0x1d) -> IOReadInstruction(opcode)                       // Opcode: 1110 1xxx

            (opcode shr 4) == 0x02 && (opcode and 0x01) == 0 -> FIMInstruction(opcode)  // Opcode: 0010 RRR0

            (opcode shr 4) == 0x02 && (opcode and 0x01) == 1 -> SRCInstruction(opcode)  // Opcode: 0010 RRR1

            (opcode shr 4) == 0x03 && (opcode and 0x01) == 0 -> FINInstruction(opcode)  // Opcode: 0011 RRR0

            (opcode shr 4) == 0x03 && (opcode and 0x01) == 1 -> JINInstruction(opcode)  // Opcode: 0011 RRR1

            (opcode shr 5) == 0x06 -> AccumulatorImmediateInstruction(opcode)           // Opcode: 110x DDDD

            (opcode shr 5) == 0x02 -> DirectJumpInstruction(opcode)                     // Opcode: 010x AAAA

            (opcode shr 6) == 0x02 -> IndexRegInstruction(opcode)                       // Opcode: 10xx RRRR

            else -> throw Exception("Invalid instruction $opcode")
        }
    }

    private fun executeInstruction() {
        logger.log(LogType.DEBUG, "Executing $currInstr", true)
        currInstr.execute(this)
    }
    private fun execute2StepInstruction() {
        logger.log(LogType.DEBUG,"Executing step ${secondStep.toInt()+1} of $currInstr", true)
        currInstr.execute(this)
        secondStep = !secondStep
    }

    // An instruction cycle normally consists 8 clock cycles, which will consists of one iteration of the step function.
    // Some instructions take up 16 clock cycles, but those consist of simply two of these steps.
    fun step() {
        when(clock.cycle) {
            // Set Data Bus to PC address in three cycles, starting from low 4 bits.
            Cycle.A1 -> {
                dataBus.data = if(fetchAddr == null)
                    UNibble(pc.value and 0x00f)
                    else UNibble(fetchAddr!!.value and 0x00f)
            }
            Cycle.A2 -> {
                dataBus.data = if (fetchAddr == null)
                    UNibble((pc.value shr 4) and 0x00f)
                else UNibble((fetchAddr!!.value shr 4) and 0x00f)
            }
            Cycle.A3 -> {
                if (fetchAddr == null)
                    dataBus.data = UNibble((pc.value shr 8) and 0x00f)
                else {
                    dataBus.data = UNibble((fetchAddr!!.value shr 8) and 0x00f)
                    fetchAddr = null
                }
            }

            Cycle.M1 -> opr = dataBus.data
            Cycle.M2 -> opa = dataBus.data

            Cycle.X1 -> {
                dataBus.enabled = false // IO instructions will enable the databus again
                // No output is sent at X1, so we can execute everything at X2 and X3
            }

            Cycle.X2 -> {
                val opcode = (opr.value shl 4) or opa.value
                if(!secondStep) currInstr = decodeInstruction(opcode)
                if(currInstr is TwoStepInstruction) {
                    if(secondStep)
                        (currInstr as TwoStepInstruction).addSecondByte(opcode)
                    execute2StepInstruction()
                } else if (currInstr !is IOReadInstruction) {
                    executeInstruction()
                }
            }

            Cycle.X3 -> {
                // SRC sends addr at X2 and X3, IO is read at X3
                if(currInstr is SRCInstruction || currInstr is IOReadInstruction) {
                    executeInstruction()
                }
            }
        }
    }

}