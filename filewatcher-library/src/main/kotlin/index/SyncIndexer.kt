package index

import Logger
import java.io.File
import java.nio.file.Files

/**
 * @author Dmitry Borodin on 7/19/21.
 */
internal class SyncIndexer {

    private val state = SynchronizedIndexState()

    fun addPathToIndex(file: File): Unit = when {
        file.isDirectory -> {
            file.listFiles().forEach { it -> addPathToIndex(it) }
        }
        file.isFile -> {
            val type = Files.probeContentType(file.toPath())
            if (type.contains("text")) {
                addTextFileToIndex(file)
            } else {
                //nothing
            }
        }
        else -> {
            Logger.debug("cannot add since not a directory and not a file $file")
        }
    }

    private fun addTextFileToIndex(file: File) {
        file
            .forEachLine(Charsets.UTF_8) { it -> it.split("\\s+".toRegex())
                .forEach { state.add(it, file) }
            }
    }

    private fun pathModified(file: File) {
        removePath(file)
        addPathToIndex(file)
    }

    private fun removePath(file: File): Unit = when {
        file.isDirectory -> {
            file.listFiles().forEach { it -> removePath(it) }
        }
        file.isFile -> {
            state.removeFile(file)
        }
        else -> {
            Logger.debug("cannot remove since not a directory and not a file $file")
        }
    }

    fun getFilesWithWord(word: String): List<File> {
        return state.getFilesForWork(word)
    }
}