package index

import java.io.File
import java.nio.file.Files

/**
 * @author Dmitry Borodin on 7/19/21.
 */
internal class Indexer {

    private val state = SynchronizedIndexState()

    fun addPathToIndex(file: File) = when {
        file.isDirectory -> {
            file.listFiles().forEach { it -> addPathToIndex(it) }
        }
        file.isFile -> {
            val type = Files.probeContentType(file.toPath())
            //state.add
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
            state.removeFile(file)
        }
        else -> {
            Logger.debug("cannot remove since not a directory and not a file $file")
        }
    }

    fun getFilesWithWord(word: String): List<File> {
        return emptyList()
    }
}