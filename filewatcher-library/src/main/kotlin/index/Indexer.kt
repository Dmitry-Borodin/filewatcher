package index

import java.nio.file.Path

/**
 * @author Dmitry Borodin on 8/6/21.
 */
interface Indexer {
    /**
     * Will follow symlinks
     * Can be called few times for the same file (like when added when original index was still in progress)
     */
    fun addPathToIndex(path: Path)
    fun pathModified(path: Path)
    fun removePath(path: Path): Unit
    fun getFilesWithWord(word: String): List<Path>
}