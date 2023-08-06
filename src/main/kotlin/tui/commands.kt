package tui

import Cycle
import MCS4
import intel4002.RamBank
import kotlin.math.max
import kotlin.system.exitProcess
import kotlin.system.measureNanoTime

interface Command {
    val name: String
    val shortDescr: String get() = ""
    val descr: String get() = ""
    fun execute(args: List<String>, system: MCS4)
}

object ShowRegs: Command {
    override val name = "regs"
    override val descr = "Show values of index registers, ACC, SP, PC and carry bit."
    override val shortDescr = descr
    override fun execute(args: List<String>, system: MCS4) {
        val indexRegs = system.cpu.indexRegisters
        for(i in indexRegs.indices step 2) {
            print("R${String.format("%X", i)}:\t${indexRegs[i]}")
            print("\t\t")
            print("R${String.format("%X", i+1)}:\t${indexRegs[i+1]}")
            print("\n")
        }
        println("ACC:\t${system.cpu.acc}")
        println("CB:\t\t${system.cpu.carry}")
        println("SP:\t\t${system.cpu.sp}")
        println("PC:\t\t${system.cpu.pc}")
    }
}

object ShowStack: Command {
    override val name = "stack"
    override val descr = "Show values of stack registers."
    override val shortDescr = descr
    override fun execute(args: List<String>, system: MCS4) {
        val stackRegs = system.cpu.stack
        for (i in stackRegs.indices) {
            println("${stackRegs[i]}")
        }
    }
}

//object ShowRom: Command {
//    // TODO show ROM at specific address range or all of it in instructions and hex
//}

object ShowRam: Command {
    override val name = "ram"
    override val descr =
        """Show contents of RAM. Depending on arguments, can show contents of all banks, one bank, or one chip.
            |Arguments are as follows: ram <bank> <chip>
            |For example, "ram 2" shows all contents of bank 2. "ram 3 1" shows the contents of chip 1 bank 3.
        """.trimMargin()
    override val shortDescr = "Show contents of RAM. Can show contents of all banks, one bank or one chip."
    override fun execute(args: List<String>, system: MCS4) {
        when (args.size) {
            // Show all banks
            0 -> {
                for (bank in system.ramBanks.ramBankArray) {
                    showBank(bank)
                }
            }
            // Show a specific bank
            1 -> {
                try {
                    showBank(system.ramBanks.ramBankArray[args[0].toInt()])
                } catch(e: IndexOutOfBoundsException) {
                    println("Bank ${args[0].toInt()} does not exist.")
                }
            }
            2 -> {
                try {
                    val bank = system.ramBanks.ramBankArray[args[0].toInt()]
                    try {
                        println(bank.ramArray[args[1].toInt()])
                    } catch (e: IndexOutOfBoundsException) {
                        println("Chip ${args[1].toInt()} in bank ${args[0].toInt()} does not exist.")
                    }
                } catch (e: IndexOutOfBoundsException) {
                    println("Bank ${args[0].toInt()} does not exist.")
                }
            }
        }
    }

    private fun showBank(bank: RamBank) {
        println("Bank ${bank.number}")
        println()
        for (chip in bank.ramArray) {
            println("Chip ${chip.number}")
            print(chip)
            if (chip.number != bank.ramArray.size - 1)
                println("\n\n")
        }
        print("\n\n")
    }
}

fun executeCycle(system: MCS4) {
    // Steps through a single clock cycle
    if (system.clock.cycle.ordinal < 3) {
        // A1, A2, A3 instructions, CPU sends addr to ROM
        system.cpu.step()
        system.stdInDevice.step()
        system.romBank.step()
        system.ramBanks.step()
        system.stdOutDevice.step()
    } else if (system.clock.cycle.ordinal < 5) {
        // M1, M2 instructions, ROM sends opcode to CPU
        system.stdInDevice.step()
        system.romBank.step()
        system.ramBanks.step()
        system.cpu.step()
        system.stdOutDevice.step()
    } else {
        // X1, X2 instructions, CPU executes instr
        system.cpu.step()
        system.stdInDevice.step()
        system.romBank.step()
        system.ramBanks.step()
        system.stdOutDevice.step()
    }
    system.clock.cycle()
}

object StepCycle: Command {
    override val name = "cycle"
    override val descr = "Advance one clock cycle out of the 8 cycles that form an instruction. 'cycle' <n> to advance n cycles."
    override val shortDescr = descr
    override fun execute(args: List<String>, system: MCS4) {
        val n = if(args.isNotEmpty()) args[0].toInt() else 1
        repeat(n) {
            executeCycle(system)
        }
    }
}

object Step: Command {
    override val name = "step"
    override val descr = "Advance a full instruction cycle (8 clock cycles). 'step <n>' to advance n instruction cycles."
    override val shortDescr = descr
    override fun execute(args: List<String>, system: MCS4) {
        val n = if(args.isNotEmpty()) args[0].toInt() else 1
        repeat(n) {
            // Steps through an entire instruction cycle
            repeat(Cycle.values().size) {
                executeCycle(system)
            }
        }
    }
}

object Run: Command {
    // TODO in separate thread
    override val name = "run"
    override val descr = """Executes cycles indefinitely. Use "run <freq>" to run at a given clock frequency in Hz.
        |With no argument, the real frequency of 740 000 is used. Use "run -1" to run at unlimited speed.
        |Does not currently in a separate thread so the only way to exit is CTRL+C.
    """.trimMargin()
    override val shortDescr = "Executes cycles indefinitely. Use \"run <freq>\" to run at a given clock frequency in Hz."
    override fun execute(args: List<String>, system: MCS4) {
        val freq = if(args.isNotEmpty()) args[0].toInt() else 740_000
        var count = 0
        while(true) {
            val elapsed = measureNanoTime {
                executeCycle(system)
            }
            if (freq != -1)
                Thread.sleep(max(((1e9/freq).toLong() - elapsed)/1_000_000L,0L))
            count++
        }
    }
}

object Debug: Command {
    override val name = "debug"
    override val descr = "Toggles debug mode, which prints each instruction being executed."
    override val shortDescr = descr
    override fun execute(args: List<String>, system: MCS4) {
        system.logger.printDebug = !system.logger.printDebug
        println("Debug mode ${if(system.logger.printDebug) "on" else "off"}")
    }
}
object Quit: Command {
    override val name = "quit"
    override val descr = "Quits the emulator."
    override val shortDescr = descr

    override fun execute(args: List<String>, system: MCS4) {
        exitProcess(0)
    }
}

object Help: Command {
    override val name = "help"
    override val descr = "Shows this page. 'help <command>' shows extended help for <command>."
    override val shortDescr = descr
    override fun execute(args: List<String>, system: MCS4) {
        if (args.isEmpty()) {
            for (command in CommandParser.commands) {
                println("${command.name}:\t${command.shortDescr}")
            }
        } else {
            val requestedCommand = args[0]
            for (command in CommandParser.commands) {
                if (command.name == requestedCommand) {
                    println(command.descr)
                    return
                }
            }
            println("No command with that name.")
        }
    }
}