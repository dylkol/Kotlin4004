import java.io.File
import java.nio.file.Path

class Config(filename: String) {
    var numRoms = 8
    var numRamBanks = 4
    var romOutPort = 0
    var romInPort = 1
    init {
        var f = File(filename)
        if (!f.exists()) {
            f = File("..", filename)
        }
        f.forEachLine {
            val split = it.replace(Regex("\\s*;.*"), "").split(Regex("\\s*=\\s*"))
            when(split[0]) {
                "num_roms" -> numRoms = split[1].toInt()
                "num_ram_banks" -> numRamBanks = split[1].toInt()
                "stdout_rom_port" -> romOutPort = split[1].toInt()
                "stdin_rom_port" -> romInPort = split[1].toInt()
            }
        }
    }
}