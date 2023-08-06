import intel4004.Intel4004
import org.junit.jupiter.api.Test
import util.UInt12
import util.UNibble
import kotlin.test.assertEquals

class IOInstructionTest {
    private val clock = Clock
    private val dataBus = Line4Bit()
    private val controlLines = ControlLines
    private val logger = Logger()
    private val cpu = Intel4004(dataBus, controlLines, clock, logger)
    private fun runInstr(opcode: Int) {
        cpu.decodeInstruction(opcode).execute(cpu)
    }

    @Test
    fun testSRC() {
        cpu.indexRegisters[6] = UNibble(0x3)
        cpu.indexRegisters[7] = UNibble(0xf)
        cpu.pc = UInt12(1)
        val opcode = 0x27 // RP: 3

        val instr = cpu.decodeInstruction(opcode)
        cpu.secondStep = false
        instr.execute(cpu)

        assertEquals(cpu.dataBus.data.value, 0x3)

        cpu.secondStep = true
        instr.execute(cpu)
        assertEquals(cpu.dataBus.data.value, 0xf)
        assertEquals(cpu.pc.value, 2)
    }

    @Test
    fun testIOWrite() {
        /* At this level, all IO write instructions are the same in that they write the accumulator to the data bus */

        cpu.acc = UNibble(2)

        var opcode = 0xe0 // WRM
        runInstr(opcode)
        assertEquals(cpu.dataBus.data.value, cpu.acc.value)

        opcode = 0xe1 // WMP
        runInstr(opcode)
        assertEquals(cpu.dataBus.data.value, cpu.acc.value)

        opcode = 0xe2 // WRR
        runInstr(opcode)
        assertEquals(cpu.dataBus.data.value, cpu.acc.value)

        opcode = 0xe4 // WR0
        runInstr(opcode)
        assertEquals(cpu.dataBus.data.value, cpu.acc.value)

        opcode = 0xe5 // WR1
        runInstr(opcode)
        assertEquals(cpu.dataBus.data.value, cpu.acc.value)

        opcode = 0xe6 // WR2
        runInstr(opcode)
        assertEquals(cpu.dataBus.data.value, cpu.acc.value)

        opcode = 0xe7 // WR3
        runInstr(opcode)
        assertEquals(cpu.dataBus.data.value, cpu.acc.value)
    }

    @Test
    fun testSBM() {
        cpu.acc = UNibble(13)
        cpu.carry = false
        cpu.dataBus.data = UNibble(2)

        val opcode = 0xe8
        runInstr(opcode)
        assertEquals(cpu.acc, UNibble(11))
        assertEquals(cpu.carry, true)

        cpu.acc = UNibble(2)
        cpu.carry = false
        cpu.dataBus.data = UNibble(4)

        runInstr(opcode)
        assertEquals(cpu.acc, UNibble(14))
        assertEquals(cpu.carry, false)


        cpu.acc = UNibble(3)
        cpu.carry = true // With borrow
        cpu.dataBus.data = UNibble(3)

        runInstr(opcode)
        assertEquals(cpu.acc, UNibble(15))
        assertEquals(cpu.carry, false)
    }

    @Test
    fun testADM() {
        cpu.acc = UNibble(13)
        cpu.carry = false
        cpu.dataBus.data = UNibble(2)

        val opcode = 0xeb
        runInstr(opcode)
        assertEquals(cpu.acc, UNibble(15))
        assertEquals(cpu.carry, false)

        cpu.acc = UNibble(15)
        cpu.carry = false
        cpu.dataBus.data = UNibble(4)

        runInstr(opcode)
        assertEquals(cpu.acc, UNibble(3))
        assertEquals(cpu.carry, true)


        cpu.acc = UNibble(3)
        cpu.carry = true // With carry
        cpu.dataBus.data = UNibble(3)

        runInstr(opcode)
        assertEquals(cpu.acc, UNibble(7))
        assertEquals(cpu.carry, false)
    }

    @Test
    fun testIORead() {
        // RDM, RDR and RDn all simply store databus in acc
        cpu.acc = UNibble(0)
        cpu.dataBus.data = UNibble(2)

        var opcode = 0xe9   // RDM
        runInstr(opcode)
        assertEquals(cpu.acc, UNibble(2))

        opcode = 0xea       // RDR
        runInstr(opcode)
        assertEquals(cpu.acc, UNibble(2))

        opcode = 0xec       // RD0
        runInstr(opcode)
        assertEquals(cpu.acc, UNibble(2))

        opcode = 0xed       // RD1
        runInstr(opcode)
        assertEquals(cpu.acc, UNibble(2))

        opcode = 0xee       // RD2
        runInstr(opcode)
        assertEquals(cpu.acc, UNibble(2))

        opcode = 0xee       // RD3
        runInstr(opcode)
        assertEquals(cpu.acc, UNibble(2))
    }
}