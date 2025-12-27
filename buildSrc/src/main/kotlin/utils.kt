import org.gradle.api.Project

internal inline fun forEachTypes(block: (type: FastutilType) -> Unit) {
    FastutilType.values().forEach(block)
}

internal inline fun forEachPrimitiveTypes(block: (type: FastutilType) -> Unit) {
    FastutilType.values().forEach {
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

private const val INDENT_SIZE = 4

fun interface LineAppendable {
    fun append(string: String)

    fun appendLine(line: String) {
        append(line)
        append("\n")
    }

    fun appendLine() = appendLine("")

    fun indent(n: Int = 1) = append(" ".repeat(n * INDENT_SIZE))
}

inline fun LineAppendable.withIndent(n: Int = 1, action: LineAppendable.() -> Unit) = LineAppendable {
    indent(n)
    append(it)
}.apply(action)
