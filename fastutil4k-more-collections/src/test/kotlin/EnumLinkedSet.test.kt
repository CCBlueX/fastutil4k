package net.ccbluex.fastutil

import it.unimi.dsi.fastutil.objects.ObjectListIterator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

enum class Color { RED, GREEN, BLUE, YELLOW, CYAN, MAGENTA }

enum class LargeEnum {
    V000,
    V001,
    V002,
    V003,
    V004,
    V005,
    V006,
    V007,
    V008,
    V009,
    V010,
    V011,
    V012,
    V013,
    V014,
    V015,
    V016,
    V017,
    V018,
    V019,
    V020,
    V021,
    V022,
    V023,
    V024,
    V025,
    V026,
    V027,
    V028,
    V029,
    V030,
    V031,
    V032,
    V033,
    V034,
    V035,
    V036,
    V037,
    V038,
    V039,
    V040,
    V041,
    V042,
    V043,
    V044,
    V045,
    V046,
    V047,
    V048,
    V049,
    V050,
    V051,
    V052,
    V053,
    V054,
    V055,
    V056,
    V057,
    V058,
    V059,
    V060,
    V061,
    V062,
    V063,
    V064,
    V065,
    V066,
    V067,
    V068,
    V069,
    V070,
    V071,
    V072,
    V073,
    V074,
    V075,
    V076,
    V077,
    V078,
    V079,
}

class EnumLinkedSetTest {

    private fun newSet() = EnumLinkedSet<Color>()

    @Test
    fun `empty set has size zero`() {
        val set = newSet()
        assertEquals(0, set.size)
        assertTrue(set.isEmpty())
    }

    @Test
    fun `empty set iterator has no elements`() {
        val set = newSet()
        val it = set.iterator()
        assertFalse(it.hasNext())
        assertFalse(it.hasPrevious())
    }

    @Test
    fun `empty set first throws`() {
        val set = newSet()
        assertFailsWith<NoSuchElementException> { set.first() }
    }

    @Test
    fun `empty set last throws`() {
        val set = newSet()
        assertFailsWith<NoSuchElementException> { set.last() }
    }

    @Test
    fun `indexOf returns -1 for absent element`() {
        val set = newSet()
        assertEquals(-1, set.indexOf(Color.RED))
    }

    @Test
    fun `add returns true for new element`() {
        val set = newSet()
        assertTrue(set.add(Color.RED))
        assertEquals(1, set.size)
    }

    @Test
    fun `add returns false for duplicate`() {
        val set = newSet()
        set.add(Color.RED)
        assertFalse(set.add(Color.RED))
        assertEquals(1, set.size)
    }

    @Test
    fun `contains returns true for added element`() {
        val set = newSet()
        set.add(Color.RED)
        assertTrue(set.contains(Color.RED))
    }

    @Test
    fun `contains returns false for absent element`() {
        val set = newSet()
        set.add(Color.RED)
        assertFalse(set.contains(Color.BLUE))
    }

    @Test
    fun `elements iterate in insertion order`() {
        val set = newSet()
        set.add(Color.GREEN)
        set.add(Color.RED)
        set.add(Color.BLUE)

        val it = set.iterator()
        assertSame(Color.GREEN, it.next())
        assertSame(Color.RED, it.next())
        assertSame(Color.BLUE, it.next())
        assertFalse(it.hasNext())
    }

    @Test
    fun `duplicate add does not change insertion order`() {
        val set = newSet()
        set.add(Color.GREEN)
        set.add(Color.RED)
        set.add(Color.GREEN)

        val it = set.iterator()
        assertSame(Color.GREEN, it.next())
        assertSame(Color.RED, it.next())
        assertFalse(it.hasNext())
    }

