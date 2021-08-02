package index

import Logger
import watcher.isDirectoryToFollow
import watcher.isFile
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
    fun addPathToIndex(path: Path) {
        if (!path.toFile().canRead()) return

        when {
            path.isDirectoryToFollow() -> {
                Files
                    .walk(path)
                    .filter { it != path }
                    .forEach { it ->
                        addPathToIndex(it)
                    }
            }
            //todo path.isFile() is false for non absolute path, fix it and print error for else, for example if have no permission to read
            else -> {
                val type = Files.probeContentType(path)
                if (type.contains("text")) {
                    addTextFileToIndex(path)
                } else {
                    //nothing
                }
            }
        }
    }

    private fun addTextFileToIndex(file: Path) {
        if (!file.toFile().canRead()) return

        file
            .toFile()
            .forEachLine(Charsets.UTF_8) { it ->
                it.split("\\s+".toRegex())
                    .forEach { state.add(it, file) }
            }
    }

    fun pathModified(file: Path) {
        removePath(file)
        addPathToIndex(file)
    }

    fun removePath(path: Path): Unit = when {
        path.isDirectoryToFollow() -> {
            Files
                .walk(path)
                .filter { it != path }
                .forEach { it -> removePath(it) }
        }
        else -> {
            state.removeFile(path)
        }
    }

    fun getFilesWithWord(word: String): List<Path> {
        return state.getFilesForWork(word)
    }
}