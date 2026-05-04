/**
 * @author Panicnot42, ArcAnc, Darkgreen_World
 * Copyright (c) 2016-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.util;

import com.panicnot42.warp_book.registration.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class WarpUtils
{
	public static @NotNull ItemStack bindItemStackToLocation(@NotNull ItemStack stack, @NotNull String name, @NotNull Player player)
	{
		BlockPos p = player.getOnPos().above();
		stack.set(Registration.DataComponentRegistry.TARGET_POSITION, GlobalPos.of(player.level().dimension(), p));
		stack.set(Registration.DataComponentRegistry.NAME_IN_BOOK, name);
		return stack;
	}

	public static @NotNull ItemStack bindItemStackToLocation(@NotNull ItemStack stack, @NotNull String name, @NotNull GlobalPos pos)
	{
		stack.set(Registration.DataComponentRegistry.TARGET_POSITION, pos);
		stack.set(Registration.DataComponentRegistry.NAME_IN_BOOK, name);
		return stack;
	}

	public static @NotNull ItemStack bindItemStackToPlayer(@NotNull ItemStack stack, @NotNull Player toPlayer)
	{
		stack.set(Registration.DataComponentRegistry.TARGET_UUID, toPlayer.getUUID());
		stack.set(Registration.DataComponentRegistry.NAME_IN_BOOK, toPlayer.getGameProfile().getName());
		return stack;
	}
}