    @Test
    fun `indexOf returns correct insertion index`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)

        assertEquals(0, set.indexOf(Color.RED))
        assertEquals(1, set.indexOf(Color.GREEN))
        assertEquals(2, set.indexOf(Color.BLUE))
    }

    @Test
    fun `indexOf after remove updates correctly`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)
        set.add(Color.YELLOW)

        set.remove(Color.GREEN)

        assertEquals(0, set.indexOf(Color.RED))
        assertEquals(-1, set.indexOf(Color.GREEN))
        assertEquals(1, set.indexOf(Color.BLUE))
        assertEquals(2, set.indexOf(Color.YELLOW))
    }

    @Test
    fun `remove returns true for present element`() {
        val set = newSet()
        set.add(Color.RED)
        assertTrue(set.remove(Color.RED))
        assertEquals(0, set.size)
    }

    @Test
    fun `remove returns false for absent element`() {
        val set = newSet()
        set.add(Color.RED)
        assertFalse(set.remove(Color.BLUE))
        assertEquals(1, set.size)
    }

    @Test
    fun `remove first element updates first`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)

        set.remove(Color.RED)

        assertSame(Color.GREEN, set.first())
        assertEquals(2, set.size)
    }

    @Test
    fun `remove last element updates last`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)

        set.remove(Color.BLUE)

        assertSame(Color.GREEN, set.last())
        assertEquals(2, set.size)
    }

    @Test
    fun `first returns first inserted element`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        assertSame(Color.RED, set.first())
    }

    @Test
    fun `last returns last inserted element`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        assertSame(Color.GREEN, set.last())
    }

    @Test
    fun `clear empties the set`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.clear()

        assertEquals(0, set.size)
        assertTrue(set.isEmpty())
        assertFalse(set.contains(Color.RED))
    }

    @Test
    fun `clone produces independent copy`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)

        val cloned = set.clone()
        assertEquals(2, cloned.size)
        assertTrue(cloned.contains(Color.RED))
        assertTrue(cloned.contains(Color.GREEN))

        cloned.add(Color.BLUE)
        assertEquals(2, set.size)
        assertEquals(3, cloned.size)
    }

    @Test
    fun `clone preserves insertion order`() {
        val set = newSet()
        set.add(Color.GREEN)
        set.add(Color.RED)
        set.add(Color.BLUE)

        val cloned = set.clone()
        val it = cloned.iterator()
        assertSame(Color.GREEN, it.next())
        assertSame(Color.RED, it.next())
        assertSame(Color.BLUE, it.next())
    }

    @Test
    fun `iterator from element starts at given element via previous`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)

        val it = set.iterator(Color.GREEN)
        assertTrue(it.hasPrevious())
        assertSame(Color.GREEN, it.previous())
        assertSame(Color.RED, it.previous())
        assertFalse(it.hasPrevious())
    }

    @Test
    fun `iterator from element next goes after given element`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)

        val it = set.iterator(Color.RED)
        assertTrue(it.hasNext())
        assertSame(Color.GREEN, it.next())
        assertSame(Color.BLUE, it.next())
        assertFalse(it.hasNext())
    }

    @Test
    fun `iterator from last element has no next`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)

        val it = set.iterator(Color.BLUE)
        assertFalse(it.hasNext())
        assertTrue(it.hasPrevious())
        assertSame(Color.BLUE, it.previous())
    }

    @Test
    fun `iterator from absent element throws`() {
        val set = newSet()
        set.add(Color.RED)
        assertFailsWith<NoSuchElementException> { set.iterator(Color.GREEN) }
    }

    @Test
    fun `bidirectional iterator can go backward`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)

        val it = set.iterator()
        it.next()
        it.next()
        it.next()

        assertTrue(it.hasPrevious())
        assertSame(Color.BLUE, it.previous())
        assertSame(Color.GREEN, it.previous())
        assertSame(Color.RED, it.previous())
        assertFalse(it.hasPrevious())
    }

    @Test
    fun `list iterator provides correct indices`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)

        val it = set.iterator() as ObjectListIterator
        assertEquals(0, it.nextIndex())
        assertEquals(-1, it.previousIndex())

        it.next()
        assertEquals(1, it.nextIndex())
        assertEquals(0, it.previousIndex())

        it.next()
        assertEquals(2, it.nextIndex())
        assertEquals(1, it.previousIndex())

        it.next()
        assertEquals(3, it.nextIndex())
        assertEquals(2, it.previousIndex())
    }

    @Test
    fun `comparator returns null`() {
        val set = newSet()
        assertEquals(null, set.comparator())
    }

    @Test
    fun `subSet throws UnsupportedOperationException`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.BLUE)
        assertFailsWith<UnsupportedOperationException> { set.subSet(Color.RED, Color.BLUE) }
    }

    @Test
    fun `headSet throws UnsupportedOperationException`() {
        val set = newSet()
        set.add(Color.RED)
        assertFailsWith<UnsupportedOperationException> { set.headSet(Color.RED) }
    }

    @Test
    fun `tailSet throws UnsupportedOperationException`() {
        val set = newSet()
        set.add(Color.RED)
        assertFailsWith<UnsupportedOperationException> { set.tailSet(Color.RED) }
    }

    @Test
    fun `addAll adds all elements preserving order`() {
        val set = newSet()
        set.addAll(listOf(Color.RED, Color.GREEN, Color.BLUE))

        assertEquals(3, set.size)
        val it = set.iterator()
        assertSame(Color.RED, it.next())
        assertSame(Color.GREEN, it.next())
        assertSame(Color.BLUE, it.next())
    }

    @Test
    fun `removeAll removes multiple elements`() {
        val set = newSet()
        set.addAll(listOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW))

        set.removeAll(listOf(Color.GREEN, Color.YELLOW))

        assertEquals(2, set.size)
        assertTrue(set.contains(Color.RED))
        assertTrue(set.contains(Color.BLUE))
        assertFalse(set.contains(Color.GREEN))
        assertFalse(set.contains(Color.YELLOW))
    }

    @Test
    fun `equals compares by membership only not order`() {
        val set1 = newSet()
        set1.add(Color.RED)
        set1.add(Color.GREEN)

        val set2 = newSet()
        set2.add(Color.GREEN)
        set2.add(Color.RED)

        assertEquals(set1, set2)
    }

    @Test
    fun `equals returns false for different elements`() {
        val set1 = newSet()
        set1.add(Color.RED)

        val set2 = newSet()
        set2.add(Color.GREEN)

        assertNotEquals(set1, set2)
    }

    @Test
    fun `hashCode is order-independent`() {
        val set1 = newSet()
        set1.add(Color.RED)
        set1.add(Color.GREEN)

        val set2 = newSet()
        set2.add(Color.GREEN)
        set2.add(Color.RED)

        assertEquals(set1.hashCode(), set2.hashCode())
    }

    @Test
    fun `removeFirst removes and returns first element`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)

        val removed = set.removeFirst()
        assertSame(Color.RED, removed)
        assertEquals(2, set.size)
        assertSame(Color.GREEN, set.first())
    }

    @Test
    fun `removeLast removes and returns last element`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)

        val removed = set.removeLast()
        assertSame(Color.BLUE, removed)
        assertEquals(2, set.size)
        assertSame(Color.GREEN, set.last())
    }

    @Test
    fun `addAndMoveToLast moves existing element to end`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)

        set.addAndMoveToLast(Color.RED)

        val it = set.iterator()
        assertSame(Color.GREEN, it.next())
        assertSame(Color.BLUE, it.next())
        assertSame(Color.RED, it.next())
    }

    @Test
    fun `addAndMoveToFirst moves existing element to front`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)

        set.addAndMoveToFirst(Color.BLUE)

        val it = set.iterator()
        assertSame(Color.BLUE, it.next())
        assertSame(Color.RED, it.next())
        assertSame(Color.GREEN, it.next())
    }

    @Test
    fun `iterator remove removes the last returned element`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)

        val it = set.iterator()
        it.next()
        it.next()
        it.remove()

        assertEquals(2, set.size)
        val remaining = mutableListOf<Color>()
        set.iterator().forEach { remaining.add(it) }
        assertEquals(listOf(Color.RED, Color.BLUE), remaining)
    }

    @Test
    fun `contains with Object parameter works for null`() {
        val set = newSet()
        set.add(Color.RED)
        assertFalse(set.contains(null as Any?))
    }

    @Test
    fun `remove with Object parameter works for null`() {
        val set = newSet()
        set.add(Color.RED)
        assertFalse(set.remove(null as Any?))
        assertEquals(1, set.size)
    }

    @Test
    fun `add null returns false`() {
        val set = newSet()
        assertFalse(set.add(null))
        assertEquals(0, set.size)
    }

    @Test
    fun `addFirst inserts new element at front`() {
        val set = newSet()
        set.add(Color.GREEN)
        set.add(Color.BLUE)
        set.addFirst(Color.RED)

        assertEquals(3, set.size)
        val it = set.iterator()
        assertSame(Color.RED, it.next())
        assertSame(Color.GREEN, it.next())
        assertSame(Color.BLUE, it.next())
    }

    @Test
    fun `addFirst moves existing element to front`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)

        set.addFirst(Color.BLUE)

        assertEquals(3, set.size)
        val it = set.iterator()
        assertSame(Color.BLUE, it.next())
        assertSame(Color.RED, it.next())
        assertSame(Color.GREEN, it.next())
    }

    @Test
    fun `addLast inserts new element at end`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.addLast(Color.BLUE)

        assertEquals(3, set.size)
        val it = set.iterator()
        assertSame(Color.RED, it.next())
        assertSame(Color.GREEN, it.next())
        assertSame(Color.BLUE, it.next())
    }

    @Test
    fun `addLast moves existing element to end`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)

        set.addLast(Color.RED)

        assertEquals(3, set.size)
        val it = set.iterator()
        assertSame(Color.GREEN, it.next())
        assertSame(Color.BLUE, it.next())
        assertSame(Color.RED, it.next())
    }

    @Test
    fun `removeIf removes matching elements`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)
        set.add(Color.YELLOW)

        val modified = set.removeIf { it == Color.RED || it == Color.BLUE }

        assertTrue(modified)
        assertEquals(2, set.size)
        assertFalse(set.contains(Color.RED))
        assertTrue(set.contains(Color.GREEN))
        assertFalse(set.contains(Color.BLUE))
        assertTrue(set.contains(Color.YELLOW))
    }

    @Test
    fun `removeIf returns false when no match`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)

        val modified = set.removeIf { it == Color.BLUE }

        assertFalse(modified)
        assertEquals(2, set.size)
    }

    @Test
    fun `forEach iterates all elements in order`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)

        val visited = mutableListOf<Color>()
        set.forEach { visited.add(it) }

        assertEquals(listOf(Color.RED, Color.GREEN, Color.BLUE), visited)
    }

    @Test
    fun `retainAll keeps only specified elements`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)
        set.add(Color.YELLOW)

        val modified = set.retainAll(listOf(Color.RED, Color.YELLOW))

        assertTrue(modified)
        assertEquals(2, set.size)
        assertTrue(set.contains(Color.RED))
        assertFalse(set.contains(Color.GREEN))
        assertFalse(set.contains(Color.BLUE))
        assertTrue(set.contains(Color.YELLOW))
    }

    @Test
    fun `toString empty set`() {
        val set = newSet()
        assertEquals("{}", set.toString())
    }

    @Test
    fun `toString preserves insertion order`() {
        val set = newSet()
        set.add(Color.GREEN)
        set.add(Color.RED)

        assertEquals("{GREEN, RED}", set.toString())
    }

    @Test
    fun `indexOf after addAndMoveToFirst`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)

        set.addAndMoveToFirst(Color.BLUE)

        assertEquals(0, set.indexOf(Color.BLUE))
        assertEquals(1, set.indexOf(Color.RED))
        assertEquals(2, set.indexOf(Color.GREEN))
    }

    @Test
    fun `indexOf after addAndMoveToLast`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)

        set.addAndMoveToLast(Color.RED)

        assertEquals(0, set.indexOf(Color.GREEN))
        assertEquals(1, set.indexOf(Color.BLUE))
        assertEquals(2, set.indexOf(Color.RED))
    }

    @Test
    fun `iterator from element provides correct nextIndex and previousIndex`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)

        val it = set.iterator(Color.GREEN) as ObjectListIterator
        assertEquals(2, it.nextIndex())
        assertEquals(1, it.previousIndex())
    }

    @Test
    fun `removeFirst on empty throws`() {
        val set = newSet()
        assertFailsWith<NoSuchElementException> { set.removeFirst() }
    }

    @Test
    fun `removeLast on empty throws`() {
        val set = newSet()
        assertFailsWith<NoSuchElementException> { set.removeLast() }
    }

    @Test
    fun `nextIndex and previousIndex after next remove`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)

        val it = set.iterator() as ObjectListIterator
        assertEquals(0, it.nextIndex())
        assertEquals(-1, it.previousIndex())

        it.next()
        it.remove()

        assertEquals(0, it.nextIndex())
        assertEquals(-1, it.previousIndex())
    }

    @Test
    fun `nextIndex and previousIndex after previous remove`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)

        val it = set.iterator() as ObjectListIterator
        it.next()
        it.next()
        it.previous()
        it.remove()

        assertEquals(1, it.nextIndex())
        assertEquals(0, it.previousIndex())
    }

    @Test
    fun `iterator remove first element preserves correct indices`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)

        val it = set.iterator() as ObjectListIterator
        assertSame(Color.RED, it.next())
        it.remove()

        assertEquals(0, it.nextIndex())
        assertEquals(-1, it.previousIndex())
    }

    @Test
    fun `iterator remove middle element via previous preserves correct indices`() {
        val set = newSet()
        set.add(Color.RED)
        set.add(Color.GREEN)
        set.add(Color.BLUE)
        set.add(Color.YELLOW)

        val it = set.iterator() as ObjectListIterator
        it.next()
        it.next()
        it.next()
        val returned = it.previous()
        assertSame(Color.BLUE, returned)
        it.remove()

        assertEquals(2, it.nextIndex())
        assertEquals(1, it.previousIndex())

        assertSame(Color.YELLOW, it.next())
        assertSame(Color.YELLOW, it.previous())
        assertSame(Color.GREEN, it.previous())
    }
}

