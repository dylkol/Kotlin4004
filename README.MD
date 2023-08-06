# Kotlin4004
### Intel 4004 (MCS-4) Emulator written in Kotlin
My first emulator project. This emulates the first CPU, the Intel 4004, along with its associated ROM and RAMs (4001 and 4002), forming the MCS-4 system.
The Shift Register 4003 is not implemented yet.

It is more or less cycle-accurate, in that the data bus is separately modelled every clock cycle. The internal behaviour of the components is not exactly accurately modelled though.

It's been tested successfully on some test roms (see `roms` folder) but these are not very extensive. Expect some bugs.

## Usage
Run `Kotlin4004` (Unix-based) or `Kotlin4004.bat` (Windows) inside `Kotlin4004-1.0/bin` with a ROM file as argument.
This will prompt you to enter a command. Use `help` to see the available commands.

### I/O
The `StdOutDevice` and `StdInDevice` classes provide support for printing to standard output
and retrieving from standard input respectively. The `config.cfg` file allows for setting a ROM I/O port assigned to
each device. 

Printing to standard output can be done by writing characters 4-bits at a time to the assigned ROM port using the `WRR` instruction, terminating with a null 
character, which flushes the buffer.

Reading from standard input can be done by reading from the assigned ROM port using the `RDR` instruction, which automatically prompts the user for a line to be entered.
This line is then stored in a buffer. Each `RDR` instruction will read the buffer nibble (4-bits) by nibble, until exhausted when a new line is prompted for again at the following `RDR` instruction.

See the test ROMs in `roms` for example usage.
