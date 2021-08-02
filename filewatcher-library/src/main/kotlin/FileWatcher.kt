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
            indexer.addPathToIndex(path.toFile())
        }

        override fun onModified(path: Path) {
            indexer.pathModified(path.toFile())
        }

        override fun onDeleted(path: Path) {
            indexer.removePath(path.toFile())
        }

    })

    /**
     * Returns list of files among indexed resources that contain substring passed
     */
    fun getFilesWithWord(substring: String): List<File> {
        return indexer.getFilesWithWord(substring)
    }

    fun addToIndex(folder: File) {
        indexer.addPathToIndex(folder)
        watcher.addFolders(listOf(folder.toPath()))
    }

    override fun close() {
        watcher.close()
    }
}