class EnumLinkedSetLargeTest {

    private fun newSet() = EnumLinkedSet<LargeEnum>()

    @Test
    fun `add and contains across bitmask word boundary`() {
        val set = newSet()
        set.add(LargeEnum.V063)
        set.add(LargeEnum.V064)

        assertTrue(set.contains(LargeEnum.V063))
        assertTrue(set.contains(LargeEnum.V064))
        assertEquals(2, set.size)
    }

    @Test
    fun `elements iterate in insertion order with large enum`() {
        val set = newSet()
        set.add(LargeEnum.V070)
        set.add(LargeEnum.V005)
        set.add(LargeEnum.V065)

        val it = set.iterator()
        assertSame(LargeEnum.V070, it.next())
        assertSame(LargeEnum.V005, it.next())
        assertSame(LargeEnum.V065, it.next())
        assertFalse(it.hasNext())
    }

    @Test
    fun `indexOf works with large enum`() {
        val set = newSet()
        set.add(LargeEnum.V000)
        set.add(LargeEnum.V063)
        set.add(LargeEnum.V064)
        set.add(LargeEnum.V079)

        assertEquals(0, set.indexOf(LargeEnum.V000))
        assertEquals(1, set.indexOf(LargeEnum.V063))
        assertEquals(2, set.indexOf(LargeEnum.V064))
        assertEquals(3, set.indexOf(LargeEnum.V079))
    }

