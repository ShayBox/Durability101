import org.gradle.plugins.ide.eclipse.model.EclipseModel

plugins {
    id("dev.kikugie.stonecutter") apply false
    id("fabric-loom") version "1.6-SNAPSHOT" apply false
    id("net.minecraftforge.gradle") version "[6.0,6.2)" apply false
    id("org.spongepowered.mixin") version "0.7.+" apply false
}

if (project == rootProject) {
    tasks.register<Copy>("collectJars") {
        group = "build"
        description = "Collects built jars from all versions into build/."

        dependsOn(subprojects.map { it.tasks.named("build") })

        into(rootProject.layout.buildDirectory)
        from(rootProject.fileTree("versions")) {
            include("**/build/libs/*.jar")
            exclude("**/*-sources.jar")
            exclude("**/*-dev.jar")
        }

        eachFile {
            val projectName = this.file.parentFile.parentFile.parentFile.name
            this.name = "$projectName-${this.name}"
        }
    }

    tasks.named("build") {
        dependsOn(subprojects.map { it.tasks.named("build") })
        finalizedBy("collectJars")
    }
} else {
    apply(plugin = "dev.kikugie.stonecutter")

    val currentProject = stonecutter.current.project

    val versionDir = rootProject.file("versions/$currentProject")
    if (!versionDir.exists()) throw GradleException("Missing version directory: $versionDir")

    val parts = currentProject.split("-", limit = 2)
    if (parts.size < 2) throw GradleException("Invalid project name (expected loader-version): $currentProject")
    val loader = parts[0]
    val isFabric = loader == "fabric"
    val isForge = loader == "forge"

    if (!isFabric && !isForge) throw GradleException("Unknown loader for $currentProject")

    val javaVersion = (findProperty("java_version") ?: "17").toString().toInt()
    val toolchainService = extensions.getByType(JavaToolchainService::class.java)
    val skipBuild = findProperty("skip_build").toString().toBoolean()
    val modId = (findProperty("mod_id")
        ?: findProperty("archives_base_name")
        ?: "durability101").toString()

    findProperty("maven_group")?.let { group = it }
        ?: findProperty("mod_group_id")?.let { group = it }

    findProperty("mod_version")?.let { version = it }

    base {
        archivesName.set((findProperty("archives_base_name") ?: modId).toString())
    }

    if (skipBuild) tasks.configureEach { enabled = false }
    if (isFabric) apply(plugin = "fabric-loom")
    if (isForge) {
        apply(plugin = "net.minecraftforge.gradle")
        apply(plugin = "eclipse")
        apply(plugin = "idea")

        val forgeMixin = findProperty("forge_mixin").toString().toBoolean()
        if (forgeMixin) apply(plugin = "org.spongepowered.mixin")
    }

    java {
        if (isForge) toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))
        if (javaVersion < 9) {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        if (isForge) {
            val compilerProvider = toolchainService.compilerFor {
                languageVersion.set(JavaLanguageVersion.of(javaVersion))
            }
            javaCompiler.set(compilerProvider)
        }
        if (javaVersion >= 9) options.release.set(javaVersion)
    }

    tasks.withType<ProcessResources>().configureEach {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    tasks.withType<Jar>().configureEach {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    val splitEnv = findProperty("split_env").toString().toBoolean()

    val sourceSets = extensions.getByType(SourceSetContainer::class.java)
    sourceSets.named("main") {
        java.srcDir(versionDir.resolve("src/main/java"))
        resources.srcDir(versionDir.resolve("src/main/resources"))
    }

    if (isFabric && splitEnv) sourceSets.matching { it.name == "client" }.configureEach {
        java.srcDir(versionDir.resolve("src/client/java"))
        resources.srcDir(versionDir.resolve("src/client/resources"))
    }

    if (isForge) sourceSets.named("main") {
        resources.srcDir(versionDir.resolve("src/generated/resources"))
    }

    if (isFabric) {
        if (splitEnv) extensions.configure<ExtensionAware>("loom") {
            withGroovyBuilder {
                "splitEnvironmentSourceSets"()
                "mods" {
                    "create"(modId) {
                        "sourceSet"(sourceSets["main"])
                        "sourceSet"(sourceSets["client"])
                    }
                }
            }
        }

        dependencies {
            add("minecraft", "com.mojang:minecraft:${property("minecraft_version")}")
            add("mappings", "net.fabricmc:yarn:${property("yarn_mappings")}:v2")
            add("modImplementation", "net.fabricmc:fabric-loader:${property("loader_version")}")
        }

        tasks.named<ProcessResources>("processResources").configure {
            inputs.property("version", project.version)
            filesMatching("fabric.mod.json") {
                expand("version" to project.version)
            }
        }
    }

    if (isForge) {
        val forgeMixin = findProperty("forge_mixin").toString().toBoolean()
        val coremod = findProperty("forge_coremod")?.toString()
        val mixinVersion = (findProperty("mixin_version") ?: "0.8.5").toString()
        val copyIdeResources = (findProperty("forge_copy_ide_resources") ?: "true").toString().toBoolean()

        val mappingChannel = property("mapping_channel").toString()
        val mappingVersion = property("mapping_version").toString()
        val minecraftVersion = property("minecraft_version").toString()
        val forgeVersion = property("forge_version").toString()
        val minecraftVersionRange = property("minecraft_version_range").toString()
        val forgeVersionRange = property("forge_version_range").toString()
        val loaderVersionRange = property("loader_version_range").toString()
        val modName = property("mod_name").toString()
        val modLicense = property("mod_license").toString()
        val modVersion = property("mod_version").toString()
        val modAuthors = property("mod_authors").toString()
        val modDescription = property("mod_description").toString()

        if (minecraftVersion == "1.18.2") {
            configurations.configureEach {
                resolutionStrategy.force(
                    "net.minecraftforge:coremods:5.0.3",
                    "net.minecraftforge:forgespi:4.0.15-4.x"
                )
            }
        }

        extensions.configure<ExtensionAware>("minecraft") {
            withGroovyBuilder {
                if (copyIdeResources) setProperty("copyIdeResources", true)
                "mappings"("channel" to mappingChannel, "version" to mappingVersion)
                "runs" {
                    "create"("client") {
                        "workingDirectory"(project.file("run"))
                        "property"("forge.logging.markers", "REGISTRIES")
                        "property"("forge.logging.console.level", "debug")
                        "property"("forge.enabledGameTestNamespaces", modId)
                        if (coremod != null) "jvmArg"("-Dfml.coreMods.load=$coremod")
                    }
                }
            }
        }

        if (forgeMixin) {
            extensions.configure<ExtensionAware>("mixin") {
                withGroovyBuilder {
                    "add"(sourceSets["main"], "mixin.durability101.refmap.json")
                    "config"("mixin.durability101.json")
                }
            }

            repositories {
                maven {
                    name = "SpongePowered"
                    url = uri("https://repo.spongepowered.org/repository/maven-public/")
                }
            }
        }

        repositories {
            maven {
                name = "Forge"
                url = uri("https://maven.minecraftforge.net/")
            }
            mavenCentral()
        }

        dependencies {
            add("minecraft", "net.minecraftforge:forge:$minecraftVersion-$forgeVersion")

            if (forgeMixin) {
                add("annotationProcessor", "org.spongepowered:mixin:$mixinVersion:processor")
                add("implementation", "org.spongepowered:mixin:$mixinVersion")
            }
        }

        if (minecraftVersion.startsWith("1.21")) {
            tasks.matching { it.name == "reobfJar" }.configureEach { enabled = false }
            tasks.matching { it.name == "reobfJarJar" }.configureEach { enabled = false }
        }

        tasks.named<ProcessResources>("processResources").configure {
            val replaceProperties = mapOf(
                "minecraft_version" to minecraftVersion,
                "minecraft_version_range" to minecraftVersionRange,
                "forge_version" to forgeVersion,
                "forge_version_range" to forgeVersionRange,
                "loader_version_range" to loaderVersionRange,
                "mod_id" to modId,
                "mod_name" to modName,
                "mod_license" to modLicense,
                "mod_version" to modVersion,
                "mod_authors" to modAuthors,
                "mod_description" to modDescription
            )
            inputs.properties(replaceProperties)

            val modsFile = (findProperty("forge_mods_file") ?: "META-INF/mods.toml").toString()
            filesMatching(listOf(modsFile, "pack.mcmeta")) {
                expand(replaceProperties + mapOf("project" to project))
            }
        }

        if (forgeMixin) {
            tasks.named<Jar>("jar").configure {
                from({
                    configurations["compileClasspath"]
                        .filter { it.name == "mixin-$mixinVersion.jar" }
                        .map { if (it.isDirectory) it else zipTree(it) }
                }) {
                    exclude(
                        "LICENSE.txt",
                        "META-INF/MANIFSET.MF",
                        "META-INF/maven/**",
                        "META-INF/*.RSA",
                        "META-INF/*.SF"
                    )
                }

                manifest {
                    attributes(
                        mapOf(
                            "FMLCorePluginContainsFMLMod" to true,
                            "ForceLoadAsMod" to true,
                            "MixinConfigs" to "mixin.durability101.json",
                            "TweakClass" to "org.spongepowered.asm.launch.MixinTweaker"
                        )
                    )
                }

                finalizedBy("reobfJar")
            }
        } else tasks.named("jar") {
            finalizedBy("reobfJar")
        }

        if (plugins.hasPlugin("eclipse")) extensions.configure<EclipseModel>("eclipse") {
            synchronizationTasks("genEclipseRuns")
        }

        sourceSets.configureEach {
            val dir = layout.buildDirectory.dir("sourcesSets/$name")
            output.setResourcesDir(dir.get().asFile)
            java.destinationDirectory.set(dir)
        }
    }
}
