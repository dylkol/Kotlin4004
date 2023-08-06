import intel4001.RomBank
import intel4002.RamBankGroup
import intel4004.Intel4004
import io.StdInDevice
import io.StdOutDevice
import tui.CommandParser
import java.lang.NumberFormatException

val config = Config("config.cfg")
object MCS4 {
    val logger = Logger()
    val clock = Clock
    val dataBus = Line4Bit()
    val cpu = Intel4004(dataBus, ControlLines, clock, logger)
    val romBank = RomBank(config.numRoms, dataBus, clock, logger)
    val ramBanks = RamBankGroup(config.numRamBanks, dataBus, ControlLines.cmRam, clock, logger)
    val stdOutDevice = StdOutDevice(romBank.romArray[config.romOutPort].ioLines)
    val stdInDevice = StdInDevice(romBank.romArray[config.romInPort].ioLines)
}
fun main(args: Array<String>) {
    val system = MCS4
    val parser = CommandParser
    // TODO log clock cycles

    if(args.isNotEmpty())
        system.romBank.loadFromFile(args[0])

    var input: String
    while(true) {
        print("Enter command: ")
        input = readln()
        val splitInput = input.split(Regex("\\s+"))
        val cmd = parser.parseCommand(splitInput[0])
        val args = if(splitInput.size > 1) splitInput.slice(1 until splitInput.size) else listOf<String>()
        if(cmd != null) {
            try {
                cmd.execute(args, system)
            } catch(e: NumberFormatException) {
                println("Invalid argument: ${e.message}")
            }
        } else {
            println("Invalid command. Use 'help' for available commands.")
        }
    }
}