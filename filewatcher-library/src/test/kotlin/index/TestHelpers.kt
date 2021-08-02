package index

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Dmitry Borodin on 8/1/21.
 */

private const val TEST_FOLDER = "test_resources"

fun createResourceFolder(): Path {
    return Files.createDirectory(Path.of(TEST_FOLDER))
}

fun deleteResourceFolder() {
    Path.of(TEST_FOLDER).toFile().deleteRecursively()
}

class UtilsTest {

    @BeforeEach
    fun setUp() {
        deleteResourceFolder()
    }

    @Test
    fun testPathCreatingWorks() {
        assert(Files.notExists(Path.of(TEST_FOLDER)))
        val resouces = createResourceFolder()
        assert(Files.exists(Path.of(TEST_FOLDER)))
        val textFile = File(resouces.toAbsolutePath().toString() + "/test.txt")
            .printWriter().use { out ->
                out.write("some boring text A")
            }
        deleteResourceFolder()
        assert(Files.notExists(Path.of(TEST_FOLDER)))
    }
}