import java.io.File
import java.lang.RuntimeException
import java.util.*

/**
 * This is to see performance
 */
fun main(args: Array<String>) {

    //given we have a folder with content
    val folder = File("../../other/intellij-community/")
    if (!folder.exists()) {
        throw RuntimeException("coudn't find directory")
    }

    println("starting index")
    println("timestamp " + Date().toString())

    //we can start tracking it's content
    val fileWatcher = FileWatcher()
    fileWatcher.addToIndex(folder.toPath())

    println("index finished")
    println("timestamp " + Date().toString())

    //now we can get files by word, that is contained in watched folder
    val foundFiles = fileWatcher.getFilesWithWord("val")
    println("timestamp " + Date().toString())
    println("files with val amount is ${foundFiles.size}")
    println("And they are ${foundFiles}")

    //can stop tracking if we don't need it anymore, to save resources on keeping index up to date
    fileWatcher.close()
}