dependencies {
    compileOnly(libs.fastutil)
    compileOnly(project(":fastutil4k-extensions-only"))
    compileOnly(libs.annotations)

    testCompileOnly(project(":fastutil4k-extensions-only"))
    testImplementation(kotlin("test"))
    testImplementation(libs.fastutil)
}

tasks.test {
    useJUnitPlatform()
}

val weightedGeneratorOutput = layout.buildDirectory.dir("generated/fastutil-more")

val generateWeightedTerminalTask = tasks.register<GenerateSrcTask>("weighted-terminal") {
    group = "fastutil-ext-generator"

    file.set(weightedGeneratorOutput.map { it.file("weighted-terminal.kt") })
    packageName.set("net.ccbluex.fastutil")
    imports.add("it.unimi.dsi.fastutil.doubles.DoubleArrayList")
    imports.add("it.unimi.dsi.fastutil.objects.ObjectArrayList")

    content {
        appendLine("@PublishedApi")
        appendLine("internal inline fun <E> weightedFilterSortedByInternal(")
        appendLine("    source: Iterator<E>,")
        appendLine("    weightSelector: (E) -> Double,")
        appendLine("    include: (Double) -> Boolean,")
        appendLine("): List<E> {")
        appendLine("    val elements = ObjectArrayList<E>()")
        appendLine("    val weights = DoubleArrayList()")
        appendLine("    while (source.hasNext()) {")
        appendLine("        val element = source.next()")
        appendLine("        val weight = weightSelector(element)")
        appendLine("        if (include(weight)) {")
        appendLine("            elements.add(element)")
        appendLine("            weights.add(weight)")
        appendLine("        }")
        appendLine("    }")
        appendLine()
        appendLine("    if (elements.size > 1) {")
        appendLine("        val elementArr = elements.elements()")
        appendLine("        val weightArr = weights.elements()")
        appendLine("        elementArr.sortByWeightsInPlace(0, elements.size, weightArr)")
        appendLine("    }")
        appendLine()
        appendLine("    return elements")
        appendLine("}")
        appendLine()

        appendLine("@PublishedApi")
        appendLine("internal inline fun <E> weightedMinByOrNullInternal(")
        appendLine("    source: Iterator<E>,")
        appendLine("    weightSelector: (E) -> Double,")
        appendLine("    include: (Double) -> Boolean,")
        appendLine("): E? {")
        appendLine("    var found = false")
        appendLine("    var minWeight = Double.POSITIVE_INFINITY")
        appendLine("    var minElement: E? = null")
        appendLine()
        appendLine("    while (source.hasNext()) {")
        appendLine("        val element = source.next()")
        appendLine("        val weight = weightSelector(element)")
        appendLine("        if (!include(weight)) continue")
        appendLine()
        appendLine("        if (!found || weight < minWeight) {")
        appendLine("            found = true")
        appendLine("            minWeight = weight")
        appendLine("            minElement = element")
        appendLine("        }")
        appendLine("    }")
        appendLine()
        appendLine("    return minElement")
        appendLine("}")
        appendLine()

        appendLine("@PublishedApi")
        appendLine("internal inline fun <E> weightedMaxByOrNullInternal(")
        appendLine("    source: Iterator<E>,")
        appendLine("    weightSelector: (E) -> Double,")
        appendLine("    include: (Double) -> Boolean,")
        appendLine("): E? {")
        appendLine("    var found = false")
        appendLine("    var maxWeight = Double.NEGATIVE_INFINITY")
        appendLine("    var maxElement: E? = null")
        appendLine()
        appendLine("    while (source.hasNext()) {")
        appendLine("        val element = source.next()")
        appendLine("        val weight = weightSelector(element)")
        appendLine("        if (!include(weight)) continue")
        appendLine()
        appendLine("        if (!found || weight > maxWeight) {")
        appendLine("            found = true")
        appendLine("            maxWeight = weight")
        appendLine("            maxElement = element")
        appendLine("        }")
        appendLine("    }")
        appendLine()
        appendLine("    return maxElement")
        appendLine("}")
        appendLine()

        for (receiver in arrayOf("Iterable<E>", "Sequence<E>")) {
            appendLine("inline fun <E> $receiver.weightedFilterSortedBy(weightSelector: (E) -> Double): List<E> =")
            appendLine("    weightedFilterSortedByInternal(iterator(), weightSelector) { true }")
            appendLine()

            appendLine("inline fun <E> $receiver.weightedFilterSortedByAtMost(maxWeight: Double, weightSelector: (E) -> Double): List<E> =")
            appendLine("    weightedFilterSortedByInternal(iterator(), weightSelector) { w -> w <= maxWeight }")
            appendLine()

            appendLine("inline fun <E> $receiver.weightedFilterSortedByAtLeast(minWeight: Double, weightSelector: (E) -> Double): List<E> =")
            appendLine("    weightedFilterSortedByInternal(iterator(), weightSelector) { w -> w >= minWeight }")
            appendLine()

            appendLine("inline fun <E> $receiver.weightedFilterSortedByIn(minWeight: Double, maxWeight: Double, weightSelector: (E) -> Double): List<E> {")
            appendLine("""    require(minWeight <= maxWeight) { "minWeight (${ '$' }minWeight) must be <= maxWeight (${ '$' }maxWeight)" }""")
            appendLine("    return weightedFilterSortedByInternal(iterator(), weightSelector) { w -> w >= minWeight && w <= maxWeight }")
            appendLine("}")
            appendLine()

            appendLine("inline fun <E> $receiver.weightedMinByOrNull(weightSelector: (E) -> Double): E? =")
            appendLine("    weightedMinByOrNullInternal(iterator(), weightSelector) { true }")
            appendLine()

            appendLine("inline fun <E> $receiver.weightedMinByOrNullAtMost(maxWeight: Double, weightSelector: (E) -> Double): E? =")
            appendLine("    weightedMinByOrNullInternal(iterator(), weightSelector) { w -> w <= maxWeight }")
            appendLine()

            appendLine("inline fun <E> $receiver.weightedMinByOrNullAtLeast(minWeight: Double, weightSelector: (E) -> Double): E? =")
            appendLine("    weightedMinByOrNullInternal(iterator(), weightSelector) { w -> w >= minWeight }")
            appendLine()

            appendLine("inline fun <E> $receiver.weightedMinByOrNullIn(minWeight: Double, maxWeight: Double, weightSelector: (E) -> Double): E? {")
            appendLine("""    require(minWeight <= maxWeight) { "minWeight (${ '$' }minWeight) must be <= maxWeight (${ '$' }maxWeight)" }""")
            appendLine("    return weightedMinByOrNullInternal(iterator(), weightSelector) { w -> w >= minWeight && w <= maxWeight }")
            appendLine("}")
            appendLine()

            appendLine("inline fun <E> $receiver.weightedMaxByOrNull(weightSelector: (E) -> Double): E? =")
            appendLine("    weightedMaxByOrNullInternal(iterator(), weightSelector) { true }")
            appendLine()

            appendLine("inline fun <E> $receiver.weightedMaxByOrNullAtMost(maxWeight: Double, weightSelector: (E) -> Double): E? =")
            appendLine("    weightedMaxByOrNullInternal(iterator(), weightSelector) { w -> w <= maxWeight }")
            appendLine()

            appendLine("inline fun <E> $receiver.weightedMaxByOrNullAtLeast(minWeight: Double, weightSelector: (E) -> Double): E? =")
            appendLine("    weightedMaxByOrNullInternal(iterator(), weightSelector) { w -> w >= minWeight }")
            appendLine()

            appendLine("inline fun <E> $receiver.weightedMaxByOrNullIn(minWeight: Double, maxWeight: Double, weightSelector: (E) -> Double): E? {")
            appendLine("""    require(minWeight <= maxWeight) { "minWeight (${ '$' }minWeight) must be <= maxWeight (${ '$' }maxWeight)" }""")
            appendLine("    return weightedMaxByOrNullInternal(iterator(), weightSelector) { w -> w >= minWeight && w <= maxWeight }")
            appendLine("}")
            appendLine()
        }
    }
}

kotlin {
    sourceSets {
        main {
            kotlin.srcDir(weightedGeneratorOutput)
        }
    }
}

tasks.named("compileKotlin") {
    dependsOn(generateWeightedTerminalTask)
}
