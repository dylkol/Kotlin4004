package io

import Line4Bit
import util.UNibble

class StdInDevice(private val ioLines: Line4Bit) {
    private val buffer = CharArray(256)
    private var bufferEmpty = true
    private var currIdx = 0
    private var firstNibble = true

    /* Reads in a line from stdin and stores in buffer, them empties the buffer nibble by nibble with every read instruction. */
    fun step() {
        if(!ioLines.enabled) return
        if (bufferEmpty) {
            buffer.fill('\u0000')
            println("Enter input: ")
            readln().toCharArray().copyInto(buffer)
            bufferEmpty = false
        }
        if (firstNibble) {
            ioLines.data = UNibble(buffer[currIdx].code shr 4)
            firstNibble = false
        } else {
            ioLines.data = UNibble(buffer[currIdx].code and 0x0f)
            if (buffer[currIdx+1] == '\u0000') {
                bufferEmpty = true
            } else {
                currIdx++
            }
            firstNibble = true
        }
        ioLines.enabled = false
    }
}