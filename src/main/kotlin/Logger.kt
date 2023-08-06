
enum class LogType {
    ERROR,
    DEBUG
}
class Logger {
    var printDebug = false
    fun log(logType: LogType, message: String, newLine: Boolean) {
        if ((printDebug and (logType == LogType.DEBUG)) or (logType == LogType.ERROR)) {
            if (newLine)
                println(message)
            else
                print(message)
        }
    }
}