package index

/**
 * @author Dmitry Borodin on 7/19/21.
 */

data class IndexModel(val files : Set<FileModel> = emptySet())

data class FileModel (val words: HashSet<String>)