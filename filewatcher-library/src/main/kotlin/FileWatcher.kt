import index.SyncIndexer
import watcher.Watcher
import java.io.Closeable
import java.lang.RuntimeException
import java.nio.file.Path

/**
 * @author Dmitry Borodin on 7/18/21.
 */
class FileWatcher: Closeable {

    private val indexer: SyncIndexer = SyncIndexer()
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
     * Add folder content of which will be indexed. This will follow symbolic links.
     */
    fun addToIndex(folder: Path) {
        indexer.addPathToIndex(folder)
        watcher.addFolders(listOf(folder))
    }

    /**
     * Add folders content of which will be indexed. This will follow symbolic links.
     */
    fun addToIndex(folders: List<Path>) {
        folders.forEach { indexer.addPathToIndex(it) }
        watcher.addFolders(folders)
    }

    override fun close() {
        watcher.close()
    }
}