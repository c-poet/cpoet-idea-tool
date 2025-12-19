repositories {
    maven {
        url = uri("https://maven.aliyun.com/repository/public")
    }
    maven {
        url = uri("https://maven.aliyun.com/repository/central")
    }
    mavenLocal()
    mavenCentral()
}

plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.2"
}

group = "cn.cpoet.tool"
version = "0.3.0"

dependencies {
    implementation(project(":core")) {
        exclude("*", "*")
    }
    implementation(project(":impl223")) {
        exclude("*", "*")
    }
    implementation(project(":impl243")) {
        exclude("*", "*")
    }
    implementation(project(":impl251")) {
        exclude("*", "*")
    }
}

intellij {
//    version.set("2022.3")
//    version.set("2023.3")
//    version.set("2024.3")
    version.set("2025.1")

    type.set("IU")

    plugins.set(listOf(
            "com.intellij.database",
            "com.intellij.java",
            "com.intellij.spring"
    ))
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    patchPluginXml {
        sinceBuild.set("223")
        untilBuild.set("252.*")
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
