; Tests binary and decimal multi-digit arithmetic functions from the MCS-4 assembly manual.
; Uses RAM bank 2 so needs at least 3 RAM banks set in config.

        JUN START

FAIL2   JUN FAIL        ; needs to cross page boundary

; From MCS-4 assembly manual, 4-14
; Multi digit addition of data RAM 0 regs 0 and 1
; Input and results are in reverse order (least significant bit first)
; Destroys data RAM 0 reg 1, index regs 4, 5, 6, 7, 8
AD      FIM R4R5, 0     ; Reg pair 2P = reg 0 of RAM chip 0
        FIM R6R7, 16    ; Reg pair 3P = reg 1 of RAM chip 0
        CLB             ; Set carry = 0
        XCH R8          ; Set digit counter = 0
AD1     SRC R4R5        ; Select RAM reg 0
        RDM             ; Read digit to accumulator
        SRC R6R7        ; Select RAM reg 1
        ADM             ; Add digit + carry to accumulator
        WRM             ; Write result to reg 1
        INC R5          ; Address next char. of RAM reg 0
        INC R7          ; Address next char. of RAM reg 1
        ISZ R8, AD1     ; Branch if digit counter < 16 (non zero)
        BBL 0

; From MCS-4 assembly manual, 4-17
; Multi digit subtraction of data RAM 0 regs 0 and 1
; Input and results are in reverse order (least significant bit first)
; Destroys data RAM 0 reg 1, index regs 4, 5, 6, 7, 8
SB      FIM R4R5, 0     ; Reg pair 2P = reg 0 of RAM chip 0
        FIM R6R7, 16    ; Reg pair 3P = reg 1 of RAM chip 0
        CLB
        XCH R8          ; Set digit counter = 0
        STC             ; Set carry = 1
SB1     CMC             ; Complement carry bit
        SRC R4R5        ; Select RAM reg 0
        RDM             ; Read digit to accumulator
        SRC R6R7        ; Select RAM reg 1
        SBM             ; Subtract digit and carry from accumulator
        WRM             ; Write result to reg 1
        INC R5          ; Address next char. of RAM reg 0
        INC R7          ; Address next char. of RAM reg 1
        ISZ R8, SB1     ; Branch if digit counter < 16 (non zero)
        BBL 0

; From MCS-4 assembly manual, 4-14, adapted with DAA instruction
; Multi digit decimal addition of data RAM 0 regs 0 and 1
; Input and results are in reverse order (least significant bit first)
; Destroys data RAM 0 reg 1, index regs 4, 5, 6, 7, 8
ADD     FIM R4R5, 0     ; Reg pair 2P = reg 0 of RAM chip 0
        FIM R6R7, 16    ; Reg pair 3P = reg 1 of RAM chip 0
        CLB             ; Set carry = 0
        XCH R8          ; Set digit counter = 0
ADD1    SRC R4R5        ; Select RAM reg 0
        RDM             ; Read digit to accumulator
        SRC R6R7        ; Select RAM reg 1
        ADM             ; Add digit + carry to accumulator
        DAA             ; Decimal adjust
        WRM             ; Write result to reg 1
        INC R5          ; Address next char. of RAM reg 0
        INC R7          ; Address next char. of RAM reg 1
        ISZ R8, ADD1    ; Branch if digit counter < 16 (non zero)
        BBL 0

; From MCS-4 assembly manual, 4-23
; Multi digit decimal subtraction of data RAM 0 regs 0 and 1
; Input and results are in reverse order (least significant bit first)
; Destroys data RAM 0 reg 0, index regs 4, 5, 6, 7, 8
SBD     FIM R4R5, 0     ; Reg pair 2P = reg 0 of RAM chip 0
        FIM R6R7, 16    ; Reg pair 3P = reg 1 of RAM chip 0
        CLB
        XCH R8          ; Set digit counter = 0
        STC             ; Set carry = 1
SBD1    TCS             ; Accumulator = 9 or 10
        SRC R6R7        ; Select RAM reg 1
        SBM             ; Produce 9's or 10's complement
        CLC             ; Set carry = 0
        SRC R4R5        ; Select RAM reg 0
        ADM             ; Add minuend to accumulator
        DAA             ; Adjust accumulator
        WRM             ; Write result to reg 0
        INC R5          ; Address next char. of RAM reg 0
        INC R7          ; Address next char. of RAM reg 1
        ISZ R8, SBD1     ; Branch if digit counter < 16 (non zero)
        BBL 0

