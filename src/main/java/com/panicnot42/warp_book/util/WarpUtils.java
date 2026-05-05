/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.util;

import com.panicnot42.warp_book.Database;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

public class WarpUtils {
    public static @NotNull ItemStack bindItemStackToLocation(@NotNull ItemStack stack, @NotNull String name, @NotNull Player player)
    {
        BlockPos p = player.getOnPos().above();
        return bindItemStackToLocation(stack, name, GlobalPos.of(player.level().dimension(), p));
    }

    public static @NotNull ItemStack bindItemStackToLocation(@NotNull ItemStack stack, @NotNull String name, @NotNull GlobalPos pos) 
    {
        CompoundTag tag = stack.getOrCreateTag();
        
        tag.putString(Database.TAG_NAME_IN_BOOK, name);
        
        CompoundTag posTag = new CompoundTag();
        posTag.putInt("x", pos.pos().getX());
        posTag.putInt("y", pos.pos().getY());
        posTag.putInt("z", pos.pos().getZ());
        posTag.putString("dimension", pos.dimension().location().toString());

        tag.put(Database.TAG_TARGET_POS, posTag);

        return stack;
    }

    public static @NotNull ItemStack bindItemStackToPlayer(@NotNull ItemStack stack, @NotNull Player toPlayer) 
    {
        CompoundTag tag = stack.getOrCreateTag();
        
        tag.putUUID(Database.TAG_TARGET_UUID, toPlayer.getUUID());
        tag.putString(Database.TAG_NAME_IN_BOOK, toPlayer.getGameProfile().getName());
        
        return stack;
    }

    public static String getNameInBook(ItemStack stack) 
    {
        if (stack.hasTag() && stack.getTag().contains(Database.TAG_NAME_IN_BOOK)) 
        {
            return stack.getTag().getString(Database.TAG_NAME_IN_BOOK);
        }
        return "";
    }
}