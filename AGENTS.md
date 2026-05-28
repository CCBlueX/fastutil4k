# AGENTS.md

## Project Overview

`fastutil4k` is a Kotlin-first, multi-module Gradle project providing utilities built on top of [fastutil](https://fastutil.di.unimi.it/). It targets JVM 1.8 with Kotlin 2.0.0.

### Modules

| Module | Purpose | Published |
|---|---|---|
| `fastutil4k-extensions-only` | Generated + hand-written inline Kotlin extension APIs for fastutil and JDK collections | Yes (`net.ccbluex:fastutil4k-extensions-only`) |
| `fastutil4k-more-collections` | Higher-level data structures built with fastutil internals (Pool, LfuCache, EnumLinkedSet, WeightedSortedList) | Yes (`net.ccbluex:fastutil4k-more-collections`) |
| `benchmark` | JMH benchmarks for local performance measurement | No |
| `buildSrc` | Custom Gradle tasks for code generation | No |

## Build Commands

```bash
# Build everything (benchmark is excluded from normal build lifecycle)
./gradlew clean build

# Run tests
./gradlew test

# Check formatting
./gradlew spotlessCheck

# Auto-fix formatting
./gradlew spotlessApply

# Regenerate generated sources
./gradlew :fastutil4k-extensions-only:generate-all
./gradlew :fastutil4k-more-collections:weighted-terminal

# Run Dokka documentation
./gradlew dokkaHtml

# Run benchmarks
./gradlew :benchmark:compileJmhKotlin
./gradlew :benchmark:jmh --no-configuration-cache
```

## Tech Stack

- **Language**: Kotlin 2.0.0
- **Build**: Gradle (Kotlin DSL) with wrapper
- **Target**: JVM 1.8 (toolchain 8) (Build requirement: JDK 17+)
- **Testing**: `kotlin.test` (NOT JUnit directly)
- **Code generation**: Custom tasks in `buildSrc` using `GenerateSrcTask` and `StringAppendable`
- **Formatting**: Spotless + ktlint
- **Docs**: Dokka 2.1.0
- **Benchmarks**: JMH via `me.champeau.jmh` plugin
- **Dependencies**: managed in `gradle/libs.versions.toml` (version catalog)

## Code Conventions

### Formatting (enforced by Spotless)
- 4-space indentation (no tabs)
- Unix line endings (LF)
- No trailing whitespace
- Files end with a newline
- No wildcard imports (`ktlint_standard_no-wildcard-imports` enabled)
- Filename checks are disabled (`ktlint_standard_filename` disabled)

### Style
- **No comments in code** — code should be self-documenting
- All public API must have KDoc on classes, interfaces, and public functions
- Prefer `inline` functions for extension utilities
- Use `@file:Suppress("unused", "NOTHING_TO_INLINE")` at the top of generated source files
- Use `@file:JvmName(...)` for generated files to control the JVM class name

### Package & Naming
- Package: `net.ccbluex.fastutil`
- Test files use the `.test.kt` suffix (e.g., `Pool.test.kt`)
- Generated sources go under `build/generated/fastutil-kt/`

### Testing
- Use `kotlin.test` assertions: `assertEquals`, `assertTrue`, `assertFalse`, `assertFailsWith`, etc.
- Test class naming: `<ClassName>Test`
- Use backtick-enclosed descriptive test names: `` fun `should borrow and recycle objects correctly`() ``
- Tests live in `src/test/kotlin/` under the same package as the source

## Code Generation

Generated sources are produced by custom Gradle tasks extending `GenerateSrcTask` defined in `buildSrc`.

### Key types in buildSrc
- `FastutilType` — enum of fastutil primitive/object/reference types used as template parameters
- `GenerateSrcTask` — abstract task base class that writes a Kotlin file with package, imports, and generated content
- `StringAppendable` — helper for building generated source strings
- `constants.kt` — shared constants
- `utils.kt` — utility extensions

### Adding a new generated source task
1. Register a task of type `GenerateSrcTask` in the module's `build.gradle.kts`
2. Configure `packageName` and `imports`
3. Define the `content { ... }` block using `StringAppendable`
4. Reference `FastutilType.entries` to iterate over all primitive types

## Dependencies

Dependencies are declared in `gradle/libs.versions.toml`. When adding a new dependency:
1. Add the version to `[versions]` if not already present
2. Add the library/plugin to `[libraries]` or `[plugins]`
3. Reference via `libs.xxx` in build scripts

## Publishing

Only `fastutil4k-extensions-only` and `fastutil4k-more-collections` are published. Publishing uses `maven-publish` plugin configured in the root `build.gradle.kts`.

- Group: `net.ccbluex`
- Version: defined in root `build.gradle.kts` (currently `0.2.8`)
- Repository: `https://maven.ccbluex.net/releases`
- Credentials: `MAVEN_TOKEN_NAME` / `MAVEN_TOKEN_SECRET` environment variables

Publishing requires tests to pass (`PublishToMavenRepository` depends on `test`).

Artifacts include `sources` and `javadoc` classifiers, plus the `LICENSE` file in `META-INF/`.

## CI/CD

GitHub Actions workflows in `.github/workflows/`:
- `build.yml` — runs `./gradlew clean build` on JDK 17 on push/PR to `main`
- `docs.yml` — generates and deploys Dokka documentation
- `benchmark.yml` — runs JMH benchmarks
- `maven-publish.yml` — publishes to Maven repository

## Module-specific Notes

### `fastutil4k-extensions-only`
- Intended for `compileOnly` usage in downstream projects
- Mix of generated sources (under `build/generated/fastutil-kt/`) and hand-written files in `src/main/kotlin/`

### `fastutil4k-more-collections`
- Contains concrete data structures: `Pool`, `LfuCache`, `EnumLinkedSet`, `WeightedSortedList`, `Ranges`
- Also has weighted terminal operations for `Iterable` / `Sequence`
- Has a `weighted-terminal` code generation task

### `benchmark`
- Skipped from normal `build`/`check`/`test` lifecycle (tasks explicitly disabled)
- JMH configuration uses Gradle properties: `jmh.warmupIterations`, `jmh.iterations`, `jmh.fork`, `jmh.includes`
