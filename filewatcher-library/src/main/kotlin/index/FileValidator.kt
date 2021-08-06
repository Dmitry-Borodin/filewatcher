package index

import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Dmitry Borodin on 8/6/21.
 */
class FileValidator {

    //Files.probeContentType checking file extension and not marking all text files.
    // For example works for *.java but not for *.kt Fixme find better solution
    fun isTextFile(path: Path): Boolean {
        val type: String? = Files.probeContentType(path)
//        if (  path.toString().contains(".kt") && !path.endsWith(".kt")) {
//            println("path ")
//        }
        return (type?.contains("text") == true) || path.toString().endsWith(".kt")
                || path.toString().endsWith(".xml")
    }
}