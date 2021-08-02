package index

import watcher.isFile
import java.io.File
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Dmitry Borodin on 7/19/21.
 */
internal class SynchronizedIndexState {
    private val state = ConcurrentHashMap<String, Set<Path>>()

    @Synchronized //todo remove synchronized
    fun add(word: String, file: Path) {
        if (!file.isFile) Logger.error("add file was not a file")
//        state.putIfAbsent()
        val files = state[word]
        if (files == null) {
            state.put(word, setOf(file))
        } else {
            val newSet = HashSet(files).also { it -> it.add(file) }
            state.put(word, newSet)
        }
    }

    @Synchronized
    fun removeFile(file: Path) {
        if (!file.isFile) Logger.error("add file was not a file")
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