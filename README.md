<p align="center">
<img width="1090" height="256" alt="logo" src="https://github.com/user-attachments/assets/7d9a8e21-b632-4a1d-9b51-109c39f8c049" />
</p>

Warp Book
=========

[![Modrinth](https://img.shields.io/badge/Published%20on-Modrinth-1bd96a?logo=modrinth&logoColor=bluegreen)](https://modrinth.com/mod/warp-book)
[![CurseForge](https://img.shields.io/badge/Published%20on-CurseForge-f16436?logo=curseforge&logoColor=orange)](https://www.curseforge.com/minecraft/mc-mods/warp-book)
[![GitHub](https://img.shields.io/badge/%E2%80%8B-GitHub-gray?logo=github&logoColor=black&labelColor=white)](https://github.com/DarkgreenWorld/WarpBook)

This niche mod allows for easy teleportation between given points without admin privileges. It offers a simple alternative to the standard /home and /warp commands.

This is a continuation of the work done by [Panicnot42](https://github.com/Outurnate) and [FerreusVeritas](https://gitlab.com/ferreusveritas/) in 2016.
It has been ported to Minecraft Java Edition 1.21 based on NeoForge by contributor [ArcAnc](https://github.com/ArcAnc) and [MelonVRneu](https://github.com/MelonVRneu) using version 2.x.
This ported version features a multitude of bug fixes, graphic overhauls, and start adding more features.

### This mod adds several items:

#### Warp Page
Right-clicking with this item will save your coordinates on the page.
Right-clicking during sneaking, it will be bound to the player who holds it.
It is crafted using a Ender Pearl and a sheet of paper (shapeless).
In the 3.0.x-3.3.x versions of the mod, it is crafted using a Warp Potion and a sheet of paper (shapeless).
#### Warp Book
This item can be used for storing and using warp pages.
By storing in warp book, the originally disposable warp page can be used infinitely.
Sneak+right click to add warp pages. Right click brings up a menu of waypoints.
It is crafted using one Ender Pearl and a book (shapeless).
In version 4.1.0+ of the mod, you will be able to place Warp Books on Lecterns.
#### Warp Page Dealthy
When you have a Warp Page Deathly in your inventory or in a Warp Book in your inventory, you will receive a warp page after death that can teleport you back to your death point.
This consumes one Warp Page Deathly, whether or not it is in a Warp Book.

<img width="242" height="111" alt="屏幕截图 2026-10-11 163211" src="https://github.com/user-attachments/assets/a52b9fa9-c868-4a6e-9336-329a7500bdc1" />

#### Warp Quill
_(exists in version 4.1.0+)_
Warp Quill is an important ingredient for crafting the Warp Scribing Table. Its crafting recipe is:

<img width="243" height="113" alt="屏幕截图 2026-10-11 163041" src="https://github.com/user-attachments/assets/12dbd802-316b-4cbe-b3e6-755faaca22d9" />

#### Warp Scribing Table
_(exists in version 4.1.0+)_
This is the “respawned version” of the Warp Book Cloner.
It has new textures and a new crafting recipe, but the GUI and usage logic are basically unchanged.

<img width="243" height="112" alt="屏幕截图 2026-10-11 163141" src="https://github.com/user-attachments/assets/7161e449-6740-413c-b6f9-c37b599961e6" />

#### Warp Lectern
_(exists in version 4.1.0+)_
Crafting a Warp Lectern consumes one Warp Book.
If the Warp Book already contains Warp Pages, the resulting Warp Lectern will inherit all of them as well.
The “Warp Book” on a Warp Lectern cannot be removed, so it is basically a Warp Book in block form.
Aside from not being able to hold Warp Page Deathly, it is no different from a Warp Book.

However, you can enable `onlyOwnerTakesPages` in the config file.
If you do, then for the Warp Lectern, only the person who inserted a Warp Page can take the corresponding Warp Page out.

<img width="246" height="113" alt="屏幕截图 2026-10-11 162952" src="https://github.com/user-attachments/assets/3397f7ca-d42f-4538-b691-99cfba7ad86d" />

#### Warp Potion
_(Only exists in versions 3.0.x-3.3.x)_
A potion that can be bound to a location.
Results in teleportation to the bound location when consumed.
Used as an ingredient for making warp pages.
#### Warp Book Cloner
_(Only exists in versions 3.0.x-3.3.x)_
A workbench for easily copying an entire book.
Just provide the book to be copied, an empty book, and enough blank warp pages to get the job done.
Useful for making backups.
#### Teleport Pad
_(Only exists in versions 3.0.x-3.3.x)_
A permanent teleportation block that activates when walked on.  
Set the destination of the teleport with a bound warp potion.

Source installation information for modders
-------------------------------------------
This code follows the Minecraft Forge installation methodology. It will apply
some small patches to the vanilla MCP source code, giving you and it access 
to some of the data and functions you need to build a successful mod.

Note also that the patches are built against "un-renamed" MCP source code (aka
SRG Names) - this means that you will not be able to read them directly against
normal code.

Setup Process:
==============================

Step 1: Open your command-line and browse to the folder where you extracted the zip file.

Step 2: You're left with a choice.
If you prefer to use Eclipse:
1. Run the following command: `./gradlew genEclipseRuns`
2. Open Eclipse, Import > Existing Gradle Project > Select Folder 
   or run `gradlew eclipse` to generate the project.

If you prefer to use IntelliJ:
1. Open IDEA, and import project.
2. Select your build.gradle file and have it import.
3. Run the following command: `./gradlew genIntellijRuns`
4. Refresh the Gradle Project in IDEA if required.

If at any point you are missing libraries in your IDE, or you've run into problems you can 
run `gradlew --refresh-dependencies` to refresh the local cache. `gradlew clean` to reset everything 
(this does not affect your code) and then start the process again.

Mapping Names:
=============================
By default, the MDK is configured to use the official mapping names from Mojang for methods and fields 
in the Minecraft codebase. These names are covered by a specific license. All modders should be aware of this
license, if you do not agree with it you can change your mapping names to other crowdsourced names in your 
build.gradle. For the latest license text, refer to the mapping file itself, or the reference copy here:
https://github.com/MinecraftForge/MCPConfig/blob/master/Mojang.md

Additional Resources: 
=========================
Community Documentation: https://docs.minecraftforge.net/en/latest/gettingstarted/
LexManos' Install Video: https://youtu.be/8VEdtQLuLO0
Forge Forums: https://forums.minecraftforge.net/
Forge Discord: https://discord.minecraftforge.net/
