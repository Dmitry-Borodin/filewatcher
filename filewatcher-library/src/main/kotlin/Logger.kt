import java.lang.RuntimeException

/**
 * @author Dmitry Borodin on 7/19/21.
 */

internal object Logger {
    fun debug(message: String) {
        if (Configuration.isDebug) {
            print("Debug message: $message")
        }
    }

    fun error(message: String) {
        if (Configuration.isDebug) {
            throw RuntimeException(message)
        } else {
            print("Error message: $message")
        }
    }
}