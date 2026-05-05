/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.warps;

import com.panicnot42.warp_book.content.core.IDeclareWarp;
import com.panicnot42.warp_book.content.core.WarpColors;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Warp implements IDeclareWarp
{

	public static final String unbound = "§4§kUnbound";
	public static final String ttprefix = "§a";

	public Warp()
	{
	}

	@Override
	public Component getName(Item.TooltipContext world, ItemStack stack)
	{
		return null;
	}

	@OnlyIn(Dist.CLIENT)
	public void addInformation(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, TooltipFlag flagIn)
	{
		tooltip.add(Component.literal(ttprefix).append(getName(context, stack)));
	}
	
	@Override
	public GlobalPos getWaypoint(Player player, ItemStack stack)
	{
		return null;
	}

	@Override
	public boolean hasValidData(ItemStack stack)
	{
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	public WarpColors getColor()
	{
		return WarpColors.UNBOUND;
	}
	
}
