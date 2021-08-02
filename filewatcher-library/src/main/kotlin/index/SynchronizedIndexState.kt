package index

import Logger
import watcher.isFile
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Dmitry Borodin on 7/19/21.
 */
internal class SynchronizedIndexState {
    private val state = ConcurrentHashMap<String, Set<Path>>()

    fun add(word: String, file: Path) {
        if (!file.isFile()) Logger.error("add file was not a file")
        state.compute(word) { word, value ->
            if (value == null) setOf(file) else HashSet(value).also { it -> it.add(file) }
        }
    }

    @Synchronized
    fun removeFile(file: Path) {
        if (!file.isFile()) Logger.error("add file was not a file")
        val iterator = state.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.value.contains(file)) {
                if (entry.value.size == 1) {
                    iterator.remove()
                } else {
                    val newSet = entry.value.filterTo(hashSetOf()) { it != file }
                    entry.setValue(newSet)
                }
            }
        }
    }

    fun getFilesForWork(word: String): List<Path> {
        //add check that this is word?
        return state.get(word)?.toList() ?: emptyList()
    }
}