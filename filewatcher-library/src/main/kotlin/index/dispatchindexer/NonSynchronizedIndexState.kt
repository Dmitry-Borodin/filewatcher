package index.dispatchindexer

import Logger
import watcher.isFile
import java.nio.file.Path
import java.util.*
import kotlin.collections.HashMap

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

    fun removeAllFilesIn(path: Path) {
        LinkedList(state.keys).forEach { word: String ->
            val files = state[word]!!
            val wasRemoved = files.removeIf { isFileContainsInPath(it, path) }
            if (wasRemoved && files.isEmpty()) {
                state.remove(word)
            }
        }
    }

    private fun isFileContainsInPath(file: Path, parent: Path): Boolean {
        if (parent.isFile()) {
            return file == parent
        }
        val parentPath = parent.toList()
        val filePath = file.toList()
        for (i: Int in parentPath.indices) {
            if (parentPath[i] != filePath[i]) return false
        }
        return true
    }

    fun getFilesForWork(word: String): List<Path> {
        //add check that this is word?
        return state.get(word)?.toList() ?: emptyList()
    }
}