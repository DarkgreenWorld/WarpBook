Warp Book
=========

[![Modrinth](https://img.shields.io/badge/Published%20on-Modrinth-1bd96a?logo=modrinth&logoColor=bluegreen)](https://modrinth.com/mod/warp-book)
[![CurseForge](https://img.shields.io/badge/Published%20on-CurseForge-f16436?logo=curseforge&logoColor=orange)](https://www.curseforge.com/minecraft/mc-mods/warp-book)
[![GitHub](https://img.shields.io/badge/%E2%80%8B-GitHub-gray?logo=github&logoColor=black&labelColor=white)](https://github.com/DarkgreenWorld/WarpBook)

This niche mod allows for easy teleportation between given points without admin privileges. It offers a simple alternative to the standard /home and /warp commands.

This is a continuation of the work done by [Panicnot42](https://github.com/Outurnate) and [FerreusVeritas](https://gitlab.com/ferreusveritas/). 
It has been ported to Minecraft Java Edition 1.21 based on NeoForge by contributor [ArcAnc](https://github.com/ArcAnc) and [MelonVRneu](https://github.com/MelonVRneu) using version 2.x. 
This version features a multitude of bug fixes, graphic overhauls, and improved features. I have fixed a large number of bugs in the ported version and start adding more features to the Mod.

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
#### Warp Quill
_(exists in version 4.1.0+)_
Warp Quill is an important ingredient for crafting the Warp Scribing Table. Its crafting recipe is:
#### Warp Scribing Table
_(exists in version 4.1.0+)_
This is the “respawned version” of the Warp Book Cloner. 
It has new textures and a new crafting recipe, but the GUI and usage logic are basically unchanged.
#### Warp Lectern
_(exists in version 4.1.0+)_
Crafting a Warp Lectern consumes one Warp Book. 
If the Warp Book already contains Warp Pages, the resulting Warp Lectern will inherit all of them as well.
The “Warp Book” on a Warp Lectern cannot be removed, so it is basically a Warp Book in block form. 
Aside from not being able to hold Warp Page Deathly, it is no different from a Warp Book.

However, you can enable `onlyOwnerTakesPages` in the config file. 
If you do, then for the Warp Lectern, only the person who inserted a Warp Page can take the corresponding Warp Page out.
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

