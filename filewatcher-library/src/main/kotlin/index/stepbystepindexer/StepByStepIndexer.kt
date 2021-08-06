package index.stepbystepindexer

/**
 * @author Dmitry Borodin on 8/6/21.
 *
 * Combination of sync indexer and dispatch indexer - updating on one thread, then merging to sync state.
 * Optimised for many requests from multiple threads. Avoiding context switching for read.
 */
class StepByStepIndexer {
}