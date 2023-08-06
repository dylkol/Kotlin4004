import intel4002.RamBankGroup
import org.junit.jupiter.api.Test
import util.UNibble
import kotlin.test.assertEquals

class RamTest {
    private val clock = Clock
    private val dataBus = Line4Bit()
    private val cmRam = Line4Bit()
    private val logger = Logger()
    private val ramBankGroup = RamBankGroup(3, dataBus, cmRam, clock, logger)

    init {
        cmRam.data = UNibble(2)
    }

    private fun runWriteDataInstr(opcode: Int, data: UNibble) {
        // A1
        ramBankGroup.step()
        clock.cycle()
        // A2
        ramBankGroup.step()
        clock.cycle()
        // A3
        ramBankGroup.step()
        clock.cycle()

        // M1
        dataBus.data = UNibble(opcode shr 4)
        ramBankGroup.step()
        clock.cycle()

        // M2
        dataBus.data = UNibble(opcode and 0x0f)
        ramBankGroup.step()
        clock.cycle()

        // X1
        ramBankGroup.step()
        clock.cycle()
        // X2
        dataBus.data = data
        ramBankGroup.step()
        clock.cycle()
        // X3
        ramBankGroup.step()
        clock.cycle()
    }

    private fun runReadDataInstr(opcode: Int) {
        // A1
        ramBankGroup.step()
        clock.cycle()
        // A2
        ramBankGroup.step()
        clock.cycle()
        // A3
        ramBankGroup.step()
        clock.cycle()

        // M1
        dataBus.data = UNibble(opcode shr 4)
        ramBankGroup.step()
        clock.cycle()

        // M2
        dataBus.data = UNibble(opcode and 0x0f)
        ramBankGroup.step()
        clock.cycle()

        // X1
        ramBankGroup.step()
        clock.cycle()
        // X2
        ramBankGroup.step()
        clock.cycle()
        // X3
        ramBankGroup.step()
        clock.cycle()
    }

    private fun runSRC(opcode: Int, data: Int) {
        // A1
        ramBankGroup.step()
        clock.cycle()
        // A2
        ramBankGroup.step()
        clock.cycle()
        // A3
        ramBankGroup.step()
        clock.cycle()

        // M1
        dataBus.data = UNibble(opcode shr 4)
        ramBankGroup.step()
        clock.cycle()

        // M2
        dataBus.data = UNibble(opcode and 0x0f)
        ramBankGroup.step()
        clock.cycle()

        // X1
        ramBankGroup.step()
        clock.cycle()

        // X2
        dataBus.data = UNibble(data shr 4)
        ramBankGroup.step()
        clock.cycle()

        // X3
        dataBus.data = UNibble(data and 0x0f)
        ramBankGroup.step()
        clock.cycle()
    }

    @Test
    fun testWriteData() {
        runSRC(0x21, 0x77) // SRC Chip 1, reg 3, nibble 7

        cmRam.data = UNibble(2)
        val opcode = 0xe0   // WRM
        runWriteDataInstr(opcode, UNibble(2))

        assertEquals(2, ramBankGroup.ramBankArray[cmRam.data.value].ramArray[1].data[3][7].value)
    }

    @Test
    fun testWriteStatus() {
        runSRC(0x23, 0xd0) // SRC Chip 3, reg 1

        cmRam.data = UNibble(2)
        var opcode = 0xe4   // WR0
        runWriteDataInstr(opcode, UNibble(4))
        assertEquals(4, ramBankGroup.ramBankArray[cmRam.data.value].ramArray[3].status[1][0].value)

        opcode = 0xe5   // WR1
        runWriteDataInstr(opcode, UNibble(6))
        assertEquals(6, ramBankGroup.ramBankArray[cmRam.data.value].ramArray[3].status[1][1].value)

        opcode = 0xe6   // WR2
        runWriteDataInstr(opcode, UNibble(11))
        assertEquals(11, ramBankGroup.ramBankArray[cmRam.data.value].ramArray[3].status[1][2].value)

        opcode = 0xe7   // WR3
        runWriteDataInstr(opcode, UNibble(13))
        assertEquals(13, ramBankGroup.ramBankArray[cmRam.data.value].ramArray[3].status[1][3].value)
    }

    @Test
    fun testWriteOut() {
        runSRC(0x23, 0x80) // SRC Chip 2
        cmRam.data = UNibble(2)

        val opcode = 0xe1 // WMP
        runWriteDataInstr(opcode,  UNibble(5))
        assertEquals(5, ramBankGroup.ramBankArray[cmRam.data.value].ramArray[2].outputLines.data.value)
    }

    @Test
    fun testReadData() {
        runSRC(0x27, 0xa9) // SRC Chip 2, reg 2, nibble 9
        cmRam.data = UNibble(2)

        ramBankGroup.ramBankArray[cmRam.data.value].ramArray[2].data[2][9] = UNibble(11)
        var opcode = 0xe9 // RDM
        runReadDataInstr(opcode)
        assertEquals(11, dataBus.data.value)

        ramBankGroup.ramBankArray[cmRam.data.value].ramArray[2].data[2][9] = UNibble(8)
        opcode = 0xe8 // SBM
        runReadDataInstr(opcode)
        assertEquals(8, dataBus.data.value)

        ramBankGroup.ramBankArray[cmRam.data.value].ramArray[2].data[2][9] = UNibble(3)
        opcode = 0xeb // ADM
        runReadDataInstr(opcode)
        assertEquals(3, dataBus.data.value)
    }

    @Test
    fun testReadStatus() {
        runSRC(0x29, 0xa0) // SRC Chip 2, reg 2
        cmRam.data = UNibble(2)

        ramBankGroup.ramBankArray[cmRam.data.value].ramArray[2].status[2][0] = UNibble(11)
        var opcode = 0xec // RD0
        runReadDataInstr(opcode)
        assertEquals(11, dataBus.data.value)

        ramBankGroup.ramBankArray[cmRam.data.value].ramArray[2].status[2][1] = UNibble(8)
        opcode = 0xed // RD1
        runReadDataInstr(opcode)
        assertEquals(8, dataBus.data.value)

        ramBankGroup.ramBankArray[cmRam.data.value].ramArray[2].status[2][2] = UNibble(3)
        opcode = 0xee // RD2
        runReadDataInstr(opcode)
        assertEquals(3, dataBus.data.value)

        ramBankGroup.ramBankArray[cmRam.data.value].ramArray[2].status[2][3] = UNibble(5)
        opcode = 0xef // RD3
        runReadDataInstr(opcode)
        assertEquals(5, dataBus.data.value)
    }
}