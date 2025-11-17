package net.ccbluex.fastutil

import java.util.function.Predicate

class AtomicSortedList<E : Any> : Iterable<E> {

    fun lazyAdd(element: E) {
        TODO()
    }

    fun flush() {
        TODO()
    }

    fun remove(element: E): Boolean {
        TODO()
    }

    fun bulkRemove(predicate: Predicate<in E>) {
        TODO()
    }

    fun clear() {
        TODO()
    }

    override fun iterator(): Iterator<E> {
        TODO("Not yet implemented")
    }
}