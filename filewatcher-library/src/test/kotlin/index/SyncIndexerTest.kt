package index

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File


/**
 * @author Dmitry Borodin on 8/1/21.
 */
internal class SyncIndexerTest {

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
        val indexer = SyncIndexer()
        val testFolder = createResourceFolder()
        indexer.addPathToIndex(testFolder)
        assertTrue(indexer.getFilesWithWord("boring").isEmpty())
        val textFile = File(testFolder.toAbsolutePath().toString() + "/test.txt")
            .printWriter().use { out ->
                out.write("some boring text A")
            }
        indexer.addPathToIndex(testFolder)
        assertEquals(1, indexer.getFilesWithWord("boring").size)
    }

    @Test
    fun pathModified() {
        //if FileWatcher will support more than one Indexer - copy tests from there
    }

    @Test
    fun removePath() {
        //if FileWatcher will support more than one Indexer - copy tests from there
    }
}