    @Test
    fun `remove across bitmask boundary updates positions`() {
        val set = newSet()
        set.add(LargeEnum.V063)
        set.add(LargeEnum.V064)
        set.add(LargeEnum.V065)

        set.remove(LargeEnum.V064)

        assertEquals(2, set.size)
        assertEquals(0, set.indexOf(LargeEnum.V063))
        assertEquals(-1, set.indexOf(LargeEnum.V064))
        assertEquals(1, set.indexOf(LargeEnum.V065))
    }

    @Test
    fun `removeFirst and removeLast with large enum`() {
        val set = newSet()
        set.add(LargeEnum.V010)
        set.add(LargeEnum.V020)
        set.add(LargeEnum.V030)

        assertSame(LargeEnum.V010, set.removeFirst())
        assertSame(LargeEnum.V030, set.removeLast())
        assertEquals(1, set.size)
        assertSame(LargeEnum.V020, set.first())
    }

    @Test
    fun `clone preserves all data across word boundary`() {
        val set = newSet()
        set.add(LargeEnum.V063)
        set.add(LargeEnum.V064)
        set.add(LargeEnum.V070)
        set.add(LargeEnum.V079)

        val cloned = set.clone()
        assertEquals(4, cloned.size)
        assertTrue(cloned.contains(LargeEnum.V063))
        assertTrue(cloned.contains(LargeEnum.V064))
        assertTrue(cloned.contains(LargeEnum.V070))
        assertTrue(cloned.contains(LargeEnum.V079))

        val it = cloned.iterator()
        assertSame(LargeEnum.V063, it.next())
        assertSame(LargeEnum.V064, it.next())
        assertSame(LargeEnum.V070, it.next())
        assertSame(LargeEnum.V079, it.next())
    }

