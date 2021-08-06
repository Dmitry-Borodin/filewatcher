import java.io.File
import java.lang.RuntimeException

fun main(args: Array<String>) {

        //given we have a folder with content
        val folder = File("../../other/intellij-community/")
        if (!folder.exists()) {
            throw RuntimeException("coudn't find directory")
        }

        println("starting index")
        //we can start tracking it's content
        val fileWatcher = FileWatcher()
        fileWatcher.addToIndex(folder.toPath())

    println("index finished")
        //now we can get files by word, that is contained in watched folder
        val foundFiles = fileWatcher.getFilesWithWord("sample")
        assert(foundFiles.size == 1)
        assert(foundFiles[0].toAbsolutePath().toString().contains("sample.txt", false)) //this path can be a full path, not related we used to create a file

        //if content is modified while on background
        File(folder.path.toString() + "/sample.txt")
            .printWriter().use { out ->
                out.write("some another characters")
            }
        //after some short time changes will be noticed and re-indexed
        Thread.sleep(100)

        //and we see changes in fileWatcher results
        assert(fileWatcher.getFilesWithWord("sample").isEmpty())
        assert(fileWatcher.getFilesWithWord("another").isNotEmpty())

        //can stop tracking if we don't need it anymore, to save resources on keeping index up to date
        fileWatcher.close()

        //cleanup after this sample run
        folder.deleteRecursively()
}