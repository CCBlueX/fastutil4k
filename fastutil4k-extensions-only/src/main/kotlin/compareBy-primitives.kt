@file:Suppress("unused", "NOTHING_TO_INLINE")

package net.ccbluex.fastutil

import java.util.function.ToDoubleFunction
import java.util.function.ToIntFunction
import java.util.function.ToLongFunction

inline fun <T> compareByInt(keyExtractor: ToIntFunction<T>): Comparator<T> = Comparator.comparingInt(keyExtractor)

inline fun <T> compareByDouble(keyExtractor: ToDoubleFunction<T>): Comparator<T> = Comparator.comparingDouble(keyExtractor)

inline fun <T> compareByLong(keyExtractor: ToLongFunction<T>): Comparator<T> = Comparator.comparingLong(keyExtractor)
