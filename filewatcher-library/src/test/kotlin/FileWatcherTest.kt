import index.createResourceFolder
import index.deleteResourceFolder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
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
        val fileWatcher = FileWatcher()
        val testFolder = createResourceFolder()
        fileWatcher.addToIndex(testFolder)
        assert(fileWatcher.getFilesWithWord("boring").isEmpty())
        val textFile = File(testFolder.toAbsolutePath().toString() + "/test.txt")
            .printWriter().use { out ->
                out.write("some boring text A")
            }
        Thread.sleep(100) //todo add handle to FileWatcher to know if sync is finished. If possible.
        Assertions.assertEquals(1, fileWatcher.getFilesWithWord("boring").size)
    }

    @Test
    fun testHandleModifiedContent() {
        val fileWatcher = FileWatcher()
        val testFolder = createResourceFolder()
        val textFile = File(testFolder.toAbsolutePath().toString() + "/test.txt").apply {
            printWriter().use { out ->
                out.write("some boring text A")
            }
        }
        fileWatcher.addToIndex(testFolder)
        textFile.printWriter().use { out ->
            out.write("Completely Different Text")
        }
        Thread.sleep(100) //todo add handle to FileWatcher to know if sync is finished. If possible.
        assert(fileWatcher.getFilesWithWord("boring").isEmpty())
        assert(fileWatcher.getFilesWithWord("Text").isNotEmpty())
    }

    @Test
    fun testHandleRemovedContent() {
        val fileWatcher = FileWatcher()
        val testFolder = createResourceFolder()
        val textFile = File(testFolder.toAbsolutePath().toString() + "/test.txt").apply {
            printWriter().use { out ->
                out.write("some boring text A")
            }
        }
        fileWatcher.addToIndex(testFolder)
        textFile.delete()
        Thread.sleep(100) //todo add handle to FileWatcher to know if sync is finished. If possible.
        assert(fileWatcher.getFilesWithWord("boring").isEmpty())
    }
}
