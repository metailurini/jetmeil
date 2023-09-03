plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
    id("org.jetbrains.intellij") version "1.10.1"
    id("com.squareup.sqldelight") version "1.5.5"
}

group = "org.metailurini"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
dependencies {
    implementation("com.google.code.gson:gson:2.10")
    implementation("org.junit.jupiter:junit-jupiter:5.8.1")
    implementation("com.squareup.sqldelight:sqlite-driver:1.5.5")
    implementation("com.squareup.sqldelight:coroutines-extensions:1.5.0")
    implementation("org.xerial:sqlite-jdbc:3.41.2.2")
    implementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.1.4")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    patchPluginXml {
        sinceBuild.set("221")
        untilBuild.set("231.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

sqldelight {
    database("Database") {
        dialect = "sqlite:3.25"
        packageName = "org.metailurini.jetmeil"
    }
}