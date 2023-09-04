plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
    id("org.jetbrains.intellij") version "1.15.0"
    id("com.squareup.sqldelight") version "1.5.5"
}

group = "org.metailurini"
version = "2.0"

repositories {
    mavenCentral()
}
dependencies {
    implementation("com.google.code.gson:gson:2.10")
    implementation("org.junit.jupiter:junit-jupiter:5.8.1")
    implementation("com.squareup.sqldelight:sqlite-driver:1.5.5")
    implementation("com.squareup.sqldelight:coroutines-extensions:1.5.0")
    implementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
    implementation("org.xerial:sqlite-jdbc:3.41.2.2")
}

intellij {
    version.set("2023.2.1")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf())
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set("232.*")
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