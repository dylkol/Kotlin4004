package intel4004.instruction

object InstructionNames {
    // Names for each instruction category in opcode order
    val ACCUMULATOR_INSTRUCTION_NAMES = arrayOf(
        "CLB", "CLC", "IAC", "CMC", "CMA", "RAL", "RAR", "TCC", "DAC", "TCS", "STC", "DAA", "KBP", "DCL"
    )
    val INDEX_REG_ACC_INSTRUCTION_NAMES = arrayOf(
        "ADD", "SUB", "LD", "XCH"
    )
    val DIRECT_JUMP_INSTRUCTION_NAMES = arrayOf(
        "JUN", "JMS"
    )
    val ACCUMULATOR_IMMEDIATE_INSTRUCTION_NAMES = arrayOf(
        "BBL", "LDM"
    )
    val IO_INSTRUCTION_NAMES = arrayOf(
        "WRM", "WMP", "WRR", "UNK", "WR0", "WR1", "WR2", "WR3", "SBM",
        "RDM", "RDR", "ADM", "RD0", "RD1", "RD2", "RD3"
    )
}