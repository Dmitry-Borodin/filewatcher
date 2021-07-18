package index

import java.io.File
import java.nio.file.Files
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Dmitry Borodin on 7/19/21.
 */
class Indexer {

    private val state = ConcurrentHashMap<String, File>() //word to files

    fun addPathToIndex(file: File) = when {
        file.isDirectory -> {
            file.listFiles().forEach { it -> addPathToIndex(it) }
        }
        file.isFile -> {
            val type = Files.probeContentType(file.toPath())
            synchronized(state) {
                //modify state
            }
        }
        else -> {
            Logger.debug("cannot add since not a directory and not a file $file")
        }
    }

    fun pathModified(file: File) {
        removePath(file)
        addPathToIndex(file)
    }

    fun removePath(file: File) = when {
        file.isDirectory -> {
            file.listFiles().forEach { removePath(it) }
        }
        file.isFile -> {
            synchronized(state) {
                state.forEach { s, file -> if () }
            }
        }
        else -> {
            Logger.debug("cannot remove since not a directory and not a file $file")
        }
    }

    fun getFilesWithWord(word: String): List<File> {
        return emptyList()
    }
}