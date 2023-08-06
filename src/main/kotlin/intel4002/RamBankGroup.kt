package intel4002

import Clock
import Line4Bit
import Logger
import kotlin.math.min


class RamBankGroup(amount: Int, val dataBus: Line4Bit, private val cmRam: Line4Bit, val clock: Clock, val logger: Logger) {
    val ramBankArray = Array(min (amount, 8)){ RamBank( it, 4, dataBus, clock, logger) }
    fun step() {
        if (ramBankArray.size > cmRam.data.value)
            ramBankArray[cmRam.data.value].step()
    }
}
