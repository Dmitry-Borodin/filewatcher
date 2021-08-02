package index

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
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
        assertTrue(Files.notExists(Path.of(TEST_FOLDER)))
        val resources = createResourceFolder()
        assertTrue(Files.exists(Path.of(TEST_FOLDER)))
        val textFile = File(resources.toAbsolutePath().toString() + "/test.txt")
            .printWriter().use { out ->
                out.write("some boring text A")
            }
        deleteResourceFolder()
        assertTrue(Files.notExists(Path.of(TEST_FOLDER)))
    }
}