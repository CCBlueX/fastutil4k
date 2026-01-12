import org.jetbrains.dokka.gradle.tasks.DokkaBaseTask

plugins {
    id("fastutil-ext-generator")
}

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly(libs.fastutil)
}

kotlin {
    sourceSets {
        main {
            kotlin.srcDir(fastutilGeneratorOutput)
        }
    }
}

dokka {
    dokkaSourceSets.configureEach {
        sourceRoots.from(fastutilGeneratorOutput)
    }
}

tasks.withType<DokkaBaseTask>().configureEach {
    dependsOn("generate-all")
}
