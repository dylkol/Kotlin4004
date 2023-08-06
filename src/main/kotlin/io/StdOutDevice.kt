package io

import Line4Bit

class StdOutDevice(private val ioLines: Line4Bit) {


    private val buffer = CharArray(256)
    private var currIdx = 0
    private var currChar = 0 // Stored as int to make bit manipulation easier
    private var firstNibble = true

    /* Reads in ASCII chars from data bus, stores in buffer, and flushes the buffer to stdout with certain instruction */
    fun step() {
        if(!ioLines.enabled) return
        if (firstNibble) {
            currChar = (ioLines.data.value shl 4)
            firstNibble = false
        } else {
            buffer[currIdx] = (ioLines.data.value or currChar).toChar()
            if (buffer[currIdx] == '\u0000') { flush() } else { currIdx++ }
            firstNibble = true
        }
        ioLines.enabled = false
    }

    private fun flush() {
        // Flush buffer and print to stdout, when given null byte
        var i = 0
        while(buffer[i] != '\u0000') {
            print(buffer[i])
            i++
        }
        buffer.fill('\u0000')
        currIdx = 0
    }
}