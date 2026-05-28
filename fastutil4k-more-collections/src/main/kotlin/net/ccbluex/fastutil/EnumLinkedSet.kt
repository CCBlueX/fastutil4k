package net.ccbluex.fastutil

import it.unimi.dsi.fastutil.objects.AbstractReferenceSortedSet
import it.unimi.dsi.fastutil.objects.ObjectIterators
import it.unimi.dsi.fastutil.objects.ObjectListIterator
import java.util.function.Consumer
import java.util.function.Predicate

/**
 * A type-specific linked set for enum types that maintains insertion order.
 *
 * Elements are iterated in the order they were first added. Adding an element that
 * is already present does not change its position. This class extends
 * [AbstractReferenceSortedSet] for compatibility with fastutil's type-specific
 * hierarchy, analogous to [it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet].
 *
 * Internally uses a bit-vector for O(1) membership tests and a doubly-linked list
 * indexed by ordinal for insertion order. Supports up to 65536 enum constants.
 *
 * [comparator] always returns `null` and [subSet], [headSet], [tailSet] throw
 * [UnsupportedOperationException], since this is a linked (insertion-ordered) set.
 *
 * Provides [indexOf] for positional lookup within the insertion order.
 *
 * Null elements are not supported: [add]`(null)`, [contains]`(null)` and
 * [remove]`(null)` all return `false`.
 *
 * This class is not thread-safe.
 *
 * @param E the enum type
 * @param enumConstants the enum constants array, typically obtained via `E.values()`
 * @throws IllegalArgumentException if the enum has more than 65536 constants
 */
