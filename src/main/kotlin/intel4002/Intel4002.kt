package intel4002

import Line4Bit
import util.UNibble


data class Intel4002(val number: Int) {
    val data = Array(4) {Array(16) { UNibble(0)}  } // 4 registers containing 16 data nibbles each
    val status = Array(4) {Array(4) { UNibble(0) } } // 4 registers containing 4 status nibbles each
    var outputLines = Line4Bit()

    override fun toString(): String {
        val builder = StringBuilder()
        // First create line of dashes
        // 2 for each character, 2 for the | at the beginning and end, then 2 for spaces between data and status, minus 1 for final character
        builder.append("  \t"+ "-".repeat(2*(data[0].size + status[0].size)+3)+"\n")
        // Then add column numbers
        builder.append("  \t")
        for (i in data[0].indices) {
            builder.append(" %X".format(i))
        }
        builder.append(" |")
        for (i in status[0].indices) {
            builder.append(" $i")
        }
        builder.append('\n')
        // Now the actual table contents
        for (i in data.indices) {
            builder.append("R$i\t|")
            for ((j,d) in data[i].withIndex()) {
                if (j != data[i].size - 1)
                    builder.append("%X|".format(d.value))
                else
                    builder.append("%X | ".format(d.value))
            }
            for (s in status[i]) {
                builder.append("%X|".format(s.value))
            }
            builder.append('\n')
        }
        // Bottom line of dashes
        // 2 for each character, 2 for the | at the beginning and end, then 2 for spaces between data and status, minus 1 for final character
        builder.append("  \t"+ "-".repeat(2*(data[0].size + status[0].size)+3))
        return builder.toString()
    }
}