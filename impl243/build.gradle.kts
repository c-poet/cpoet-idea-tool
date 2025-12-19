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

dependencies {
    implementation(project(":core")) {
        exclude("*", "*")
    }
}

intellij {
    version.set("2024.3")

    type.set("IU")

    plugins.set(listOf(
            "com.intellij.database",
            "com.intellij.java",
            "com.intellij.spring"
    ))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

