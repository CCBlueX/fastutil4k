package net.ccbluex.fastutil

import java.io.Serializable
import java.util.function.BiConsumer
import java.util.function.BooleanSupplier
import java.util.function.Consumer
import java.util.function.DoubleConsumer
import java.util.function.DoublePredicate
import java.util.function.IntConsumer
import java.util.function.IntPredicate
import java.util.function.LongConsumer
import java.util.function.LongPredicate
import java.util.function.ObjDoubleConsumer
import java.util.function.ObjIntConsumer
import java.util.function.ObjLongConsumer
import java.util.function.Predicate

internal data object Nop :
    Runnable,
    Consumer<Any?>,
    DoubleConsumer,
    IntConsumer,
    LongConsumer,
    ObjDoubleConsumer<Any?>,
    ObjIntConsumer<Any?>,
    ObjLongConsumer<Any?>,
    BiConsumer<Any?, Any?>,
    Serializable {
    private const val serialVersionUID: Long = 1L
    private fun readResolve(): Any = Nop

    override fun run() = Unit

    override fun accept(t: Any?) = Unit
    override fun accept(value: Double) = Unit
    override fun accept(value: Int) = Unit
    override fun accept(value: Long) = Unit

    override fun andThen(after: Consumer<in Any?>): Consumer<Any?> = after
    override fun andThen(after: DoubleConsumer): DoubleConsumer = after
    override fun andThen(after: IntConsumer): IntConsumer = after
    override fun andThen(after: LongConsumer): LongConsumer = after

    override fun accept(t: Any?, value: Double) = Unit
    override fun accept(t: Any?, value: Int) = Unit
    override fun accept(t: Any?, value: Long) = Unit

    override fun accept(t: Any?, u: Any?) = Unit
    override fun andThen(after: BiConsumer<in Any?, in Any?>): BiConsumer<Any?, Any?> = after
}

internal data object AlwaysTrue : BooleanSupplier, Predicate<Any?>, DoublePredicate, IntPredicate, LongPredicate, Serializable {
    private const val serialVersionUID: Long = 1L
    private fun readResolve(): Any = AlwaysTrue

    override fun getAsBoolean(): Boolean = true

    override fun test(t: Any?): Boolean = true
    override fun test(value: Int): Boolean = true
    override fun test(value: Long): Boolean = true
    override fun test(value: Double): Boolean = true

    override fun and(other: Predicate<in Any?>): Predicate<Any?> = other
    override fun and(other: DoublePredicate): DoublePredicate = other
    override fun and(other: IntPredicate): IntPredicate = other
    override fun and(other: LongPredicate): LongPredicate = other

    override fun negate() = AlwaysFalse

    override fun or(other: Predicate<in Any?>): Predicate<Any?> = this
    override fun or(other: DoublePredicate): DoublePredicate = this
    override fun or(other: IntPredicate): IntPredicate = this
    override fun or(other: LongPredicate): LongPredicate = this
}

internal data object AlwaysFalse : BooleanSupplier, Predicate<Any?>, DoublePredicate, IntPredicate, LongPredicate, Serializable {
    private const val serialVersionUID: Long = 1L
    private fun readResolve(): Any = AlwaysFalse

    override fun getAsBoolean(): Boolean = false

    override fun test(t: Any?): Boolean = false
    override fun test(value: Int): Boolean = false
    override fun test(value: Long): Boolean = false
    override fun test(value: Double): Boolean = false

    override fun and(other: Predicate<in Any?>): Predicate<Any?> = this
    override fun and(other: DoublePredicate): DoublePredicate = this
    override fun and(other: IntPredicate): IntPredicate = this
    override fun and(other: LongPredicate): LongPredicate = this

    override fun negate() = AlwaysTrue

    override fun or(other: Predicate<in Any?>): Predicate<Any?> = other
    override fun or(other: DoublePredicate): DoublePredicate = other
    override fun or(other: IntPredicate): IntPredicate = other
    override fun or(other: LongPredicate): LongPredicate = other
}
