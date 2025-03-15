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
    id("org.jetbrains.intellij") version "1.16.1"
}

group = "cn.cpoet.tool"
version = "0.1.4"


// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {

    version.set("2022.1")
//    version.set("2023.3.4")
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
        targetCompatibility = "11"
        sourceCompatibility = "11"
        options.encoding = "UTF-8"
    }

    patchPluginXml {
        sinceBuild.set("221")
        untilBuild.set("241.*")
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

