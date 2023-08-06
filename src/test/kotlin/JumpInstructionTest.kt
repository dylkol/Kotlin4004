import intel4004.Intel4004
import intel4004.instruction.TwoStepInstruction
import org.junit.jupiter.api.Test
import util.UInt12
import util.UNibble
import kotlin.test.assertEquals

class JumpInstructionTest {
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
    fun testJUN() {
        cpu.pc = UInt12(0)
        val addr = 0x371

        val opcode = (0x4 shl 12) or addr
        run2StepInstr(opcode)

        assertEquals(cpu.pc.value, 0x371)
    }

    @Test
    fun testJMS() {
        cpu.pc = UInt12(2)
        cpu.sp = 1
        val addr = 0x371

        val opcode = (0x5 shl 12) or addr
        run2StepInstr(opcode)

        assertEquals(cpu.pc.value, 0x371)
        assertEquals(cpu.sp, 2)
        assertEquals(cpu.stack[cpu.sp-1].value, 4)
    }

    @Test
    fun testJCN() {
        cpu.pc = UInt12(2)
        cpu.test = true
        cpu.carry = false
        cpu.acc = UNibble(0)

        val addr = 0x31
        var cond = 0b0101 // Jump if acc == 0 or test == false

        var opcode = (0x01 shl 12) or (cond shl 8) or addr
        run2StepInstr(opcode)

        assertEquals(cpu.pc.value, 0x031)

        cpu.pc = UInt12(2)
        cpu.test = true
        cpu.carry = true
        cpu.acc = UNibble(0)

        cond = 0b1011 // Jump if carry == false and test == true

        opcode = (0x1 shl 12) or (cond shl 8) or addr
        run2StepInstr(opcode)

        assertEquals(cpu.pc.value, 0x004)

        cpu.pc = UInt12(0x0ff)
        cpu.test = true
        cpu.carry = false
        cpu.acc = UNibble(1)

        cond = 0b1010 // Jump if acc != 0 and carry == false

        opcode = (0x1 shl 12) or (cond shl 8) or addr
        run2StepInstr(opcode)

        assertEquals(cpu.pc.value, 0x131) // Value should be moved to next page

        cpu.pc = UInt12(2)
        cpu.test = true
        cpu.carry = false
        cpu.acc = UNibble(1)

        cond = 0b0111 // Jump if acc == 0 or carry == true or test == false

        opcode = (0x1 shl 12) or (cond shl 8) or addr
        run2StepInstr(opcode)

        assertEquals(cpu.pc.value, 0x004)
    }

    @Test
    fun testJIN() {
        cpu.indexRegisters[4] = UNibble(3)
        cpu.indexRegisters[5] = UNibble(1)
        cpu.pc = UInt12(0)

        val opcode = 0x35

        run1StepInstr(opcode)

        assertEquals(cpu.indexRegisters[4].value, 3)
        assertEquals(cpu.indexRegisters[5].value, 1)

        assertEquals(cpu.pc.value, 0x31)

        cpu.indexRegisters[4] = UNibble(3)
        cpu.indexRegisters[5] = UNibble(1)
        cpu.pc = UInt12(0x2ff)

        run1StepInstr(opcode)

        assertEquals(cpu.indexRegisters[4].value, 3)
        assertEquals(cpu.indexRegisters[5].value, 1)

        assertEquals(cpu.pc.value, 0x331)
    }

    @Test
    fun testISZ() {
        cpu.indexRegisters[6] = UNibble(0)
        cpu.pc = UInt12(0x2ff)

        var opcode = 0x7652

        run2StepInstr(opcode)

        assertEquals(cpu.indexRegisters[6].value, 1)
        assertEquals(cpu.pc.value, 0x352) // Should have gone to next page

        cpu.indexRegisters[6] = UNibble(0)
        cpu.pc = UInt12(0x001)

        run2StepInstr(opcode)

        assertEquals(cpu.indexRegisters[6].value, 1)
        assertEquals(cpu.pc.value, 0x052)

        cpu.indexRegisters[8] = UNibble(15)
        cpu.pc = UInt12(0x027)

        opcode = 0x7852

        run2StepInstr(opcode)

        assertEquals(cpu.indexRegisters[8].value, 0)
        assertEquals(cpu.pc.value, 0x029)
    }

    @Test
    fun testBBL() {
        cpu.acc = UNibble(0)
        cpu.pc = UInt12(0x31)
        cpu.sp = 1
        cpu.stack[0] = UInt12(0x12f)

        val opcode = 0xc8

        run1StepInstr(opcode)

        assertEquals(cpu.sp, 0)
        assertEquals(cpu.pc.value, 0x12f)
        assertEquals(cpu.acc.value, 8)
    }
}