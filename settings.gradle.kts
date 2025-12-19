pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "cpoet-idea-tool"

include("core")
include("impl223")
include("impl243")
include("impl251")
include("cpoet-tool")
