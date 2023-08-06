package intel4001

import Line4Bit

/***
 * The 4001 is a 2048-bit metal mask programmable ROM providing custom microprogramming capability for the MCS-4
 * micro computer set. It is organized as 256 x 8 bit words.
 */
data class Intel4001(val number: Int) {
    val data = IntArray(256) { 0 }
    val ioLines = Line4Bit()
}