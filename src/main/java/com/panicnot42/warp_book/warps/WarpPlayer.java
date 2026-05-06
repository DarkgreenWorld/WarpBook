/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.warps;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.core.WarpColors;
import com.panicnot42.warp_book.registration.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class WarpPlayer extends Warp
{
	
	@Override
	public Component getName(@NotNull Item.TooltipContext ctx, @NotNull ItemStack stack)
	{
		if (hasValidData(stack))
		{
			return Component.literal(stack.get(Registration.DataComponentRegistry.NAME_IN_BOOK.get()));
		}
		return Component.literal(unbound);
	}
	
	@Override
	public GlobalPos getWaypoint(@NotNull Player player, ItemStack stack)
	{
		if(hasValidData(stack))
		{
			UUID playerID = stack.get(Registration.DataComponentRegistry.TARGET_UUID.get());

			Player targetPlayer = player.getServer().getPlayerList().getPlayer(playerID);

			if (targetPlayer != null)
			{
				return new GlobalPos(targetPlayer.level().dimension(), new BlockPos((int)targetPlayer.getX(), (int)targetPlayer.getEyeY(), (int)targetPlayer.getZ()));
			}
			else
			{
				player.sendSystemMessage(Component.translatable(Database.MESSAGE_ERROR_PLAYER_NOTFOUND));
			}
		}
		else
		{
			player.sendSystemMessage(Component.translatable(Database.MESSAGE_ERROR_INVALID_PLAYER));
		}

		return null;
	}
	
	@Override
	public boolean hasValidData(@NotNull ItemStack stack)
	{
		return stack.has(Registration.DataComponentRegistry.TARGET_UUID.get());

	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, TooltipFlag flagIn)
	{
		tooltip.add(Component.literal(ttprefix).append(getName(context, stack)));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public WarpColors getColor() {
		return WarpColors.PLAYER;
	}
	
}
