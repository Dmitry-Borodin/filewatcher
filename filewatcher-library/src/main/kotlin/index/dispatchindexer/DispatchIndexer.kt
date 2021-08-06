package index.dispatchindexer

import index.FileValidator
import index.Indexer
import index.syncindexer.SynchronizedIndexState
import kotlinx.coroutines.*
import java.io.Closeable
import java.nio.file.Path
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineScope
import watcher.isDirectory
import kotlin.io.path.isHidden
import kotlin.io.path.listDirectoryEntries

/**
 * @author Dmitry Borodin on 7/19/21.
 *
 * Sync free and copy free indexer... that dispatching all requests to a backup thread.
 *
 */
internal class DispatchIndexer : Indexer, Closeable, CoroutineScope by MainScope() {
    private val state = SynchronizedIndexState()
    private val wordsInLineRegex = "\\s+".toRegex()
    private val singleThreadDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val textValidator = FileValidator()

    override fun addPathToIndex(path: Path) {
        launch(singleThreadDispatcher) {
            if (!path.toFile().canRead()) return@launch
            if (path.isHidden()) return@launch

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
                    if (textValidator.isTestFile(path)) {
                        Logger.addingFile(path)
                        addTextFileToIndex(path)
                    }
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

    override fun removePath(path: Path) {
        launch(singleThreadDispatcher) {
            when {
                path.isDirectory() -> {
                    path.listDirectoryEntries()
                        .forEach { it -> removePath(it) }
                }
                else -> {
                    state.removeFile(path)
                }
            }
        }
    }


    override fun getFilesWithWord(word: String): List<Path> {
        return runBlocking(singleThreadDispatcher) {
            state.getFilesForWork(word)
        }
    }

    override fun close() {
        cancel()
    }
}