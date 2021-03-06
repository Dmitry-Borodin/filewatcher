import java.io.File
import java.lang.RuntimeException
import java.util.*
import kotlin.io.path.Path

/**
 * This is to see performance
 */
fun main(args: Array<String>) {

    //given we have a folder with content
    val folder = File("../../other/intellij-community/")
//    val folder = File("../../other/cloc/")
    if (!folder.exists()) {
        throw RuntimeException("coudn't find directory")
    }

    val start = Date()
    println("starting index")
    println("timestamp start $start")

    //we can start tracking it's content
    val fileWatcher = FileWatcher()
    fileWatcher.addToIndex(folder.toPath())

    println()
    println("index returned")
    val finish = Date()
    if (finish.time - start.time < 10_000) { //took longer than 10s
        println("timestamp finish " + finish.toString() + " it took " + (finish.time - start.time) + " ms")
    } else {
        println("timestamp finish " + finish.toString() + " it took " + (finish.time/1000 - start.time/1000) + " seconds")
    }

    //now we can get files by word, that is contained in watched folder
    val foundFiles = fileWatcher.getFilesWithWord("val")
    println()
    println("timestamp after getting files " + Date().toString())
    println("files with val amount is ${foundFiles.size}")

    fileWatcher.removeFromIndex(listOf(folder.toPath()))

    println("timestamp after removing " + Date().toString())

    //wait to see if some async output
    Thread.sleep(1000)
    //can stop tracking if we don't need it anymore, to save resources on keeping index up to date
    fileWatcher.close()
}