    @Test
    fun `addAndMoveToFirst with large enum`() {
        val set = newSet()
        set.add(LargeEnum.V010)
        set.add(LargeEnum.V020)
        set.add(LargeEnum.V065)

        set.addAndMoveToFirst(LargeEnum.V065)

        val it = set.iterator()
        assertSame(LargeEnum.V065, it.next())
        assertSame(LargeEnum.V010, it.next())
        assertSame(LargeEnum.V020, it.next())
    }

    @Test
    fun `addAndMoveToLast with large enum`() {
        val set = newSet()
        set.add(LargeEnum.V010)
        set.add(LargeEnum.V020)
        set.add(LargeEnum.V065)

        set.addAndMoveToLast(LargeEnum.V010)

        val it = set.iterator()
        assertSame(LargeEnum.V020, it.next())
        assertSame(LargeEnum.V065, it.next())
        assertSame(LargeEnum.V010, it.next())
    }

    @Test
    fun `clear and reuse with large enum`() {
        val set = newSet()
        set.add(LargeEnum.V000)
        set.add(LargeEnum.V079)
        set.clear()

        assertEquals(0, set.size)
        assertTrue(set.isEmpty())

        set.add(LargeEnum.V005)
        set.add(LargeEnum.V010)
        assertEquals(2, set.size)
        assertEquals(0, set.indexOf(LargeEnum.V005))
        assertEquals(1, set.indexOf(LargeEnum.V010))
    }

