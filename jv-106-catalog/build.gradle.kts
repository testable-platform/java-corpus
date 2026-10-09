plugins {
    java
    id("com.gradleup.shadow") version "9.6.1"
    jacoco
    checkstyle
    pmd
    id("com.github.spotbugs") version "6.5.11"
}

group = "com.pramora.testable"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    implementation("commons-collections:commons-collections:3.2.1")
    implementation("org.apache.commons:commons-text:1.9")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.10.1")
    implementation("log4j:log4j:1.2.17")
    implementation("org.yaml:snakeyaml:1.30")
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

// --release is what pins the *API* surface to Java 25, not just the bytecode level.
// On this family the host JDK is Java 25 itself, so source/target and release agree here;
// release is declared anyway so a build on a newer JDK cannot compile against newer APIs
// and still emit Java 25 class files. This family is verified with `javac --release
// 25`, so the build files declare the same thing.
tasks.withType<JavaCompile>().configureEach {
    options.release.set(25)
}

jacoco {
    toolVersion = "0.8.14"
}

tasks.test {
    useJUnit()
    finalizedBy(tasks.jacocoTestReport)
}

checkstyle {
    toolVersion = "14.1.0"
    configFile = file("Tool Triggering (Synthetic Data)/checkstyle/checkstyle.xml")
}

pmd {
    toolVersion = "7.26.0"
    ruleSetFiles = files("Tool Triggering (Synthetic Data)/pmd/ruleset.xml")
    ruleSets = emptyList()
}

spotbugs {
    toolVersion.set("4.10.3")
    excludeFilter.set(file("Tool Triggering (Synthetic Data)/spotbugs/exclude.xml"))
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    relocate("org.apache.commons", "com.pramora.testable.shaded.commons")
    relocate("com.fasterxml.jackson", "com.pramora.testable.shaded.jackson")
    relocate("org.yaml.snakeyaml", "com.pramora.testable.shaded.snakeyaml")
}
