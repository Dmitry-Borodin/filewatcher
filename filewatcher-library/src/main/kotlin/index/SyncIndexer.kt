package index

import Logger
import watcher.isDirectoryToFollow
import watcher.isFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Dmitry Borodin on 7/19/21.
 */
internal class SyncIndexer {

    private val state = SynchronizedIndexState()

    /**
     * Will follow symlinks
     */
    fun addPathToIndex(path: Path): Unit = when {
        path.isDirectoryToFollow() -> {
            Files.walk(path).forEach { it -> addPathToIndex(it) }
        }
        path.isFile() -> {
            val type = Files.probeContentType(path)
            if (type.contains("text")) {
                addTextFileToIndex(path)
            } else {
                //nothing
            }
        }
        else -> {
            Logger.debug("cannot add since not a directory and not a file $path")
        }
    }

    private fun addTextFileToIndex(file: Path) {
        file
            .toFile()
            .forEachLine(Charsets.UTF_8) { it -> it.split("\\s+".toRegex())
                .forEach { state.add(it, file) }
            }
    }

    fun pathModified(file: Path) {
        removePath(file)
        addPathToIndex(file)
    }

    fun removePath(path: Path): Unit = when {
        path.isDirectoryToFollow() -> {
            Files.walk(path).forEach { it -> removePath(it) }
        }
        path.isFile() -> {
            state.removeFile(path)
        }
        else -> {
            Logger.debug("cannot remove since not a directory and not a file $path")
        }
    }

    fun getFilesWithWord(word: String): List<Path> {
        return state.getFilesForWork(word)
    }
}