# fastutil4k-extensions-only

Inline Kotlin extension APIs for fastutil and related JDK collection types.

This module is primarily generated source plus a few hand-written helpers.  
Generated files are located at:

- `build/generated/fastutil-kt`

Package namespace:

- `net.ccbluex.fastutil`

## What this module provides

- wrappers:
  - `synchronized()` / `synchronized(lock)`
  - `unmodifiable()`
- pair helpers:
  - infix constructors like `1 pair 2`, `1 mutPair 2`, `1 refPair someRef`
  - destructuring support (`component1`, `component2`) for fastutil pairs
- primitive/object collection factories:
  - list, set, and map factory helpers
- typed traversal:
  - `forEachInt`, `forEachByteIndexed`, `onEachDouble`, ...
- mapping helpers:
  - `mapToArray`, `mapToIntArray`, `mapToLongArray`, ...
- invocation operators for fastutil function interfaces:
  - `UnaryOperator.invoke`, `BinaryOperator.invoke`
  - `Predicate.invoke`, `Consumer.invoke`, `Function.invoke`

All exported APIs are inline, so `compileOnly` is a common usage mode.

## Usage

```kotlin
repositories {
    maven {
        name = "CCBlueX"
        url = uri("https://maven.ccbluex.net/releases")
    }
    mavenCentral()
}

dependencies {
    compileOnly("net.ccbluex:fastutil4k-extensions-only:$version")
}
```

## Example

```kotlin
import net.ccbluex.fastutil.*

val list = intListOf(1, 2, 3)
list.forEachIntIndexed { index, value ->
    println("$index -> $value")
}

val pair = 10 pair 20
val (left, right) = pair
println("$left / $right")
```

## Regenerate sources

```bash
./gradlew :fastutil4k-extensions-only:generate-all
```
