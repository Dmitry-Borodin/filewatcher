package index

import index.dispatchindexer.DispatchIndexer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

/**
 * @author Dmitry Borodin on 8/7/21.
 */
internal class DispatchIndexerTest {

    lateinit var indexer: DispatchIndexer

    @BeforeEach
    fun setUp() {
        //in case if prev tests failed and left garbage
        indexer = DispatchIndexer()
        deleteResourceFolder()
    }

    @AfterEach
    fun tearDown() {
        indexer.close()
        deleteResourceFolder()
    }

    @Test
    fun addPathToIndex() {
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
        val testFolder = createResourceFolder()
        indexer.addPathToIndex(testFolder)
        assertTrue(indexer.getFilesWithWord("boring").isEmpty())
        val textFile = File(testFolder.toAbsolutePath().toString() + "/test.txt")
        textFile
            .printWriter().use { out ->
                out.write("some boring text A")
            }
        indexer.addPathToIndex(testFolder)
        textFile.printWriter().use { out ->
            out.write("new text")
        }
        indexer.pathModified(testFolder)
        assertEquals(0, indexer.getFilesWithWord("boring").size)
        assertEquals(1, indexer.getFilesWithWord("new").size)
    }

    @Test
    fun removePath() {

        val testFolder = createResourceFolder()
        indexer.addPathToIndex(testFolder)
        assertTrue(indexer.getFilesWithWord("boring").isEmpty())
        val textFile = File(testFolder.toAbsolutePath().toString() + "/test.txt")
            .printWriter().use { out ->
                out.write("some boring text A")
            }
        indexer.addPathToIndex(testFolder)
        assertEquals(1, indexer.getFilesWithWord("boring").size)
        indexer.removePath(testFolder)
        assertEquals(0, indexer.getFilesWithWord("boring").size)
    }
}