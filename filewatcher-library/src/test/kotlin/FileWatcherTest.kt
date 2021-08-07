import index.createResourceFolder
import index.deleteResourceFolder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

/**
 * @author Dmitry Borodin on 8/2/21.
 */
internal class FileWatcherTest {

    private lateinit var fileWatcher: FileWatcher
    @BeforeEach
    fun setUp() {
        fileWatcher = FileWatcher()
        //in case if prev tests failed and left garbage
        deleteResourceFolder()
    }

    @AfterEach
    fun tearDown() {
        deleteResourceFolder()
    }

    @Test
    fun addPathToIndex() {
        val testFolder = createResourceFolder()
        fileWatcher.addToIndex(testFolder)
        assertTrue(fileWatcher.getFilesWithWord("boring").isEmpty())
        val textFile = File(testFolder.toAbsolutePath().toString() + "/test.txt").apply {
            printWriter().use { out ->
                out.write("some boring text A")
            }
        }
        Thread.sleep(100) //todo add handle to FileWatcher to know if sync is finished. If possible.
        assertEquals(1, fileWatcher.getFilesWithWord("boring").size)
    }

    @Test
    fun testHandleModifiedContent() {
        val testFolder = createResourceFolder()
        val textFile = File(testFolder.toAbsolutePath().toString() + "/test.txt").apply {
            printWriter().use { out ->
                out.write("some boring text A")
            }
        }
        fileWatcher.addToIndex(testFolder)
        assertTrue(fileWatcher.getFilesWithWord("boring").isNotEmpty())
        textFile.printWriter().use { out ->
            out.write("Completely Different Text")
        }
        Thread.sleep(100) //todo add handle to FileWatcher to know if sync is finished. If possible.
        assertTrue(fileWatcher.getFilesWithWord("boring").isEmpty())
        assertTrue(fileWatcher.getFilesWithWord("Different").isNotEmpty())
    }

    @Test
    fun testHandleRemovedContent() {
        val testFolder = createResourceFolder()
        val textFile = File(testFolder.toAbsolutePath().toString() + "/test.txt").apply {
            printWriter().use { out ->
                out.write("some boring text A")
            }
        }
        fileWatcher.addToIndex(testFolder)
        textFile.delete()
        Thread.sleep(100)
        assertEquals(true, fileWatcher.getFilesWithWord("boring").isEmpty())
    }

    @Test
    fun testHandleStopTracking() {
        val testFolder = createResourceFolder()
        val textFile = File(testFolder.toAbsolutePath().toString() + "/test.txt").apply {
            printWriter().use { out ->
                out.write("some boring text A")
            }
        }
        fileWatcher.addToIndex(testFolder)
        fileWatcher.removeFromIndex(listOf(testFolder))
        assertEquals(true, fileWatcher.getFilesWithWord("boring").isEmpty())
    }

    @Test
    fun canTrackIndividualFilesChanges() {
        val testFolder = createResourceFolder()
        val textFile = File(testFolder.toAbsolutePath().toString() + "/test.txt").apply {
            printWriter().use { out ->
                out.write("A")
            }
        }
        fileWatcher.addToIndex(textFile.toPath())
        textFile.printWriter().use { out ->
            out.write("some boring text A")
        }
        Thread.sleep(100) //so watcher can detect changes
        assertEquals(true, fileWatcher.getFilesWithWord("boring").isNotEmpty())
    }
}
