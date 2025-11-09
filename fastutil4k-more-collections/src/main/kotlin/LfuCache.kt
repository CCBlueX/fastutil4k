@file:Suppress("NOTHING_TO_INLINE")

package net.ccbluex.fastutil

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectCollection
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import it.unimi.dsi.fastutil.objects.ReferenceArrayList
import java.util.function.BiConsumer
import java.util.function.IntFunction

/**
 * A simple least frequency used cache. Non-thread-safe.
 */
class LfuCache<K : Any, V : Any> @JvmOverloads constructor(
    @get:JvmName("capacity")
    val capacity: Int,
    val onDiscard: BiConsumer<K, V> = BiConsumer { _, _ -> },
) : Map<K, V> {
    init {
        require(capacity > 0) { "capacity should be positive" }
    }

    /**
     * [Map] backend.
     */
    private val cache = Object2ObjectOpenHashMap<K, V>()

    /**
     * Access count of each key.
     *
     * The key set of [counts] is same as [cache]:
     * `counts.keys == cache.keys`
     */
    private val counts = Object2IntOpenHashMap<K>()

    /**
     * The access count and keys with this count.
     *
     * This matches:
     * `countTable.values.flatten() == counts.keys`
     * `countTable.keys == counts.values`
     *
     * All values are not empty.
     */
    private val countTable = Int2ObjectOpenHashMap<ObjectLinkedOpenHashSet<K>>()

    /**
     * The minimum access count of all keys.
     *
     * When the cache is empty, it is 0.
     */
    private var minCount = 0

    private val setPool = ReferenceArrayList<ObjectLinkedOpenHashSet<K>>()

    /**
     * Creates a [ObjectLinkedOpenHashSet] (or get from the pool) for [countTable].
     */
    private val newSet = IntFunction<ObjectLinkedOpenHashSet<K>> { _ ->
        if (setPool.isEmpty) ObjectLinkedOpenHashSet() else setPool.pop()
    }

    /**
     * Increases the access count of [key].
     */
    private fun incr(key: K) {
        val oldCount = counts.addTo(key, 1)
        val setOfOldCount = countTable.get(oldCount) ?: error("oldCount $oldCount is not in countTable")
        if (setOfOldCount.size == 1) {
            // Remove this set
            if (minCount == oldCount) minCount++

            countTable.remove(oldCount)
            setOfOldCount.clear()
            setPool.add(setOfOldCount)
        } else {
            setOfOldCount.remove(key)
        }
        countTable.computeIfAbsent(oldCount + 1, newSet).addAndMoveToLast(key)
    }

    /**
     * Discards one of the least-used keys.
     */
    private fun discard() {
        if (countTable.isEmpty()) return

        val set = countTable[minCount] ?: error("minCount $minCount is not in countTable")
        val removedKey = set.removeFirst()
        val removedValue = cache.remove(removedKey) ?: error("removedKey $removedKey is not in cache")
        counts.removeInt(removedKey)
        if (set.isEmpty()) {
            setPool.add(set)
            countTable.remove(minCount)
        }

        if (countTable.isEmpty()) minCount = 0

        onDiscard.accept(removedKey, removedValue)
    }

    // Map operations

    override val size: Int get() = cache.size

    override fun containsValue(value: V): Boolean = cache.containsValue(value)

    /**
     * Gets the key and corresponding value (if exists), and increases its access count.
     */
    override operator fun get(key: K): V? = cache[key]?.also { incr(key) }

    override fun isEmpty(): Boolean = minCount == 0

    /**
     * Sets the key and corresponding value, and discards one of the least-used keys if full.
     *
     * @return the previous value of [key], or null if it is absent.
     */
    fun put(key: K, value: V): V? {
        // Present
        cache.put(key, value)?.let {
            incr(key)
            return it
        }

        // Absent
        if (cache.size > capacity) {
            discard()
        }

        counts.put(key, 1)
        countTable.computeIfAbsent(1, newSet).addAndMoveToLast(key)
        minCount = 1

        return null
    }

    inline operator fun set(key: K, value: V) {
        put(key, value)
    }

    override fun containsKey(key: K): Boolean = cache.containsKey(key)

    inline fun getOrPut(key: K, mappingFunction: (K) -> V): V = get(key) ?: mappingFunction(key).also { put(key, it) }

    /**
     * Clears the cache.
     */
    fun clear() {
        cache.clear()
        counts.clear()
        countTable.clear()
        minCount = 0
    }

    private var _entries: ObjectSet<out Map.Entry<K, V>>? = null

    override val entries: ObjectSet<out Map.Entry<K, V>>
        get() = _entries ?: cache.entries.unmodifiable().also { _entries = it }

    private var _keys: ObjectSet<K>? = null

    override val keys: ObjectSet<K>
        get() = _keys ?: cache.keys.unmodifiable().also { _keys = it }

    private var _values: ObjectCollection<V>? = null

    override val values: ObjectCollection<V>
        get() = _values ?: cache.values.unmodifiable().also { _values = it }
}