class EnumLinkedSet<E : Enum<E>> private constructor(
    private val enumConstants: Array<E>,
) : AbstractReferenceSortedSet<E>(),
    Cloneable {

    private val n = enumConstants.size

    init {
        require(n <= 65536) {
            "EnumLinkedSet supports at most 65536 enum constants, got $n"
        }
    }

    constructor(type: Class<E>) : this(type.enumConstants)

    private val bits = LongArray((n + 63) ushr 6)
    private val link = LongArray(n)

    private var first: Int = -1
    private var last: Int = -1
    override var size: Int = 0
        private set

    override fun isEmpty(): Boolean = size == 0

    private fun containsOrdinal(ord: Int): Boolean = (bits[ord ushr 6] and (1L shl (ord and 63))) != 0L

    private fun setBit(ord: Int) {
        bits[ord ushr 6] = bits[ord ushr 6] or (1L shl (ord and 63))
    }

    private fun clearBit(ord: Int) {
        bits[ord ushr 6] = bits[ord ushr 6] and (1L shl (ord and 63)).inv()
    }

    private inline fun linkNext(ord: Int): Int = link[ord].toInt()

    private inline fun linkPrev(ord: Int): Int = (link[ord] ushr 32).toInt()

    private inline fun linkSetNext(ord: Int, next: Int) {
        link[ord] = (link[ord] and (-0x1_0000_0000L)) or (next.toLong() and 0xFFFFFFFFL)
    }

    private inline fun linkSetPrev(ord: Int, prev: Int) {
        link[ord] = (link[ord] and 0xFFFFFFFFL) or (prev.toLong() shl 32)
    }

    private fun linkAtEnd(ord: Int) {
        if (last >= 0) {
            linkSetNext(last, ord)
            link[ord] = (last.toLong() shl 32) or 0xFFFFFFFFL
        } else {
            first = ord
            link[ord] = -1L
        }
        last = ord
    }

    private fun unlink(ord: Int) {
        val p = linkPrev(ord)
        val n = linkNext(ord)

        if (p >= 0) linkSetNext(p, n) else first = n
        if (n >= 0) linkSetPrev(n, p) else last = p
    }

    private fun removeOrdinal(ord: Int) {
        unlink(ord)
        clearBit(ord)
        size--
    }

    private fun moveIndexToFirst(ord: Int) {
        if (size == 1 || first == ord) return
        if (last == ord) {
            last = linkPrev(ord)
            linkSetNext(last, -1)
        } else {
            val prev = linkPrev(ord)
            val next = linkNext(ord)
            linkSetNext(prev, next)
            linkSetPrev(next, prev)
        }
        linkSetPrev(first, ord)
        link[ord] = (-1L shl 32) or (first.toLong() and 0xFFFFFFFFL)
        first = ord
    }

    private fun moveIndexToLast(ord: Int) {
        if (size == 1 || last == ord) return
        if (first == ord) {
            first = linkNext(ord)
            linkSetPrev(first, -1)
        } else {
            val prev = linkPrev(ord)
            val next = linkNext(ord)
            linkSetNext(prev, next)
            linkSetPrev(next, prev)
        }
        linkSetNext(last, ord)
        link[ord] = (last.toLong() shl 32) or 0xFFFFFFFFL
        last = ord
    }

    override fun add(k: E?): Boolean {
        if (k == null) return false
        val ord = k.ordinal
        if (containsOrdinal(ord)) return false

        setBit(ord)
        linkAtEnd(ord)
        size++
        return true
    }

    override fun remove(k: E?): Boolean {
        val ord = enumOrdinalOf(k)
        if (ord < 0 || !containsOrdinal(ord)) return false
        removeOrdinal(ord)
        return true
    }

    override fun contains(k: E?): Boolean {
        val ord = enumOrdinalOf(k)
        return ord >= 0 && containsOrdinal(ord)
    }

    /**
     * Adds [k] to this set and moves it to the front of the insertion order.
     *
     * If [k] is already present, it is only moved without changing [size].
     *
     * @param k the element to add or move to the front
     * @return `true` if [k] was not already present, `false` if it was only moved
     */
    fun addAndMoveToFirst(k: E): Boolean {
        val ord = k.ordinal
        if (containsOrdinal(ord)) {
            moveIndexToFirst(ord)
            return false
        }
        setBit(ord)
        if (size == 0) {
            first = ord
            last = ord
            link[ord] = -1L
        } else {
            linkSetPrev(first, ord)
            link[ord] = (-1L shl 32) or (first.toLong() and 0xFFFFFFFFL)
            first = ord
        }
        size++
        return true
    }

    /**
     * Adds [k] to this set and moves it to the front of the insertion order.
     *
     * Convenience bridge for compatibility with [java.util.SequencedSet].
     * Delegates to [addAndMoveToFirst].
     *
     * @param k the element to add or move to the front
     */
    fun addFirst(k: E) {
        addAndMoveToFirst(k)
    }

    /**
     * Adds [k] to this set and moves it to the end of the insertion order.
     *
     * If [k] is already present, it is only moved without changing [size].
     *
     * @param k the element to add or move to the end
     * @return `true` if [k] was not already present, `false` if it was only moved
     */
    fun addAndMoveToLast(k: E): Boolean {
        val ord = k.ordinal
        if (containsOrdinal(ord)) {
            moveIndexToLast(ord)
            return false
        }
        setBit(ord)
        if (size == 0) {
            first = ord
            last = ord
            link[ord] = -1L
        } else {
            linkSetNext(last, ord)
            link[ord] = (last.toLong() shl 32) or 0xFFFFFFFFL
            last = ord
        }
        size++
        return true
    }

    /**
     * Adds [k] to this set and moves it to the end of the insertion order.
     *
     * Convenience bridge for compatibility with [java.util.SequencedSet].
     * Delegates to [addAndMoveToLast].
     *
     * @param k the element to add or move to the end
     */
    fun addLast(k: E) {
        addAndMoveToLast(k)
    }

    /**
     * Removes and returns the first element in insertion order.
     *
     * @return the first element
     * @throws NoSuchElementException if this set is empty
     */
    fun removeFirst(): E {
        if (size == 0) throw NoSuchElementException()
        val ord = first
        removeOrdinal(ord)
        return enumConstants[ord]
    }

    /**
     * Removes and returns the last element in insertion order.
     *
     * @return the last element
     * @throws NoSuchElementException if this set is empty
     */
    fun removeLast(): E {
        if (size == 0) throw NoSuchElementException()
        val ord = last
        removeOrdinal(ord)
        return enumConstants[ord]
    }

    /**
     * Returns the index of [element] in insertion order, or -1 if not present.
     *
     * The first inserted element has index 0, the second has index 1, etc.
     * Runs in O(k) where k is the number of elements in this set.
     *
     * @param element the element to locate
     * @return the insertion-order index of [element], or -1 if not in this set
     */
    fun indexOf(element: E): Int {
        var idx = 0
        var cur = first
        while (cur >= 0) {
            if (enumConstants[cur] === element) return idx
            idx++
            cur = linkNext(cur)
        }
        return -1
    }

    // SortedSet

    override fun first(): E {
        if (size == 0) throw NoSuchElementException()
        return enumConstants[first]
    }

    override fun last(): E {
        if (size == 0) throw NoSuchElementException()
        return enumConstants[last]
    }

    override fun comparator(): Comparator<in E>? = null

    override fun subSet(from: E, to: E) = throw UnsupportedOperationException()

    override fun headSet(to: E) = throw UnsupportedOperationException()

    override fun tailSet(from: E) = throw UnsupportedOperationException()

    override fun clear() {
        bits.fill(0L)
        first = -1
        last = -1
        size = 0
    }

    override fun iterator(): ObjectListIterator<E> = if (size == 0) emptyIterator() else SetIterator(first, -1, 0, -1)

    override fun iterator(from: E): ObjectListIterator<E> {
        val ord = from.ordinal
        if (ord >= n || !containsOrdinal(ord)) {
            throw NoSuchElementException("The element $from does not belong to this set.")
        }
        val idx = indexOf(from)
        return SetIterator(linkNext(ord), ord, idx + 1, idx)
    }

    override fun hashCode(): Int {
        var h = 0
        var cur = first

        while (cur >= 0) {
            h += System.identityHashCode(enumConstants[cur])
            cur = linkNext(cur)
        }

        return h
    }

    override fun toString(): String {
        val s = StringBuilder()
        var cur = first
        var isFirst = true
        s.append('{')
        while (cur >= 0) {
            if (isFirst) {
                isFirst = false
            } else {
                s.append(", ")
            }

            s.append(enumConstants[cur])
            cur = linkNext(cur)
        }
        s.append('}')
        return s.toString()
    }

    private inline fun removeIfInlined(filter: (E) -> Boolean): Boolean {
        var modified = false

        var cur = first
        while (cur >= 0) {
            val next = linkNext(cur)

            if (filter(enumConstants[cur])) {
                removeOrdinal(cur)
                modified = true
            }

            cur = next
        }

        return modified
    }

    override fun removeIf(filter: Predicate<in E>): Boolean = removeIfInlined(filter::test)

    override fun forEach(action: Consumer<in E>) {
        var cur = first
        while (cur >= 0) {
            action.accept(enumConstants[cur])
            cur = linkNext(cur)
        }
    }

    override fun retainAll(c: Collection<E?>): Boolean = removeIfInlined { !c.contains(it) }

    override fun removeAll(c: Collection<E?>): Boolean = removeIfInlined(c::contains)

    public override fun clone(): EnumLinkedSet<E> {
        val cloned = EnumLinkedSet(enumConstants)
        bits.copyInto(cloned.bits)
        link.copyInto(cloned.link)
        cloned.first = first
        cloned.last = last
        cloned.size = size
        return cloned
    }

    private fun enumOrdinalOf(k: Any?): Int {
        if (k !is Enum<*>) return -1
        val ord = k.ordinal
        if (ord >= n) return -1
        if (enumConstants[ord] !== k) return -1
        return ord
    }

    @Suppress("UNCHECKED_CAST")
    private fun emptyIterator(): ObjectListIterator<E> = ObjectIterators.EMPTY_ITERATOR as ObjectListIterator<E>

    private inner class SetIterator(
        initNext: Int,
        initPrev: Int,
        initNextIdx: Int,
        initPrevIdx: Int,
    ) : ObjectListIterator<E> {

        private var nextOrd: Int = initNext
        private var prevOrd: Int = initPrev
        private var lastOrd: Int = -1
        private var nextIdx: Int = initNextIdx
        private var prevIdx: Int = initPrevIdx

        override fun hasNext(): Boolean = nextOrd >= 0

        override fun hasPrevious(): Boolean = prevOrd >= 0

        override fun next(): E {
            if (!hasNext()) throw NoSuchElementException()
            lastOrd = nextOrd
            prevOrd = nextOrd
            prevIdx = nextIdx
            val currentNext = linkNext(nextOrd)
            nextOrd = currentNext
            nextIdx++
            return enumConstants[lastOrd]
        }

        override fun previous(): E {
            if (!hasPrevious()) throw NoSuchElementException()
            lastOrd = prevOrd
            nextOrd = prevOrd
            nextIdx = prevIdx
            prevIdx--
            prevOrd = linkPrev(prevOrd)
            return enumConstants[lastOrd]
        }

        override fun nextIndex(): Int = nextIdx

        override fun previousIndex(): Int = prevIdx

        override fun remove() {
            if (lastOrd < 0) throw IllegalStateException()
            if (lastOrd == prevOrd) {
                prevOrd = linkPrev(lastOrd)
                prevIdx--
                nextIdx--
            } else {
                nextOrd = linkNext(lastOrd)
            }
            removeOrdinal(lastOrd)
            lastOrd = -1
        }
    }

    companion object {
        /**
         * Creates a new empty [EnumLinkedSet] for the given enum type.
         *
         * Usage: `EnumLinkedSet<Color>()`
         *
         * @param E the enum type
         * @return a new empty [EnumLinkedSet]
         */
        inline operator fun <reified E : Enum<E>> invoke(): EnumLinkedSet<E> = EnumLinkedSet(E::class.java)
    }
}
