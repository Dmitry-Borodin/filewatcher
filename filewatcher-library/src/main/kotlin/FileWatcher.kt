import index.Indexer
import index.dispatchindexer.DispatchIndexer
import index.syncindexer.SyncIndexer
import watcher.Watcher
import java.io.Closeable
import java.nio.file.Path

/**
 * @author Dmitry Borodin on 7/18/21.
 */
class FileWatcher: Closeable {

    private val indexer: Indexer = DispatchIndexer()
    private val watcher: Watcher = Watcher(object : Watcher.WatcherCallback {

        override fun onCreated(path: Path) {
            indexer.addPathToIndex(path)
        }

        override fun onModified(path: Path) {
            indexer.pathModified(path)
        }

        override fun onDeleted(path: Path) {
            indexer.removePath(path)
        }

    })

    /**
     * Returns list of files among indexed resources that contain a word passed
     * @throws RuntimeException if not single word passed
     */
    fun getFilesWithWord(substring: String): List<Path> {
        if (substring.contains(" ")) throw RuntimeException("not a word requested - it contains space")
        return indexer.getFilesWithWord(substring)
    }

    /**
     * Add folder or file content of which will be indexed. This will follow symbolic links.
     */
    fun addToIndex(path: Path) {
        watcher.addPaths(listOf(path))
        indexer.addPathToIndex(path)
    }

    /**
     * Add folders or files content of which will be indexed. This will follow symbolic links.
     */
    fun addToIndex(folders: List<Path>) {
        watcher.addPaths(folders)
        folders.forEach { indexer.addPathToIndex(it) }
    }

    fun removeFromIndex(folders: List<Path>) {
        var somethingChanged = false
        folders.forEach {
            val removed = watcher.removeFolderPreparation(it)
            if (removed) {
                indexer.removePath(it)
                somethingChanged = true
            }
        }
        if (somethingChanged) {
            watcher.refreshWatcherLibrary()
        }
    }

    override fun close() {
        watcher.close()
    }
}