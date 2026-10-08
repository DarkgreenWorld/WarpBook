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
import com.panicnot42.warp_book.util.WarpUtils;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class WarpLocus extends Warp
{
	
	@Override
	public Component getName(@Nullable Level level, @NotNull ItemStack stack)
	{
		if (WarpUtils.hasName(stack))
			return Component.literal(WarpUtils.getName(stack));
		return Component.literal(unbound);
	}
	
	@Override
	public GlobalPos getWaypoint(Player player, ItemStack stack)
	{
		if(hasValidData(stack))
		{
			return WarpUtils.getPosition(stack);
		}
		else
		{
			player.sendSystemMessage(Component.translatable(Database.MESSAGE_ERROR_INVALID_POSITION));
		}
		return null;
	}
	
	@Override
	public boolean hasValidData(ItemStack stack)
	{
		return WarpUtils.hasPosition(stack);
	}
	
	@Override
	public void addInformation(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, TooltipFlag flagIn)
	{
		tooltip.add(Component.literal(ttprefix).append(getName(level, stack)));
		GlobalPos pos = WarpUtils.getPosition(stack);
		if(pos != null)
		{
			String dimensionName = pos.dimension().location().toShortLanguageKey();
			tooltip.add(Component.translatable(Database.GUI_TEXT_WARP_BOOK_BIND_TOOLTIP,
					pos.pos().getX(),
					pos.pos().getY(),
					pos.pos().getZ(),
					dimensionName.substring(0, 1).toUpperCase(Locale.ROOT) + dimensionName.substring(1).replace('_',' ')));
		}
	}
	
	@Override
	public WarpColors getColor() {
		return WarpColors.BOUND;
	}

}
