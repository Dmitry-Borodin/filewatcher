package watcher

import io.methvin.watcher.DirectoryChangeEvent
import io.methvin.watcher.DirectoryWatcher
import kotlinx.coroutines.*
import java.io.Closeable
import java.nio.file.Files
import java.nio.file.Path
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * @author Dmitry Borodin on 8/2/21.
 *
 * This class responsible for managing underlaying directory-watcher library
 * since it doesn't support changing folders, but we want to be able to add and remove
 * folders without forcing clients to re-initialize FileWatcher
 *
 */
internal class Watcher(private val callback: WatcherCallback) : Closeable, CoroutineScope by MainScope() {

    private var isClosed = false;
    private var watcher: DirectoryWatcher? = null
    private val watchedFolders: MutableSet<Path> = mutableSetOf()

    @Synchronized
    fun addPaths(foldersToAdd: List<Path>) {
        if (isClosed) return
        watchedFolders.addAll(foldersToAdd)
        refreshWatcherLibrary()
    }

    @OptIn(ExperimentalTime::class)
    fun refreshWatcherLibrary() {
        Logger.debug("Started watcher refreshing")
        watcher?.close()
        watcher = DirectoryWatcher.builder()
            .paths(watchedFolders.toList())
            .listener { dirChangeEvent ->
                when (dirChangeEvent.eventType()!!) {
                    DirectoryChangeEvent.EventType.CREATE -> callback.onCreated(path = dirChangeEvent.path())
                    DirectoryChangeEvent.EventType.MODIFY -> callback.onModified(path = dirChangeEvent.path())
                    DirectoryChangeEvent.EventType.DELETE -> callback.onDeleted(path = dirChangeEvent.path())
                    DirectoryChangeEvent.EventType.OVERFLOW -> {
                        //overflow occured and some events may lost, we need to recalculate index
                        launch(Dispatchers.IO) {
                            delay(Duration.Companion.minutes(1))
                            addPaths(emptyList())
                        }
                    }
                }
            }
            //this method blocks for 6 sec when 40k folders, but otherwise we may miss changes between it init() call and this method completes
            .build()
            .also { it.watchAsync() }
        Logger.debug("Finish watcher refreshing")
    }

    /**
     * Currently doesn't support child folders e.q. you cannot exclude subfolder from watched parent folder
     * Doesn't update watcher, call refreshWatcherLibrary() after all changes complete
     */
    @Synchronized
    fun removeFolderPreparation(folderToRemove: Path): Boolean {
        if (isClosed) return false
        return watchedFolders.remove(folderToRemove)
    }

    @Synchronized
    override fun close() {
        isClosed = true
        watcher?.close()
        cancel()
    }

    internal interface WatcherCallback {
        fun onCreated(path: Path)
        fun onModified(path: Path)
        fun onDeleted(path: Path)
    }
}

internal fun Path.isFile(): Boolean = Files.isRegularFile(this)
internal fun Path.isDirectory(): Boolean = Files.isDirectory(this)
