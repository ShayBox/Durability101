<div align="center">
  <a href="https://discord.shaybox.com">
    <img alt="Discord" src="https://img.shields.io/discord/824865729445888041?color=404eed&label=Discord&logo=Discord&logoColor=FFFFFF">
  </a>
  <a href="https://curseforge.com/minecraft/mc-mods/durability101/files/all">
    <img alt="CurseForge" src="https://img.shields.io/curseforge/dt/325020?color=f16436&label=CurseForge&logo=curseforge&logoColor=FFFFFF">
  </a>
  <a href="https://modrinth.com/mod/durability101/versions">
    <img alt="Modrinth" src="https://img.shields.io/modrinth/dt/durability101?color=00d845&label=Modrinth&logo=curseforge&logoColor=FFFFFF">
  </a>
</div>

# Durability101
[A rewrite of the original Durability101 mod](https://minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/1285383-1-6-2-jul-13-durability101-hotbar-visible-use)

![Screenshot](https://i.imgur.com/Le7cALn.png)

## Forge
| Minecraft | Type        |
|-----------|-------------|
| 1.12-2    | [Mixin]     |
| 1.13.2    | [CoreMod]   |
| 1.14.2-4  | [CoreMod]   |
| 1.15-2    | [CoreMod]   |
| 1.16.1-5  | [CoreMod]   |
| 1.17.1    | [CoreMod]   |
| 1.18-2    | [CoreMod]   |
| 1.19-2    | [CoreMod]   |
| 1.19.2    | [Decorator] |
| 1.19.3    | [Decorator] |
| 1.19.4    | [Decorator] |
| 1.20-5    | [Decorator] |

### Notes
- 1.16.4 and below requires Java 8 312
- 1.16.5 requires FML 36.3 or Java 8 312
- 1.19.2 requires FML 43.1 or use 1.19-2
- 1.20.4 and below requires Java 17
- 1.20.5 and above requires Java 21
- TODO: Support 1.20.6
- TODO: Support NeoForge

## Fabric
| Minecraft | Type    |
|-----------|---------|
| 1.14-4    | [Mixin] |
| 1.15-2    | [Mixin] |
| 1.16-5    | [Mixin] |
| 1.17-1    | [Mixin] |
| 1.18-2    | [Mixin] |
| 1.19-2    | [Mixin] |
| 1.19.3    | [Mixin] |
| 1.19.4    | [Mixin] |
| 1.20-6    | [Mixin] |

### Notes
- 1.16.4 and below requires Java 8
- 1.20.4 and below requires Java 17
- 1.20.5 and above requires Java 21
- TODO: Support ClientItemExtensions
- TODO: Support Quilt

### Long Term Goals
- Switch to Architectury for 1.16+
- Switch Forge 1.13-1.15 to Mixins
- Create a common library
- Create a config file

[CoreMod]: https://github.com/MinecraftForge/CoreMods
[Decorator]: https://github.com/MinecraftForge/MinecraftForge/pull/8794
[Mixin]: https://github.com/SpongePowered/Mixin