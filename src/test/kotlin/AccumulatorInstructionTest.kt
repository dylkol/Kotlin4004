import intel4004.Intel4004
import org.junit.jupiter.api.Test
import util.UNibble
import kotlin.test.assertEquals

class AccumulatorInstructionTest {
    private val clock = Clock
    private val dataBus = Line4Bit()
    private val controlLines = ControlLines
    private val logger = Logger()
    private val cpu = Intel4004(dataBus, controlLines, clock, logger)
    private fun runInstr(opcode: Int) {
        cpu.decodeInstruction(opcode).execute(cpu)
    }

    @Test
    fun testCLB() {
        cpu.carry = true
        cpu.acc = UNibble(2)

        val opcode = 0xf0
        runInstr(opcode)

        assertEquals(cpu.carry, false)
        assertEquals(cpu.acc.value, 0)
    }

    @Test
    fun testCLC() {
        cpu.carry = true

        val opcode = 0xf1
        runInstr(opcode)

        assertEquals(cpu.carry, false)
    }

    @Test
    fun testIAC() {
        cpu.acc = UNibble(0)


        val opcode = 0xf2
        runInstr(opcode)

        assertEquals(cpu.acc.value, 1)
        assertEquals(cpu.carry, false)

        cpu.acc = UNibble(15)

        runInstr(opcode)

        assertEquals(cpu.acc.value, 0)
        assertEquals(cpu.carry, true)
    }

    @Test
    fun testCMC() {
        cpu.carry = false

        val opcode = 0xf3
        runInstr(opcode)

        assertEquals(cpu.carry, true)
    }

    @Test
    fun testCMA() {
        cpu.acc = UNibble(0b1100)

        val opcode = 0xf4
        runInstr(opcode)

        assertEquals(cpu.acc.value, 0b0011)
    }

    @Test
    fun testRAL() {
        cpu.carry = true
        cpu.acc = UNibble(0b0110)

        val opcode = 0xf5
        runInstr(opcode)

        // 1 0110 => 0 1101
        assertEquals(cpu.carry, false)
        assertEquals(cpu.acc.value, 0b1101)
    }

    @Test
    fun testRAR() {
        cpu.carry = true
        cpu.acc = UNibble(0b0110)

        val opcode = 0xf6
        runInstr(opcode)

        // 1 0110 => 0 1011
        assertEquals(cpu.carry, false)
        assertEquals(cpu.acc.value, 0b1011)
    }

    @Test
    fun testTCC() {
        cpu.carry = true
        cpu.acc = UNibble(0)

        val opcode = 0xf7
        runInstr(opcode)

        assertEquals(cpu.acc.value, 1)
        assertEquals(cpu.carry, false)
    }

    @Test
    fun testDAC() {
        cpu.acc = UNibble(0)

        val opcode = 0xf8
        runInstr(opcode)

        assertEquals(cpu.carry, false)
        assertEquals(cpu.acc.value, 15)

        cpu.acc = UNibble(1)

        runInstr(opcode)

        assertEquals(cpu.carry, true)
        assertEquals(cpu.acc.value, 0)
    }

    @Test
    fun testTCS() {
        cpu.carry = false
        cpu.acc = UNibble(0)

        val opcode = 0xf9
        runInstr(opcode)
        assertEquals(cpu.carry, false)
        assertEquals(cpu.acc.value, 9)

        cpu.carry = true
        runInstr(opcode)

        assertEquals(cpu.carry, false)
        assertEquals(cpu.acc.value, 10)
    }

    @Test
    fun testSTC() {
        cpu.carry = false

        val opcode = 0xfa
        runInstr(opcode)

        assertEquals(cpu.carry, true)
    }

    @Test
    fun testDAA() {
        cpu.carry = false
        cpu.acc = UNibble(10)

        val opcode = 0xfb
        runInstr(opcode)

        assertEquals(cpu.carry, true)
        assertEquals(cpu.acc.value, 0)

        cpu.carry = true
        cpu.acc = UNibble(8)

        runInstr(opcode)

        assertEquals(cpu.carry, true)
        assertEquals(cpu.acc.value, 14)
    }

    @Test
    fun testKBP() {
        cpu.acc = UNibble(0b1000)

        val opcode = 0xfc
        runInstr(opcode)

        assertEquals(cpu.acc.value, 4)

        cpu.acc = UNibble(0b1001)

        runInstr(opcode)

        assertEquals(cpu.acc.value, 15)
    }

    @Test
    fun testDCL() {
        cpu.acc = UNibble(0b1001)

        val opcode = 0xfd
        runInstr(opcode)

        assertEquals(cpu.controlLines.cmRam.data.value, 1)
    }

    @Test
    fun testLDM() {
        cpu.acc = UNibble(0)

        val opcode = 0xd4
        runInstr(opcode)

        assertEquals(cpu.acc.value, 4)
    }
}