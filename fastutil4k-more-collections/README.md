# fastutil4k-more-collections

Additional collection utilities built with fastutil data structures.

Package namespace:

- `net.ccbluex.fastutil`

## Features

### `Pool<E>`

Reusable object pool with:

- `borrow()`, `recycle()`, `recycleAll()`
- batch APIs: `borrowInto(...)`, `clearInto(...)`
- optional finalizer callback before recycle
- synchronized wrapper via `pool.synchronized()`
- scoped helper: `Pool.use { ... }`

### `LfuCache<K, V>`

Map-like LFU cache:

- tracks access counts
- evicts least-frequently-used entry when capacity pressure occurs
- uses LRU tie-breaking among same-frequency keys
- optional discard callback (`onDiscard`)

### `WeightedSortedList<E>`

List-like container sorted by weight:

- weight supplied by `ToDoubleFunction<in E>`
- non-decreasing order is enforced on updates
- optional inclusive/exclusive lower and upper bounds
- exposes weight lookup through `Object2DoubleFunction<E>`

### Stepped ranges

- `ClosedRange<Double>.step(step: Double): DoubleList`
- `ClosedRange<Float>.step(step: Float): FloatList`

Returns virtual read-only fastutil lists with O(1) random access and without materializing all values eagerly.

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
    implementation("net.ccbluex:fastutil4k-more-collections:$version")
    compileOnly("it.unimi.dsi:fastutil:8.5.15")
}
```

## Examples

```kotlin
import net.ccbluex.fastutil.*

val pool = Pool({ StringBuilder() }) { it.clear() }
val text = pool.use { sb ->
    sb.append("hello")
    sb.toString()
}

val cache = LfuCache<String, Int>(capacity = 2)
cache["a"] = 1
cache["b"] = 2
cache["a"] // increase frequency of "a"
cache["c"] = 3 // evicts "b"

val steps = (0.0..1.0).step(0.25)
println(steps) // [0.0, 0.25, 0.5, 0.75, 1.0]
```
