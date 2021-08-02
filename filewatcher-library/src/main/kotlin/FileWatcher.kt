import index.SyncIndexer
import java.io.File
import java.lang.Exception

/**
 * @author Dmitry Borodin on 7/18/21.
 */
class FileWatcher {

    private val indexer: SyncIndexer = SyncIndexer()

    /**
     * Returns list of files among indexed resources that contain substring passed
     */
    fun getFilesForWord(substring: String): List<File> {
        return indexer.getFilesWithWord(substring)
    }

    fun addToIndex(folder: File) {
        indexer.addPathToIndex(folder)
    }



}