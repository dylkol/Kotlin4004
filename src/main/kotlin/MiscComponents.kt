import util.UNibble

enum class Cycle {
    A1, A2, A3, // Address sending cycles
    M1, M2, // Opcode sending cycles
    X1, X2, X3 // Execution cycles
}

object Clock {
    private var totalCycles = 0
    var cycle: Cycle
        private set

    init {
        cycle = Cycle.A1
//        logger.log(totalCycles.toString(), false)
    }

    fun cycle() {
        val cycles = Cycle.values()
        totalCycles++
        cycle = cycles[(cycle.ordinal + 1) % cycles.size]
//        if (totalCycles % 8 == 0)
//            logger.log(totalCycles.toString(), false)
    }
}

object ControlLines {
    var cmRam = Line4Bit()
}

class Line4Bit {
    var data = UNibble(0)
        set(data) {
            field = data
            enabled = true
        }
    var enabled = false
}


