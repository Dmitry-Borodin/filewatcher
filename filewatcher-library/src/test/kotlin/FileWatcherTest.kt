import index.SyncIndexer
import index.createResourceFolder
import index.deleteResourceFolder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

/**
 * @author Dmitry Borodin on 8/2/21.
 */
internal class FileWatcherTest {

    @BeforeEach
    fun setUp() {
        //in case if prev tests failed and left garbage
        deleteResourceFolder()
    }

    @AfterEach
    fun tearDown() {
        deleteResourceFolder()
    }

    @Test
    fun addPathToIndex() {
        val indexer = FileWatcher()
        val testFolder = createResourceFolder()
        indexer.addToIndex(testFolder)
        assert(indexer.getFilesWithWord("boring").isEmpty())
        val textFile = File(testFolder.toAbsolutePath().toString() + "/test.txt")
            .printWriter().use { out ->
                out.write("some boring text A")
            }
        Thread.sleep(1000)
        Assertions.assertEquals(1, indexer.getFilesWithWord("boring").size)
    }
}
