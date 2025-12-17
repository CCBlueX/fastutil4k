package net.ccbluex.fastutil

import java.util.stream.Stream

inline fun <reified T> Array<*>.forEachIsInstance(action: (T) -> Unit) {
    for (element in this) {
        if (element is T) {
            action(element)
        }
    }
}

inline fun <reified T> Iterable<*>.forEachIsInstance(action: (T) -> Unit) {
    for (element in this) {
        if (element is T) {
            action(element)
        }
    }
}

inline fun <reified T> Sequence<*>.forEachIsInstance(action: (T) -> Unit) {
    for (element in this) {
        if (element is T) {
            action(element)
        }
    }
}

inline fun <reified T> Stream<*>.forEachIsInstance(action: (T) -> Unit) {
    for (element in this) {
        if (element is T) {
            action(element)
        }
    }
}

inline fun <reified T, C : Iterable<*>> C.onEachIsInstance(action: (T) -> Unit) = apply {
    forEachIsInstance(action)
}

inline fun <reified T, C : Sequence<*>> C.onEachIsInstance(action: (T) -> Unit) = apply {
    forEachIsInstance(action)
}

inline fun <reified T, C : Stream<*>> C.onEachIsInstance(action: (T) -> Unit) = apply {
    forEachIsInstance(action)
}
