import intel4004.Intel4004
import intel4004.instruction.TwoStepInstruction
import org.junit.jupiter.api.Test
import util.UInt12
import util.UNibble
import kotlin.test.assertEquals

class IndexRegInstructionTest {
    private val clock = Clock
    private val dataBus = Line4Bit()
    private val controlLines = ControlLines
    private val logger = Logger()
    private val cpu = Intel4004(dataBus, controlLines, clock, logger)
    private fun run1StepInstr(opcode: Int) {
        cpu.decodeInstruction(opcode).execute(cpu)
    }

    private fun run2StepInstr(opcode: Int) {
        val instr = cpu.decodeInstruction(opcode shr 8) as TwoStepInstruction
        cpu.secondStep = false
        instr.execute(cpu)
        cpu.secondStep = true
        instr.addSecondByte(opcode and 0x00ff)
        instr.execute(cpu)
        cpu.secondStep = false
    }

    @Test
    fun testADD() {
        cpu.acc = UNibble(2)
        cpu.carry = true
        cpu.indexRegisters[0] = UNibble(3)

        run1StepInstr(0x80)

        assertEquals(cpu.acc.value, 6)
        assertEquals(cpu.carry, false)

        cpu.acc = UNibble(13)
        cpu.carry = false
        cpu.indexRegisters[1] = UNibble(5)

        run1StepInstr(0x81)

        assertEquals(cpu.acc.value, 2)
        assertEquals(cpu.carry, true)
    }

    @Test
    fun testSUB() {
        cpu.acc = UNibble(2)
        cpu.carry = true
        cpu.indexRegisters[0] = UNibble(3)

        run1StepInstr(0x90)

        assertEquals(cpu.acc.value, 14)
        assertEquals(cpu.carry, false)

        cpu.acc = UNibble(13)
        cpu.carry = false
        cpu.indexRegisters[1] = UNibble(5)

        run1StepInstr(0x91)

        assertEquals(cpu.acc.value, 8)
        assertEquals(cpu.carry, true)
    }

    @Test
    fun testLD() {
        cpu.acc = UNibble(2)
        cpu.carry = true
        cpu.indexRegisters[0] = UNibble(3)

        run1StepInstr(0xa0)

        assertEquals(cpu.acc.value, 3)
        assertEquals(cpu.carry, true)

        cpu.acc = UNibble(13)
        cpu.carry = false
        cpu.indexRegisters[1] = UNibble(5)

        run1StepInstr(0xa1)

        assertEquals(cpu.acc.value, 5)
        assertEquals(cpu.carry, false)
    }

    @Test
    fun testXCH() {
        cpu.acc = UNibble(2)
        cpu.carry = true
        cpu.indexRegisters[0] = UNibble(3)

        run1StepInstr(0xb0)

        assertEquals(cpu.acc.value, 3)
        assertEquals(cpu.indexRegisters[0].value, 2)
        assertEquals(cpu.carry, true)

        cpu.acc = UNibble(13)
        cpu.carry = false
        cpu.indexRegisters[1] = UNibble(5)

        run1StepInstr(0xb1)

        assertEquals(cpu.acc.value, 5)
        assertEquals(cpu.indexRegisters[1].value, 13)
        assertEquals(cpu.carry, false)
    }

    @Test
    fun testINC() {
        cpu.indexRegisters[7] = UNibble(0)

        var opcode = 0x67
        run1StepInstr(opcode)

        assertEquals(cpu.indexRegisters[7].value, 1)

        cpu.indexRegisters[5] = UNibble(15)

        opcode = 0x65
        run1StepInstr(opcode)

        assertEquals(cpu.indexRegisters[5].value, 0)
    }

    @Test
    fun testFIM() {
        cpu.indexRegisters[4] = UNibble(0)
        cpu.indexRegisters[5] = UNibble(0)

        val opcode = 0x2442

        run2StepInstr(opcode)

        assertEquals(cpu.indexRegisters[4].value, 4)
        assertEquals(cpu.indexRegisters[5].value, 2)
    }

    @Test
    fun testFIN() {
        cpu.pc = UInt12(0)
        cpu.indexRegisters[0] = UNibble(2)
        cpu.indexRegisters[1] = UNibble(3)
        cpu.indexRegisters[4] = UNibble(0)
        cpu.indexRegisters[5] = UNibble(0)

        val opcode = 0x3452

        run2StepInstr(opcode)

        assertEquals(cpu.indexRegisters[0].value, 2)
        assertEquals(cpu.indexRegisters[1].value, 3)

        assertEquals(cpu.indexRegisters[4].value, 5)
        assertEquals(cpu.indexRegisters[5].value, 2)

        assertEquals(cpu.pc.value, 1)


        cpu.pc = UInt12(0x2ff)
        cpu.indexRegisters[0] = UNibble(2)
        cpu.indexRegisters[1] = UNibble(3)
        cpu.indexRegisters[4] = UNibble(0)
        cpu.indexRegisters[5] = UNibble(0)

        run2StepInstr(opcode)

        assertEquals(cpu.indexRegisters[0].value, 2)
        assertEquals(cpu.indexRegisters[1].value, 3)

        assertEquals(cpu.indexRegisters[4].value, 5)
        assertEquals(cpu.indexRegisters[5].value, 2)

        assertEquals(cpu.pc.value, 0x300)
    }
}