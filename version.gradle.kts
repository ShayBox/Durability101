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

    fun requireProp(name: String): String = findProperty(name)?.toString()
            ?: throw GradleException("Missing required property '$name' in $currentProject")

    val versionDir = rootProject.file("versions/$currentProject")
    if (!versionDir.exists()) throw GradleException("Missing version directory: $versionDir")

    val parts = currentProject.split("-", limit = 2)
    if (parts.size < 2) throw GradleException("Invalid project name (expected loader-version): $currentProject")
    val loader = parts[0]
    val isFabric = loader == "fabric"
    val isForge = loader == "forge"

    if (!isFabric && !isForge) throw GradleException("Unknown loader for $currentProject")

    val javaVersion = requireProp("java_version").toInt()
    val toolchainService = extensions.getByType(JavaToolchainService::class.java)
    val skipBuild = findProperty("skip_build").toString().toBoolean()
    val modId = requireProp("mod_id")

    findProperty("maven_group")?.let { group = it }
        ?: findProperty("mod_group_id")?.let { group = it }

    findProperty("mod_version")?.let { version = it }

    base {
        archivesName.set(modId)
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

    val sourceSets = extensions.getByType(SourceSetContainer::class.java)
    sourceSets.named("main") {
        java.srcDir(versionDir.resolve("src/main/java"))
        resources.srcDir(versionDir.resolve("src/main/resources"))
    }

    if (isForge) sourceSets.named("main") {
        resources.srcDir(versionDir.resolve("src/generated/resources"))
    }

    if (isFabric) {
        val modName = property("mod_name").toString()
        val modLicense = property("mod_license").toString()
        val modAuthors = property("mod_authors").toString()
        val modDescription = property("mod_description").toString()

        dependencies {
            add("minecraft", "com.mojang:minecraft:${property("minecraft_version")}")
            add("mappings", "net.fabricmc:yarn:${property("yarn_mappings")}:v2")
            add("modImplementation", "net.fabricmc:fabric-loader:${property("loader_version")}")
        }

        tasks.named<ProcessResources>("processResources").configure {
            val replaceProperties = mapOf(
                "java_version" to javaVersion.toString(),
                "mod_name" to modName,
                "mod_license" to modLicense,
                "mod_authors" to modAuthors,
                "mod_description" to modDescription,
                "version" to project.version.toString(),
            )
            inputs.properties(replaceProperties)
            filesMatching(listOf("fabric.mod.json", "durability101.mixins.json")) {
                expand(replaceProperties)
            }
        }
    }

    if (isForge) {
        val forgeMixin = findProperty("forge_mixin").toString().toBoolean()
        val coremod = findProperty("forge_coremod")?.toString()
        val hasCoremodHooks = findProperty("coremod_target_class") != null

        val mixinVersion = if (forgeMixin) requireProp("mixin_version") else null

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

        val coremodTargetClass = if (hasCoremodHooks) requireProp("coremod_target_class") else null
        val coremodObfMethod = if (hasCoremodHooks) requireProp("coremod_obf_method") else null
        val coremodFontClass = if (hasCoremodHooks) requireProp("coremod_font_class") else null
        val coremodItemStackClass = if (hasCoremodHooks) requireProp("coremod_itemstack_class") else null

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
                setProperty("copyIdeResources", true)
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
                    "config"("durability101.mixins.json")
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
            val replaceProperties = mutableMapOf(
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
                "mod_description" to modDescription,
                "java_version" to javaVersion.toString(),
            )

            if (hasCoremodHooks) {
                replaceProperties["coremod_target_class"] = coremodTargetClass!!
                replaceProperties["coremod_obf_method"] = coremodObfMethod!!
                replaceProperties["coremod_font_class"] = coremodFontClass!!
                replaceProperties["coremod_itemstack_class"] = coremodItemStackClass!!
            }

            inputs.properties(replaceProperties)

            val modsFile = findProperty("forge_mods_file")?.toString() ?: "META-INF/mods.toml"
            val filesToExpand = mutableListOf(modsFile, "META-INF/mods.toml")

            if (hasCoremodHooks) {
                filesToExpand += "ItemRenderer.js"
                filesToExpand += "META-INF/coremods.json"
            } else {
                exclude("ItemRenderer.js", "META-INF/coremods.json")
            }

            if (forgeMixin) {
                filesToExpand += "durability101.mixins.json"
            } else {
                exclude("durability101.mixins.json")
            }

            filesMatching(filesToExpand) {
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
                            "MixinConfigs" to "durability101.mixins.json",
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
