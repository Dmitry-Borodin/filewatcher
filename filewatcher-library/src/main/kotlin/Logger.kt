import java.lang.RuntimeException
import java.nio.file.Path
import java.util.*

/**
 * @author Dmitry Borodin on 7/19/21.
 */

internal object Logger {

    private var filesIndexed = 0L //this is not synced, this value is very approximate and not currect
    private var foldersIndexed = 0L //this is not synced, this value is very approximate and not currect
    private var wordsForEachFileIndexed = 0L //this is not synced, this value is very approximate and not currect
    private var start: Date = Date()
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
//        println("file $file")
        if (filesIndexed % 100 == 0L) {
            printStats()
        }
    }

    fun addingFolder(file: Path) {
        if (!Configuration.isDebug) return
//        println("folder $file")
        foldersIndexed++
    }

    fun addingWord(file: Path) {
        if (!Configuration.isDebug) return
        wordsForEachFileIndexed++
    }

    private fun printStats() {
        val speed : Long = filesIndexed * 1000 / (Date().time - start.time)  //files per second
        print("\rFiles indexed $filesIndexed    Folders indexed $foldersIndexed Words indexed $wordsForEachFileIndexed  speed is $speed fps")
    }

    fun fileNotText(file: Path) {
        if (!Configuration.isDebug) return
        println("fileN not text $file")
    }
}