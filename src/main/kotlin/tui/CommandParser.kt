package tui

object CommandParser {
    val commands = arrayListOf(
        ShowRegs,
        ShowStack,
        ShowRam,
        StepCycle,
        Step,
        Run,
        Debug,
        Quit,
        Help
    )

    fun parseCommand(name: String) : Command? {
        return commands.find { cmd -> cmd.name == name }
    }
}