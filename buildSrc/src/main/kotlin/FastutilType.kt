enum class FastutilType(
	val typeName: String,
	val lowercaseName: String,
	val isGeneric: Boolean,
	val packageName: String,
) {
	BOOLEAN("Boolean", "boolean", false, "it.unimi.dsi.fastutil.booleans"),
	BYTE("Byte", "byte", false, "it.unimi.dsi.fastutil.bytes"),
	CHAR("Char", "char", false, "it.unimi.dsi.fastutil.chars"),
	DOUBLE("Double", "double", false, "it.unimi.dsi.fastutil.doubles"),
	FLOAT("Float", "float", false, "it.unimi.dsi.fastutil.floats"),
	INT("Int", "int", false, "it.unimi.dsi.fastutil.ints"),
	LONG("Long", "long", false, "it.unimi.dsi.fastutil.longs"),
	OBJECT("Object", "object", true, "it.unimi.dsi.fastutil.objects"),
	SHORT("Short", "short", false, "it.unimi.dsi.fastutil.shorts"),
	REFERENCE("Reference", "reference", true, "it.unimi.dsi.fastutil.objects"),
	;

	val kotlinType: String?
		get() = when (this) {
			REFERENCE -> null
			OBJECT -> "Any"
			else -> typeName
		}

	override fun toString(): String = typeName
}
