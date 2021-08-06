package index.syncindexer

import Logger
import watcher.isFile
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Dmitry Borodin on 7/19/21.
 */
internal class SynchronizedIndexState {
    private val state = ConcurrentHashMap<String, Set<Path>>()  // word to list of files it's present

    fun add(word: String, file: Path) {
        if (!file.isFile()) Logger.error("add file was not a file")
        state.compute(word) { word, value ->
            if (value == null) setOf(file) else HashSet(value).also { it -> it.add(file) }
        }
    }

    //todo write test for parallel modification to check consistency
    fun removeFile(file: Path) {
        if (!file.isFile()) Logger.error("add file was not a file")

        val elementsPerThread = 1000L
        state.forEach(elementsPerThread) { word, files ->
            if (files.contains(file)) {
                if (files.size == 1) {
                    state.remove(word)
                } else {
                    val newSet = files.filterTo(hashSetOf()) { it != file }
                    state[word] = newSet
                }
            }
        }
    }

    fun getFilesForWork(word: String): List<Path> {
        //add check that this is word?
        return state.get(word)?.toList() ?: emptyList()
    }
}