package index

import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Dmitry Borodin on 7/19/21.
 */
internal class SynchronizedIndexState {
    private val state = ConcurrentHashMap<String, Set<File>>()

    fun add(word: String, file: File) {
        if (!file.isFile) Logger.error("add file was not a file")
//        state.putIfAbsent()
        synchronized(state) {
            val files = state[word]
            if (files == null) {
                state.put(word, setOf(file))
            } else {
                val newSet = HashSet(files).also { it -> it.add(file) }
                state.put(word, newSet)
            }
        }
    }

    fun removeFile(file: File) {
        if (!file.isFile) Logger.error("add file was not a file")
        synchronized(state) {
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
    }

    fun getFilesForWork(word: String): List<File> {
        //add check that this is word?
        return state.get(word)?.toList() ?: emptyList()
    }
}