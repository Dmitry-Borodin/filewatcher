/**
 * @author Dmitry Borodin on 7/19/21.
 */

object Logger {
    fun debug(message: String) {
        if (Configuration.isDebug) {
            print("Debug message: $message")
        }
    }
}