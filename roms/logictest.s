; Tests logic gate functions as defined in the MCS-4 assembly manual
        JUN START

; From MCS-4 assembly manual, 4-9
; AND of reg 0 and 1
; Result in reg 0
; Destroys regs 0, 1, 2, 3, acc, carry
AND     FIM R2R3, 11    ; Reg 2 = 0, Reg 3 = 11
AND1    LDM 0           ; Get bit of reg 0; set acc = 0
        XCH R0          ; Reg 0 data to acc; reg 0 = 0
        RAL             ; 1st 'AND' bit to carry
        XCH R0          ; Save shifted data in reg 0; acc=0
        INC R3          ; Done if reg 3 = 0
        XCH R3          ; Reg 3 to acc
        JCN 4, AND2     ; Return if acc = 0
        XCH R3          ; Otherwise restore acc and reg3
        RAR             ; Bit of reg0 is alone in acc
        XCH R2          ; Save 1st 'AND' bit in reg 2
        XCH R1          ; Get bit of reg 1
        RAL             ; Left bit to carry
        XCH R1          ; Save shifted data in reg 1
        RAR             ; 2nd 'AND' bit to acc
        ADD R2          ; 'ADD' gives 'AND' of the 2 bits
        JUN AND1        ; in carry
AND2    BBL 0           ; Return to main program

; From MCS-4 assembly manual, 4-10
; OR of reg 0 and 1
; Result in reg 0
; Destroys regs 0, 1, 2, 3, acc, carry
OR      FIM R2R3, 11    ; Reg 2 = 0, Reg 3 = 11
OR1     LDM 0           ; Get bit of reg 0; set acc = 0
        XCH R0          ; Reg 0 data to acc; reg 0 = 0
        RAL             ; 1st 'OR' bit to carry
        XCH R0          ; Save shifted data in reg 0; acc=0
        INC R3          ; Done if reg 3 = 0
        XCH R3          ; Reg 3 to acc
        JCN 4, OR2      ; Return if acc = 0
        XCH R3          ; Otherwise restore acc and reg3
        RAR             ; Bit of reg0 is alone in acc
        XCH R2          ; Save 1st 'OR' bit in reg 2
        LDM 0           ; Set acc = 0
        XCH R1          ; Get bit of reg 1
        RAL             ; Left bit to carry
        XCH R1          ; Save shifted data in reg 1
        RAR             ; 2nd 'OR' bit to acc
        ADD R2          ; Produce the OR of the bits
        JCN 2, OR1      ; Jump if carry = 1 because 'OR'=1
        RAL             ; Otherwise 'OR' = left bit of
        JUN OR1         ; Accumulator
OR2     BBL 0           ; Transmit to carry by RAL


; From MCS-4 assembly manual, 4-11
; XOR of reg 0 and 1
; Result in reg 0
; Destroys regs 0, 1, 2, 3, acc, carry
XOR     FIM R2R3, 11    ; Reg 2 = 0, Reg 3 = 11
XOR1    LDM 0           ; Get bit of reg 0; set acc = 0
        XCH R0          ; Reg 0 data to acc; reg 0 = 0
        RAL             ; 1st 'OR' bit to carry
        XCH R0          ; Save shifted data in reg 0; acc=0
        INC R3          ; Done if reg 3 = 0
        XCH R3          ; Reg 3 to acc
        JCN 4, XOR2     ; Return if acc = 0
        XCH R3          ; Otherwise restore acc and reg3
        RAR             ; Bit of reg0 is alone in acc
        XCH R2          ; Save 1st XOR bit in reg 2
        LDM 0           ; Get bit in reg 1, set acc = 0
        XCH R1
        RAL             ; Left bit to carry
        XCH R1          ; Save shifted data in reg 1
        RAR             ; 2nd 'XOR' bit to acc
        ADD R2          ; Produce the XOR of the bits
        RAL             ; XOR = left bit of Accum; transmit
        JUN XOR1        ; to carry by RAL
XOR2    BBL 0

;Put ASCII char stored in R0R1 to designated ROM output port
;Stores 0 in accumulator
PUTC    XCH R0
        WRR
        XCH R0
        XCH R1
        WRR
        XCH R1
        BBL 0

; tests the logic functions and prints success or failure
START   FIM R0R1, 0
        SRC R0R1        ; Use ROM output port 0
        FIM R0R1, 0FH
        JMS AND         ; 0x0 AND 0xF
        XCH R0          ; result in accumulator
        JCN 0CH, FAIL   ; jump if 0x0 AND 0xF != 0
        FIM R0R1, 255
        JMS AND         ; 0xF AND 0xF
        CLC
        LDM 0FH
        SUB R0          ; 0XF - result
        JCN 0CH, FAIL   ; jump if (0xF AND 0xF) != 0xF
        FIM R0R1, 59H
        JMS AND         ; 0x5 AND 0x9
        CLC
        LDM 1
        SUB R0          ; 0x1 - result
        JCN 0CH, FAIL   ; jump if (0x5 AND 0x9) != 0x1

        FIM R0R1, 0FH
        JMS OR          ; 0x0 OR 0xF
        CLC
        LDM 0FH
        SUB R0          ; 0xF - result
        JCN 0CH, FAIL   ; jump if (0x0 OR 0xF) != 0xF
        FIM R0R1, 255
        JMS OR          ; 0x0 OR 0xF
        CLC
        LDM 0FH
        SUB R0          ; 0xF - result
        JCN 0CH, FAIL   ; jump if (0xF OR 0xF) != 0xF
        FIM R0R1, 59H
        JMS OR          ; 0x5 OR 0x9
        CLC
        LDM 0DH
        SUB R0          ; 0xD - result
        JCN 0CH, FAIL   ; jump if (0x5 OR 0x9) != 0xD

        JUN SUCC


SUCC    FIM R0R1, 48H  ; H
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
        FIM R0R1, 59H  ; Y
        JMS PUTC
        FIM R0R1, 0AH  ; LF
        JMS PUTC
        FIM R0R1, 0    ; Null termination
        JMS PUTC
        JUN DONE

FAIL    FIM R0R1, 46H  ; F
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