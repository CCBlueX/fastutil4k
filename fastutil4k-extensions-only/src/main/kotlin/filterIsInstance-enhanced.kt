@file:Suppress("unused", "NOTHING_TO_INLINE")
@file:JvmName("filterIsInstance-enhanced")

package net.ccbluex.fastutil

import java.util.function.Predicate
import java.util.stream.Stream

/**
 * Equivalent to `this.filterIsInstance<R>.filter { predicate(it) }`
 */
inline fun <reified R> Array<*>.filterIsInstance(predicate: (R) -> Boolean): List<R> = filterIsInstanceTo(ArrayList(), predicate)

/**
 * Equivalent to `this.filterIsInstance<R>.filter { predicate(it) }`
 */
inline fun <reified R> Iterable<*>.filterIsInstance(predicate: (R) -> Boolean): List<R> = filterIsInstanceTo(ArrayList(), predicate)

/**
 * Equivalent to `this.filterIsInstance<R>.filter { predicate(it) }`
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified R> Sequence<*>.filterIsInstance(crossinline predicate: (R) -> Boolean): Sequence<R> = filter { it is R && predicate(it) } as Sequence<R>

/**
 * Equivalent to `this.filterIsInstance<R>.filter { predicate(it) }`
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified R> Stream<*>.filterIsInstance(crossinline predicate: (R) -> Boolean): Stream<R> = filter(Predicate { it is R && predicate(it) }) as Stream<R>
