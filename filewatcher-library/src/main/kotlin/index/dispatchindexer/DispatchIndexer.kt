package index.dispatchindexer

import index.syncindexer.SynchronizedIndexState

/**
 * @author Dmitry Borodin on 7/19/21.
 *
 * Sync free and copy free indexer... that dispatching all requests to a backup thread.
 *
 */
internal class DispatchIndexer {
    private val state = SynchronizedIndexState()
    private val wordsInLineRegex = "\\s+".toRegex()


}