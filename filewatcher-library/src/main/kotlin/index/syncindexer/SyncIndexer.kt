package index.syncindexer

import index.FileValidator
import index.Indexer
import watcher.isDirectory
import java.nio.file.Path
import kotlin.io.path.isHidden
import kotlin.io.path.listDirectoryEntries

/**
 * @author Dmitry Borodin on 7/19/21.
 *
 * This indexer supports
 */
internal class SyncIndexer : Indexer {

    private val state = SynchronizedIndexState()
    private val wordsInLineRegex = "\\s+".toRegex()
    private val textValidator = FileValidator()

    /**
     * Will follow symlinks
     * Can be called few times for the same file (like when added when original index was still in progress)
     *
     */
    override fun addPathToIndex(path: Path) {
        if (!path.toFile().canRead()) return

        when {
            path.isDirectory() -> {
                Logger.addingFolder(path)
                path.listDirectoryEntries()
//                    .parallel() //that makes it async but doesn't improve performance with current indexer
                    .forEach { it ->
                        addPathToIndex(it)
                    }
            }
            //todo path.isFile() is false for non absolute path, fix it and print error for else, for example if have no permission to read
            else -> {
                if (textValidator.isTextFile(path)) {
                    Logger.addingFile(path)
                    addTextFileToIndex(path)
                }
            }
        }
    }

    private fun addTextFileToIndex(file: Path) {
        if (!file.toFile().canRead()) return

        file
            .toFile()
            .forEachLine(Charsets.UTF_8) { it ->
                it.split(wordsInLineRegex)
                    .forEach {
                        Logger.addingWord(file)
                        state.add(it, file)
                    }
            }
    }

    override fun pathModified(path: Path) {
        removePath(path)
        addPathToIndex(path)
    }

    override fun removePath(path: Path): Unit = when {
        path.isDirectory() -> {
            path.listDirectoryEntries()
                .forEach { it -> removePath(it) }
        }
        else -> {
            state.removeFile(path)
        }
    }

    override fun getFilesWithWord(word: String): List<Path> {
        return state.getFilesForWork(word)
    }

    override fun close() {
        //do nothing
    }
}