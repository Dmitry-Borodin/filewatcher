import java.lang.RuntimeException
import java.nio.file.Path

/**
 * @author Dmitry Borodin on 7/19/21.
 */

internal object Logger {

    var filesIndexed = 0L //this is not synced, this value is very approximate and not currect
    var foldersIndexed = 0L //this is not synced, this value is very approximate and not currect
    var wordsForEachFileIndexed = 0L //this is not synced, this value is very approximate and not currect

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
//        if (filesIndexed%100 == 0L) {
            printStats()
//        }
    }

    fun addingFolder(file: Path) {
        if (!Configuration.isDebug) return
        foldersIndexed++
    }

    fun addingWord(file: Path) {
        if (!Configuration.isDebug) return
        wordsForEachFileIndexed++
    }

    private fun printStats() {
        print("\rFiles indexed $filesIndexed    Folders indexed $foldersIndexed Words indexed $wordsForEachFileIndexed ")
    }
}