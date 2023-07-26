plugins {
    `java-library`
    `maven-publish`
}

group = "dev.goldenstack.enchantment"
version = "1.1.1"

java {
    withJavadocJar()
    withSourcesJar()

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()

    maven("https://jitpack.io")
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")

    compileOnly("com.github.Minestom.Minestom:Minestom:c5047b8037")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

configure<JavaPluginExtension> {
    withSourcesJar()
}

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}