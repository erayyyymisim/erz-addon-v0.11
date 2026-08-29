plugins {
    id("fabric-loom") version "1.9-SNAPSHOT"
    id("maven-publish")
}

version = "1.0.0"
group = "com.example.addon"

repositories {
    mavenCentral()
    maven {
        name = "meteor-maven"
        url = uri("https://maven.meteordev.org/releases")
    }
    maven {
        name = "meteor-maven-snapshots"
        url = uri("https://maven.meteordev.org/snapshots")
    }
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:1.21.4")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:0.16.9")

    // Meteor Client bağımlılığı (Doğru depo ve paket adı)
    modImplementation("meteordevelopment:meteor-client:0.6.0-SNAPSHOT")
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

java {
    withSourcesJar()

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
