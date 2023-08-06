; Tests 4 bit multiplication with some values.
; At the end prompts user for two single-digit inputs, multiplies them, and returns the result.

        ; multiply 2 and 4
        FIM R0R1, 24H
        JMS MULT
        LDM 8
        SUB R2
        JCN 12, FAIL    ; fail if accumulator != 0 (not equal to number)

        ; multiply 3 and 2
        FIM R0R1, 32H
        JMS MULT
        LDM 6
        SUB R2
        JCN 12, FAIL    ; fail if accumulator != 0 (not equal to number)

        ; multiply 10 and 1
        FIM R0R1, 0A1H
        JMS MULT
        LDM 10
        SUB R2
        JCN 12, FAIL    ; fail if accumulator != 0 (not equal to number)

        ; multiply 4 and 4
        FIM R0R1, 44H
        JMS MULT
        LDM 0
        SUB R2
        JCN 12, FAIL    ; fail if accumulator != 0 (not equal to number)

        ; multiply 4 and 3
        FIM R0R1, 43H
        JMS MULT
        LDM 12
        SUB R2
        JCN 12, FAIL    ; fail if accumulator != 0 (not equal to number)

        ; multiply user input
        FIM R0R1, 10H
        SRC R0R1        ; set to the input port

        JMS GETC
        JMS CHTONUM
        XCH R1          ; should only be a single nibble so we can ignore R0
        XCH R4          ; move first input to R4

        JMS GETC
        JMS CHTONUM
        XCH R4          ; second input should now be correctly in R1, just need to move R4 (first input) to R0
        XCH R0

        JMS MULT

        LDM 0
        XCH R0          ; set R0 to 0
        XCH R2
        XCH R1          ; move mult result to R1
        JMS NUMTOCH

        FIM R2R3, 0
        SRC R2R3        ; set to the output port
        JMS PUTC
        FIM R0R1, 0AH   ; LF
        JMS PUTC
        FIM R0R1, 0
        JMS PUTC        ; print result (null byte at end)
        JUN SUCC

;Gets ASCII char from standard input and store in R0R1 to designated ROM i/o port
;Stores 0 in accumulator
GETC    RDR
        XCH R0
        RDR
        XCH R1
        BBL 0

;Put ASCII char stored in R0R1 to designated ROM i/o port
;Stores 0 in accumulator
PUTC    XCH R0
        WRR
        XCH R0
        XCH R1
        WRR
        XCH R1
        BBL 0

;Converts ASCII digit in R0R1 to number, stored in R0R1
;Stores 0 in accumulator
CHTONUM CLC
        LDM 3           ; Subtract 30H (ASCII 0) from R0R1
        XCH R0
        SUB R0
        CLC
        XCH R0
        BBL 0

;Converts number in R0R1 to ASCII digit, stored in R0R1
;Stores 0 in accumulator
NUMTOCH CLC
        LDM 3           ; Add 30H (ASCII 0) to R0R1
        ADD R0
        XCH R0
        CLC
        BBL 0

SUCC    FIM R0R1, 0
        SRC R0R1       ; Set ROM output port 0
        FIM R0R1, 48H  ; H
        JMS PUTC
        FIM R0R1, 4FH  ; O
        JMS PUTC
        JMS PUTC       ; O
        FIM R0R1, 52H  ; R
        JMS PUTC
        FIM R0R1, 41H  ; A
        JMS PUTC
        FIM R0R1, 59H  ; Y
        JMS PUTC
        FIM R0R1, 0AH  ; LF
        JMS PUTC
        FIM R0R1, 0    ; Null termination
        JMS PUTC
        JUN DONE

FAIL    FIM R0R1, 0
        SRC R0R1       ; Set ROM output port 0
        FIM R0R1, 46H  ; F
        JMS PUTC
        FIM R0R1, 41H  ; A
        JMS PUTC
        FIM R0R1, 49H  ; I
        JMS PUTC
        FIM R0R1, 4CH  ; L
        JMS PUTC
        FIM R0R1, 0AH  ; LF
        JMS PUTC
        FIM R0R1, 0    ; Null termination
        JMS PUTC

DONE    JUN DONE

; 4 bit multiplication of R0 and R1
; adds R0 to R0 R1 times
; result stored in R2
; destroys ACC, R1, R2
MULT
    CLB
    XCH R2 ; Clear R2 to store result

    LDM 15
    SUB R1
    XCH R1 ; Store loop counter in R1 (15-b) in a*b
    CLC

MULT_CHECK
    ISZ R1, MULT_LOOP
    BBL 0

MULT_LOOP
    LD R0
    ADD R2
    CLC
    XCH R2
    LD R1
    XCH R1
    JUN MULT_CHECK