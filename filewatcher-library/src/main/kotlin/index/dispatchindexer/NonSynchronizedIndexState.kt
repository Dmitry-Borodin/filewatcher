package index.dispatchindexer

import Logger
import watcher.isFile
import java.nio.file.Path

/**
 * @author Dmitry Borodin on 7/19/21.
 *
 * Can be used from one stread only, but much faster
 */
internal class NonSynchronizedIndexState {
    private val state = HashMap<String, MutableSet<Path>>()  // word to list of files it's present

    fun add(word: String, file: Path) {
        if (!file.isFile()) Logger.error("add file was not a file")
        if (state[word] == null) {
            state[word] = mutableSetOf(file)
        } else {
            state[word]!!.add(file)
        }
    }

    fun removeFile(file: Path) {
        if (!file.isFile()) Logger.error("add file was not a file")

        val elementsPerThread = 1000L
        state.forEach { (word, files) ->
            if (files.contains(file)) {
                if (files.size == 1) {
                    state.remove(word)
                } else {
                    files.remove(file)
                }
            }
        }
    }

    fun getFilesForWork(word: String): List<Path> {
        //add check that this is word?
        return state.get(word)?.toList() ?: emptyList()
    }
}