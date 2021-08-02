package watcher

import io.methvin.watcher.DirectoryChangeEvent
import io.methvin.watcher.DirectoryWatcher
import java.io.Closeable
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Dmitry Borodin on 8/2/21.
 *
 * This class responsible for managing underlaying directory-watcher library
 * since it doesn't support changing folders, but we want to be able to add and remove
 * folders without forcing clients to re-initialize FileWatcher
 *
 */
internal class Watcher(private val callback: WatcherCallback) : Closeable {

    private var isClosed = false;
    private var watcher: DirectoryWatcher? = null
    private val watchedFolders: MutableSet<Path> = mutableSetOf()

    @Synchronized
    fun addPaths(foldersToAdd: List<Path>) {
        if (isClosed) return
        watchedFolders.addAll(foldersToAdd)
        refreshWatcherLibrary()
    }

    private fun refreshWatcherLibrary() {
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
                        //todo add delay to not overload already overflown system
                        addPaths(emptyList())
                    }
                }
            }
            .build()
            .also { it.watchAsync() }
    }

    /**
     * Currently doesn't support child folders e.q. you cannot exclude subfolder from watched parent folder
     */
    @Synchronized
    fun removeFolders(foldersToRemove: List<Path>) {
        if (isClosed) return
        watchedFolders.removeAll(foldersToRemove)
        refreshWatcherLibrary()
    }

    @Synchronized
    override fun close() {
        isClosed = true
        watcher?.close()
    }

    internal interface WatcherCallback {
        fun onCreated(path: Path)
        fun onModified(path: Path)
        fun onDeleted(path: Path)
    }
}

internal fun Path.isFile(): Boolean = Files.isRegularFile(this)
internal fun Path.isDirectoryToFollow(): Boolean = Files.isDirectory(this) || Files.isSymbolicLink(this)
