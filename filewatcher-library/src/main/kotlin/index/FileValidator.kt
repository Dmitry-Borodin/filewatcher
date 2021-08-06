package index

import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Dmitry Borodin on 8/6/21.
 */
class FileValidator {

    //some .kt files are not text files according to this method. Fixme
    fun isTestFile(path: Path): Boolean {
        val type: String? = Files.probeContentType(path)
        return type?.contains("text") == true
    }
}