START   LDM 2
        DCL             ; Select RAM bank 2

        ; Binary arithmetic test
                        ; load 241233 = 3AE51H in reverse order in reg 0
        FIM R0R1, 0
        SRC R0R1        ; Select reg 0, char 0 of RAM chip 0
        LDM 1
        WRM
        INC R1
        SRC R0R1        ; next character
        LDM 5
        WRM
        INC R1
        SRC R0R1
        LDM 0EH
        WRM
        INC R1
        SRC R0R1
        LDM 0AH
        WRM
        INC R1
        SRC R0R1
        LDM 3
        WRM

                        ; load 3538 = DD2H in reverse order in reg 0
        FIM R0R1, 16
        SRC R0R1        ; Select reg 1, char 0 of RAM chip 0
        LDM 2
        WRM
        INC R1
        SRC R0R1        ; next character
        LDM 0DH
        WRM
        INC R1
        SRC R0R1
        LDM 0DH
        WRM

        JMS AD          ; 241233 + 3538 = 244771 = 3BC23H

                        ; read the answer in reg 1 of RAM chip 0 and check each character
        FIM R0R1, 16
        SRC R0R1
        LDM 3
        CLC
        SBM
        JCN 12, FAIL2   ; fail if accumulator != 0 (not equal to number)

        INC R1
        SRC R0R1
        LDM 2
        CLC
        SBM
        JCN 12, FAIL2   ; fail if accumulator != 0 (not equal to number)

        INC R1
        SRC R0R1
        LDM 0CH
        CLC
        SBM
        JCN 12, FAIL2   ; fail if accumulator != 0 (not equal to number)

        INC R1
        SRC R0R1
        LDM 0BH
        CLC
        SBM
        JCN 12, FAIL2   ; fail if accumulator != 0 (not equal to number)

        INC R1
        SRC R0R1
        LDM 3
        CLC
        SBM
        JCN 12, FAIL2   ; fail if accumulator != 0 (not equal to number)

                        ; load 123456789 = 75BCD15H in reverse order in reg 0
        FIM R0R1, 0
        SRC R0R1        ; Select reg 0, char 0 of RAM chip 0
        LDM 5
        WRM
        INC R1
        SRC R0R1        ; next character
        LDM 1
        WRM
        INC R1
        SRC R0R1
        LDM 0DH
        WRM
        INC R1
        SRC R0R1
        LDM 0CH
        WRM
        INC R1
        SRC R0R1
        LDM 0BH
        WRM
        INC R1
        SRC R0R1
        LDM 5
        WRM
        INC R1
        SRC R0R1
        LDM 7
        WRM

        JMS SB          ; 123456789 - 244771 = 123212018 = 75810F2H

        FIM R0R1, 16
        SRC R0R1
        LDM 2
        CLC
        SBM
        JCN 12, FAIL2   ; fail if accumulator != 0 (not equal to number)

        INC R1
        SRC R0R1
        LDM 0FH
        CLC
        SBM
        JCN 12, FAIL2   ; fail if accumulator != 0 (not equal to number)

        INC R1
        SRC R0R1
        LDM 0
        CLC
        SBM
        JCN 12, FAIL2   ; fail if accumulator != 0 (not equal to number)

        INC R1
        SRC R0R1
        LDM 1
        CLC
        SBM
        JCN 12, FAIL2   ; fail if accumulator != 0 (not equal to number)

        INC R1
        SRC R0R1
        LDM 8
        CLC
        SBM
        JCN 12, FAIL2   ; fail if accumulator != 0 (not equal to number)

        INC R1
        SRC R0R1
        LDM 5
        CLC
        SBM
        JCN 12, FAIL2   ; fail if accumulator != 0 (not equal to number)

        INC R1
        SRC R0R1
        LDM 7
        CLC
        SBM
        JCN 12, FAIL2   ; fail if accumulator != 0 (not equal to number)

        ; Decimal arithmetic test
                        ; load 333 in reverse order in reg 0
        FIM R0R1, 0
        SRC R0R1        ; Select reg 0, char 0 of RAM chip 0
        LDM 3
        WRM
        INC R1
        SRC R0R1        ; next character
        WRM
        INC R1
        SRC R0R1
        WRM
        INC R1
        LDM 0
ADDZ    SRC R0R1
        WRM
        ISZ R1, ADDZ    ; Keep adding zeros until reg is full

                        ; load 884 in reverse order in reg 1
        FIM R0R1, 16
        SRC R0R1        ; Select reg 1, char 0 of RAM chip 0
        LDM 4
        WRM
        INC R1
        SRC R0R1
        LDM 8
        WRM
        INC R1
        SRC R0R1
        WRM
        INC R1
        LDM 0
ADDZ2   SRC R0R1
        WRM
        ISZ R1, ADDZ2   ; Keep adding zeros until reg is full

        JMS ADD         ; 333 + 884 = 1217

        FIM R0R1, 16
        SRC R0R1
        LDM 7
        CLC
        SBM
        JCN 12, FAIL    ; fail if accumulator != 0 (not equal to number)

        INC R1
        SRC R0R1
        LDM 1
        CLC
        SBM
        JCN 12, FAIL    ; fail if accumulator != 0 (not equal to number)

        INC R1
        SRC R0R1
        LDM 2
        CLC
        SBM
        JCN 12, FAIL    ; fail if accumulator != 0 (not equal to number)

        INC R1
        SRC R0R1
        LDM 1
        CLC
        SBM
        JCN 12, FAIL    ; fail if accumulator != 0 (not equal to number)


                        ; load 1219 in reverse order in reg 0
        FIM R0R1, 0
        SRC R0R1        ; Select reg 0, char 0 of RAM chip 0
        LDM 9
        WRM
        INC R1
        SRC R0R1        ; next character
        LDM 1
        WRM
        INC R1
        SRC R0R1
        LDM 2
        WRM
        INC R1
        SRC R0R1
        LDM 1
        WRM

        JMS SBD         ; 1219 - 1217 = 2

                        ; Unlike other functions here the result is stored in reg 0
        FIM R0R1, 0
        SRC R0R1
        LDM 2
        CLC
        SBM
        JCN 12, FAIL    ; fail if accumulator != 0 (not equal to number)
        JUN SUCC

;Put ASCII char stored in R0R1 to designated ROM output port
;Stores 0 in accumulator
PUTC    XCH R0
        WRR
        XCH R0
        XCH R1
        WRR
        XCH R1
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