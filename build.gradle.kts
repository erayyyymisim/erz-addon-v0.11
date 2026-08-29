'll analyze the failing job to find a solution. Let me start by getting the job logs.


Job 99035060842 in erayyyymisim/erz-addon-v0.11
The build is failing due to a syntax error in your build.gradle.kts file at line 95. The error message indicates an "Unexpected symbol" at the closing brace }.

Let me examine the build file to identify the exact issue:


View file build.gradle.kts
Solution
The build failure is caused by syntax errors in build.gradle.kts at line 95. There are two main issues:

Missing closing brace for dependencies block (line 31): The dependencies block that starts at line 25 is not properly closed before the java block begins at line 32.

Duplicate code block (lines 73-77): The processResources task configuration is duplicated and malformed, with orphaned code outside any block.

Fixed build.gradle.kts:
Kotlin
plugins {
    alias(libs.plugins.fabric.loom)
}

val archivesBaseName = providers.gradleProperty("archives_base_name").get()
val mavenGroup = providers.gradleProperty("maven_group").get()

base {
    archivesName = archivesBaseName
    version = libs.versions.mod.version.get()
    group = mavenGroup
}

repositories {
    maven {
        name = "meteor-maven"
        url = uri("https://maven.meteordev.org/releases")
    }
    maven {
        name = "meteor-maven-snapshots"
        url = uri("https://maven.meteordev.org/snapshots")
    }
}

dependencies {
    // Fabric
    minecraft(libs.minecraft)
    implementation(libs.fabric.loader)

    // Meteor
    implementation(libs.meteor.client)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

fun toMinecraftCompat(version: String): String {
    val stable = Regex("""^(\d{2})\.([1-9]\d*)(?:\.(\d+))?$""")

    stable.matchEntire(version)?.let {
        val (year, drop, _) = it.destructured
        return "~$year.$drop"
    }

    val pre = Regex("""^(\d{2})\.([1-9]\d*)-pre[-.](\d+)$""")
    pre.matchEntire(version)?.let {
        return version.replace("-pre-", "-pre.")
    }

    val rc = Regex("""^(\d{2})\.([1-9]\d*)-rc[-.](\d+)$""")
    rc.matchEntire(version)?.let {
        return version.replace("-rc-", "-rc.")
    }

    return version
}

tasks {
    processResources {
        val propertyMap = mapOf(
            "version" to project.version,
            "minecraft_version" to toMinecraftCompat(libs.versions.minecraft.get()),
            "jdk_version" to "21",
        )
        inputs.properties(propertyMap)
        filteringCharset("UTF-8")
        filesMatching("fabric.mod.json") {
            expand(propertyMap)
        }
    }

    jar {
        inputs.property("archivesName", archivesBaseName)

        from("LICENSE") {
            rename { "${it}_$archivesBaseName" }
        }
    }

    withType<JavaCompile>().configureEach {
        options.compilerArgs.addAll(
            listOf(
                "-Xlint:deprecation",
                "-Xlint:unchecked"
            )
        )
    }
}
Key changes:

Added missing closing brace } after line 31 to properly close the dependencies block
Removed the duplicate/malformed processResources configuration (lines 73-77)
Kept all logic intact within the properly structured tasks block
