; Prints "HELLO WORLD" to the standard output.

        FIM R0R1, 0
        SRC R0R1                ;Use ROM output port 0

        ;Load and send H: 48
        FIM R0R1, 48H
        JMS PUTC

        ;E: 45
        FIM R0R1, 45H
        JMS PUTC

        ;L: 4C
        ;Done twice
        FIM R0R1, 4CH
        JMS PUTC
        JMS PUTC

        ;O: 4F
        FIM R0R1, 4FH
        JMS PUTC

        ;Space: 20
        FIM R0R1, 20H
        JMS PUTC

        ;W: 57
        FIM R0R1, 57H
        JMS PUTC

        ;O: 4F
        FIM R0R1, 4FH
        JMS PUTC

        ;R: 52
        FIM R0R1, 52H
        JMS PUTC

        ;L: 4C
        FIM R0R1, 4CH
        JMS PUTC

        ;D: 44
        FIM R0R1, 44H
        JMS PUTC

        ;!: 21
        FIM R0R1, 21H
        JMS PUTC

        ;LF(newline): 0A
        FIM R0R1, 0AH
        JMS PUTC

        ;Flush the buffer with null byte
        FIM R0R1, 0
        JMS PUTC

;Infinite loop
DONE    JUN DONE

;Put ASCII char stored in R0R1 to designated ROM output port
;Stores 0 in accumulator
PUTC    XCH R0
        WRR
        XCH R0
        XCH R1
        WRR
        XCH R1
        BBL 0