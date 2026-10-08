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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class WarpUtils
{
	public static @NotNull ItemStack bindItemStackToLocation(@NotNull ItemStack stack, @NotNull String name, @NotNull Player player)
	{
		BlockPos p = player.getOnPos().above();
		setPosition(stack, GlobalPos.of(player.level().dimension(), p));
		setName(stack, name);
		return stack;
	}

	public static @NotNull ItemStack bindItemStackToLocation(@NotNull ItemStack stack, @NotNull String name, @NotNull GlobalPos pos)
	{
		setPosition(stack, pos);
		setName(stack, name);
		return stack;
	}

	public static @NotNull ItemStack bindItemStackToPlayer(@NotNull ItemStack stack, @NotNull Player toPlayer)
	{
		stack.getOrCreateTag().putUUID(Database.TAG_PLAYER_UUID, toPlayer.getUUID());
		setName(stack, toPlayer.getGameProfile().getName());
		return stack;
	}

	// Item NBT accessors. In 1.21.1 these are data components (Registration.DataComponentRegistry).

	public static boolean hasPosition(@NotNull ItemStack stack)
	{
		CompoundTag tag = stack.getTag();
		return tag != null && tag.contains(Database.TAG_COORDINATES);
	}

	public static @Nullable GlobalPos getPosition(@NotNull ItemStack stack)
	{
		CompoundTag tag = stack.getTag();
		if (tag == null || !tag.contains(Database.TAG_COORDINATES))
			return null;
		return GlobalPos.CODEC.parse(NbtOps.INSTANCE, tag.get(Database.TAG_COORDINATES)).result().orElse(null);
	}

	public static void setPosition(@NotNull ItemStack stack, @NotNull GlobalPos pos)
	{
		GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, pos).result().ifPresent(tag -> stack.getOrCreateTag().put(Database.TAG_COORDINATES, tag));
	}

	public static boolean hasName(@NotNull ItemStack stack)
	{
		CompoundTag tag = stack.getTag();
		return tag != null && tag.contains(Database.TAG_NAME_IN_BOOK, Tag.TAG_STRING);
	}

	public static @NotNull String getName(@NotNull ItemStack stack)
	{
		CompoundTag tag = stack.getTag();
		return tag == null ? "" : tag.getString(Database.TAG_NAME_IN_BOOK);
	}

	public static void setName(@NotNull ItemStack stack, @NotNull String name)
	{
		stack.getOrCreateTag().putString(Database.TAG_NAME_IN_BOOK, name);
	}

	public static boolean hasPlayer(@NotNull ItemStack stack)
	{
		CompoundTag tag = stack.getTag();
		return tag != null && tag.hasUUID(Database.TAG_PLAYER_UUID);
	}

	public static @Nullable UUID getPlayer(@NotNull ItemStack stack)
	{
		CompoundTag tag = stack.getTag();
		return tag != null && tag.hasUUID(Database.TAG_PLAYER_UUID) ? tag.getUUID(Database.TAG_PLAYER_UUID) : null;
	}
}
