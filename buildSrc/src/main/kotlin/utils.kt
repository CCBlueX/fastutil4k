import org.gradle.api.Project
import org.intellij.lang.annotations.Language

internal inline fun forEachTypes(block: (type: FastutilType) -> Unit) {
    FastutilType.entries.forEach(block)
}

internal inline fun forEachPrimitiveTypes(block: (type: FastutilType) -> Unit) {
    FastutilType.entries.forEach {
        if (it.isGeneric) return@forEach
        block(it)
    }
}

internal inline fun forEachMapTypes(block: (left: FastutilType, right: FastutilType) -> Unit) {
    forEachTypes { left ->
        if (left == FastutilType.BOOLEAN) return@forEachTypes // BooleanMap does not exist

        forEachTypes { right ->
            block(left, right)
        }
    }
}

val Project.fastutilGeneratorOutput get() = layout.buildDirectory.dir("generated/fastutil-kt")

const val INDENT_SIZE = 4

fun interface StringAppendable {
    fun append(string: String)
}

inline fun StringAppendable.appendLine() = append("\n")

fun StringAppendable.space(n: Int = 1) = append(" ".repeat(n))

inline fun StringAppendable.appendLine(line: String) {
    append(line)
    appendLine()
}

inline fun StringAppendable.appendMultiline(@Language("kotlin") line: String) {
    append(line.trimIndent())
    appendLine()
}

inline fun StringAppendable.withIndent(n: Int = 1, action: StringAppendable.() -> Unit) = StringAppendable {
    space(n * INDENT_SIZE)
    append(it)
}.apply(action)
