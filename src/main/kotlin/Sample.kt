import java.io.File

/**
 * @author Dmitry Borodin on 8/2/21.
 */
fun main() {

    //given we have a folder with content
    val folder = File("Sample folder")
    if (!folder.exists()) {
        folder.mkdir()
    }

    File(folder.path.toString() + "/sample.txt")
        .printWriter().use { out ->
            out.write("some sample characters")
        }

    //we can start tracking it's content
    val fileWatcher = FileWatcher()
    fileWatcher.addToIndex(folder.toPath())

    //now we can get files by word, that is contained in watched folder
    val foundFiles = fileWatcher.getFilesWithWord("sample")
    assert(foundFiles.size == 1)
    assert(foundFiles[0].toAbsolutePath().toString() == "Sample folder/sample.txt")

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