import index.SyncIndexer
import io.methvin.watcher.DirectoryChangeEvent
import io.methvin.watcher.DirectoryWatcher
import watcher.Watcher
import java.io.Closeable
import java.io.File
import java.lang.Exception
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
     * Returns list of files among indexed resources that contain substring passed
     */
    fun getFilesWithWord(substring: String): List<Path> {
        return indexer.getFilesWithWord(substring)
    }

    fun addToIndex(folder: Path) {
        indexer.addPathToIndex(folder)
        watcher.addFolders(listOf(folder))
    }

    override fun close() {
        watcher.close()
    }
}