    @Test
    fun `iterator from element with large enum`() {
        val set = newSet()
        set.add(LargeEnum.V000)
        set.add(LargeEnum.V040)
        set.add(LargeEnum.V079)

        val it = set.iterator(LargeEnum.V040)
        assertTrue(it.hasNext())
        assertSame(LargeEnum.V079, it.next())
        assertFalse(it.hasNext())
        assertTrue(it.hasPrevious())
        assertSame(LargeEnum.V079, it.previous())
        assertSame(LargeEnum.V040, it.previous())
    }

    @Test
    fun `addAll with large enum preserves order`() {
        val set = newSet()
        set.addAll(listOf(LargeEnum.V010, LargeEnum.V064, LargeEnum.V079, LargeEnum.V005))

        assertEquals(4, set.size)
        val it = set.iterator()
        assertSame(LargeEnum.V010, it.next())
        assertSame(LargeEnum.V064, it.next())
        assertSame(LargeEnum.V079, it.next())
        assertSame(LargeEnum.V005, it.next())
    }

    @Test
    fun `list iterator indices work with large enum`() {
        val set = newSet()
        set.add(LargeEnum.V063)
        set.add(LargeEnum.V064)
        set.add(LargeEnum.V065)

        val it = set.iterator() as ObjectListIterator
        assertEquals(0, it.nextIndex())
        assertEquals(-1, it.previousIndex())

        it.next()
        assertEquals(1, it.nextIndex())
        assertEquals(0, it.previousIndex())

        it.next()
        assertEquals(2, it.nextIndex())
        assertEquals(1, it.previousIndex())

        it.next()
        assertEquals(3, it.nextIndex())
        assertEquals(2, it.previousIndex())
    }

    @Test
    fun `removeIf with large enum`() {
        val set = newSet()
        set.add(LargeEnum.V063)
        set.add(LargeEnum.V064)
        set.add(LargeEnum.V065)
        set.add(LargeEnum.V079)

        val modified = set.removeIf { it == LargeEnum.V064 || it == LargeEnum.V065 }

        assertTrue(modified)
        assertEquals(2, set.size)
        assertTrue(set.contains(LargeEnum.V063))
        assertFalse(set.contains(LargeEnum.V064))
        assertFalse(set.contains(LargeEnum.V065))
        assertTrue(set.contains(LargeEnum.V079))
    }

    @Test
    fun `retainAll with large enum`() {
        val set = newSet()
        set.add(LargeEnum.V063)
        set.add(LargeEnum.V064)
        set.add(LargeEnum.V065)
        set.add(LargeEnum.V079)

        val modified = set.retainAll(listOf(LargeEnum.V063, LargeEnum.V079))

        assertTrue(modified)
        assertEquals(2, set.size)
        assertTrue(set.contains(LargeEnum.V063))
        assertFalse(set.contains(LargeEnum.V064))
        assertFalse(set.contains(LargeEnum.V065))
        assertTrue(set.contains(LargeEnum.V079))
    }

    @Test
    fun `indexOf after addAndMoveToFirst with large enum`() {
        val set = newSet()
        set.add(LargeEnum.V010)
        set.add(LargeEnum.V065)
        set.add(LargeEnum.V079)

        set.addAndMoveToFirst(LargeEnum.V079)

        assertEquals(0, set.indexOf(LargeEnum.V079))
        assertEquals(1, set.indexOf(LargeEnum.V010))
        assertEquals(2, set.indexOf(LargeEnum.V065))
    }

    @Test
    fun `iterator from element nextIndex and previousIndex with large enum`() {
        val set = newSet()
        set.add(LargeEnum.V000)
        set.add(LargeEnum.V064)
        set.add(LargeEnum.V079)

        val it = set.iterator(LargeEnum.V064) as ObjectListIterator
        assertEquals(2, it.nextIndex())
        assertEquals(1, it.previousIndex())
    }
}
