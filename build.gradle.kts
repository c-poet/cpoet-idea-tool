import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

group = "cn.cpoet.ideas"
version = "0.1.3"

plugins {
    id("com.gradleup.shadow") version "8.3.5" apply false
    id("org.jetbrains.intellij") version "1.16.1" apply false
}


allprojects {
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
}

subprojects {

    apply(plugin = "java")
    apply(plugin = "org.jetbrains.intellij")
    apply(plugin = "com.gradleup.shadow")

    group = parent!!.group
    version = parent!!.version

    tasks {
        // Set the JVM compatibility versions
        withType<JavaCompile> {
            sourceCompatibility = "11"
            targetCompatibility = "11"
            options.encoding = "UTF-8"
        }
    }
}
