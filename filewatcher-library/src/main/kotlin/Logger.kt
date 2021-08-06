import java.lang.RuntimeException
import java.nio.file.Path

/**
 * @author Dmitry Borodin on 7/19/21.
 */

internal object Logger {

    var filesIndexed = 0L //this is not synced, this value is very approximate and not currect

    fun debug(message: String) {
        if (Configuration.isDebug) {
            println("Debug message: $message")
        }
    }

    fun error(message: String) {
        if (Configuration.isDebug) {
            throw RuntimeException(message)
        } else {
            println("Error message: $message")
        }
    }

    fun addingFile(file: Path) {
        if (!Configuration.isDebug) return
        filesIndexed++
        if (filesIndexed%100 == 0L) {
            print("\rFiles indexed $filesIndexed")
        }
    }
}