package index.dispatchindexer

import Logger
import index.FileValidator
import index.Indexer
import kotlinx.coroutines.*
import watcher.isDirectory
import java.io.Closeable
import java.nio.file.Path
import java.util.concurrent.Executors
import kotlin.io.path.isHidden
import kotlin.io.path.listDirectoryEntries

/**
 * @author Dmitry Borodin on 7/19/21.
 *
 * Sync free and copy free indexer, that dispatching all requests to a backup thread.
 * Price for that is it's single thread only, blocking calling thread and executing all requests in internal thread
 *
 */
internal class DispatchIndexer : Indexer, Closeable, CoroutineScope by MainScope() {
    private val state = NonSynchronizedIndexState()
    private val wordsInLineRegex = "\\s+".toRegex()
    private val singleThreadDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val textValidator = FileValidator()

    override fun addPathToIndex(path: Path) {
        launch(singleThreadDispatcher) {
            addPathToIndexInternal(path)
        }
    }

    /**
     * Internal thread only!
     */
    private fun addPathToIndexInternal(path: Path) {
        if (!path.toFile().canRead()) return

        when {
            path.isDirectory() -> {
                Logger.addingFolder(path)
                path.listDirectoryEntries()
//                    .parallel() //that makes it async but doesn't improve performance with current indexer
                    .forEach { it ->
                        addPathToIndexInternal(it)
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

    /**
     * Internal thread only!
     */
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
        launch(singleThreadDispatcher) {
            removePathInternal(path)
            addPathToIndexInternal(path)
        }
    }

    override fun removePath(path: Path) {
        launch(singleThreadDispatcher) {
            Logger.debug("removing path started for path $path")
            removePathInternal(path)
            Logger.debug("removing path finish for path $path")
        }
    }

    /**
     * Internal thread only!
     */
    private fun removePathInternal(path: Path) {
        when {
            path.isDirectory() -> {
                path.listDirectoryEntries()
                    .forEach { it -> removePathInternal(it) }
            }
            else -> {
                state.removeFile(path)
            }
        }
    }

    /**
     * TODO make dispatcher to not dispatch on main thread
     */
    override fun getFilesWithWord(word: String): List<Path> {
        return runBlocking(singleThreadDispatcher) {
            state.getFilesForWork(word)
        }
    }

    override fun close() {
        cancel()
    }
}