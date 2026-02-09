
import java.util.*

plugins {
    base
    id("dev.kikugie.stonecutter")
}

stonecutter.active("fabric-1.21.6-11")

tasks.register<Copy>("collectJars") {
    group = "build"
    description = "Collects built jars from all versions into build/."

    dependsOn(subprojects.map { it.tasks.named("build") })

    doFirst {
        delete(layout.buildDirectory)
    }

    into(layout.buildDirectory)
    includeEmptyDirs = false
    from(fileTree("versions")) {
        include("**/build/libs/*.jar")
        exclude("**/*-sources.jar")
        exclude("**/*-dev.jar")
    }

    eachFile {
        val projectName = this.file.parentFile.parentFile.parentFile.name
        val parts = projectName.split("-", limit = 2)
        if (parts.size < 2) throw GradleException("Invalid project name (expected loader-version): $projectName")
        val loader = parts[0]
        val mcVersion = parts[1]

        val propsFile = File(rootProject.projectDir, "versions/$projectName/gradle.properties")
        val props = Properties()
        propsFile.inputStream().use { props.load(it) }
        val modVersion = props.getProperty("mod_version") ?: "0.0.0"

        this.name = "${rootProject.name}-$loader-$mcVersion-$modVersion.jar"
        this.relativePath = RelativePath(true, this.name)
    }
}

tasks.named("build") {
    dependsOn(subprojects.map { it.tasks.named("build") })
    finalizedBy("collectJars")
}
