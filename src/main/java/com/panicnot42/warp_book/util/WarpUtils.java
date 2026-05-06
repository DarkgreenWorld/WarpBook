/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2014-2026
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
		stack.set(Registration.DataComponentRegistry.TARGET_POSITION.get(), GlobalPos.of(player.level().dimension(), p));
		stack.set(Registration.DataComponentRegistry.NAME_IN_BOOK.get(), name);
		return stack;
	}

	public static @NotNull ItemStack bindItemStackToLocation(@NotNull ItemStack stack, @NotNull String name, @NotNull GlobalPos pos)
	{
		stack.set(Registration.DataComponentRegistry.TARGET_POSITION.get(), pos);
		stack.set(Registration.DataComponentRegistry.NAME_IN_BOOK.get(), name);
		return stack;
	}

	public static @NotNull ItemStack bindItemStackToPlayer(@NotNull ItemStack stack, @NotNull Player toPlayer)
	{
		stack.set(Registration.DataComponentRegistry.TARGET_UUID.get(), toPlayer.getUUID());
		stack.set(Registration.DataComponentRegistry.NAME_IN_BOOK.get(), toPlayer.getGameProfile().getName());
		return stack;
	}
}
