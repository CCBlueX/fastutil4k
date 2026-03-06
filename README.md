# fastutil4k

Kotlin-first utilities built on top of [fastutil](https://fastutil.di.unimi.it/).

This repository is a multi-module Gradle project:

- `fastutil4k-extensions-only`: generated and hand-written inline Kotlin extension APIs for fastutil and JDK collections.
- `fastutil4k-more-collections`: additional collection implementations built with fastutil data structures.

## Modules

### `fastutil4k-extensions-only`

Provides a large set of extension helpers in package `net.ccbluex.fastutil`, including:

- collection wrappers: `synchronized()` and `unmodifiable()`
- pair helpers: `pair`, `mutPair`, `refPair`, destructuring support
- primitive/object list, set, and map factories
- typed iteration helpers: `forEachInt`, `forEachDoubleIndexed`, `onEachLong`, etc.
- typed array mapping helpers: `mapToIntArray`, `mapToArray`, ...
- function-like operators for fastutil functional interfaces:
  `UnaryOperator.invoke`, `BinaryOperator.invoke`, `Predicate.invoke`, `Consumer.invoke`

Most APIs are generated under:

- `fastutil4k-extensions-only/build/generated/fastutil-kt`

The module is designed to be used as `compileOnly` in downstream projects.

### `fastutil4k-more-collections`

Provides higher-level collection utilities and data structures, including:

- `Pool<E>`: reusable object pool with optional finalizer and synchronized wrapper
- `LfuCache<K, V>`: non-thread-safe LFU cache with LRU tie-breaking
- `WeightedSortedList<E>`: bounds-checked list sorted by element weight
- stepped float/double ranges returning fastutil lists (`ClosedRange<Double>.step`, `ClosedRange<Float>.step`)

## Requirements

- JDK 8+ (toolchain target is Java 8)
- Gradle Wrapper (included)

## Build

```bash
./gradlew clean build
```

## Regenerate extension sources

Generated extension sources are produced by module tasks in `fastutil4k-extensions-only`.

```bash
./gradlew :fastutil4k-extensions-only:generate-all
```

## Dependency coordinates

Group:

- `net.ccbluex`

Artifacts:

- `fastutil4k-extensions-only`
- `fastutil4k-more-collections`

Version is defined in the root build script (`build.gradle.kts`).

## License

MIT. See [LICENSE](LICENSE).
