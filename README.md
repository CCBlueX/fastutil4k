# fastutil4k

Kotlin-first utilities built on top of [fastutil](https://fastutil.di.unimi.it/).

This repository is a multi-module Gradle project:

- `fastutil4k-extensions-only`: generated and hand-written inline Kotlin extension APIs for fastutil and JDK collections.
- `fastutil4k-more-collections`: additional collection implementations built with fastutil data structures.
- `benchmark`: JMH benchmarks for local performance measurement (not published).

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
- `EnumLinkedSet<E>`: insertion-ordered set for enum types backed by a bit-vector and packed doubly-linked list
- `WeightedSortedList<E>`: bounds-checked list sorted by element weight
- weighted terminal operations for `Iterable` / `Sequence`:
  `weightedFilterSortedBy*`, `weightedMinByOrNull*`, `weightedMaxByOrNull*`
- stepped float/double ranges returning fastutil lists (`ClosedRange<Double>.step`, `ClosedRange<Float>.step`)

### `benchmark`

Independent JMH module for performance testing:

- core benchmarks for `Pool`, `LfuCache`, `WeightedSortedList`, `Ranges`
- transform benchmark:
  `mapToArray { }.asList()` vs `collection.map` vs `sequence.map.toList` vs `stream.map.toList`
- not part of publish artifacts

## Requirements

- JDK 8+ (toolchain target is Java 8)
- Gradle Wrapper (included)

## Build

```bash
./gradlew clean build
```

`benchmark` is intentionally skipped in normal `build/check/test` lifecycle.

## Run Benchmarks

```bash
# Compile JMH sources
./gradlew :benchmark:compileJmhKotlin

# Smoke run (single benchmark class, short run)
./gradlew :benchmark:jmh --no-configuration-cache \
  -Pjmh.includes=MapTransformBenchmark \
  -Pjmh.warmupIterations=1 \
  -Pjmh.iterations=1 \
  -Pjmh.fork=1

# Full benchmark suite
./gradlew :benchmark:jmh --no-configuration-cache
```

JMH JSON output path:

- `benchmark/build/reports/jmh/results.json`

## Regenerate extension sources

Generated sources are produced by module tasks.

```bash
./gradlew :fastutil4k-extensions-only:generate-all
./gradlew :fastutil4k-more-collections:weighted-terminal
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
