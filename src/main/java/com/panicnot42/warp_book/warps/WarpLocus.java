/**
 * @author Panicnot42, ArcAnc, Darkgreen_World
 * Copyright (c) 2016-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.warps;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.core.WarpColors;
import com.panicnot42.warp_book.registration.Registration;
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
import java.util.Locale;

public class WarpLocus extends Warp
{
	
	@Override
	public Component getName(@NotNull Item.TooltipContext ctx, @NotNull ItemStack stack)
	{
		if (stack.has(Registration.DataComponentRegistry.NAME_IN_BOOK))
			return Component.literal(stack.get(Registration.DataComponentRegistry.NAME_IN_BOOK));
		return Component.literal(unbound);
	}
	
	@Override
	public GlobalPos getWaypoint(Player player, ItemStack stack)
	{
		if(hasValidData(stack))
		{
			return stack.get(Registration.DataComponentRegistry.TARGET_POSITION);
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
		return stack.has(Registration.DataComponentRegistry.TARGET_POSITION);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, TooltipFlag flagIn)
	{
		tooltip.add(Component.literal(ttprefix).append(getName(context, stack)));
		if(hasValidData(stack))
		{
			GlobalPos pos = stack.get(Registration.DataComponentRegistry.TARGET_POSITION);
			String dimensionName = pos.dimension().location().toShortLanguageKey();
			tooltip.add(Component.translatable(Database.GUI_TEXT_WARP_BOOK_BIND_TOOLTIP,
					pos.pos().getX(),
					pos.pos().getY(),
					pos.pos().getZ(),
					dimensionName.substring(0, 1).toUpperCase(Locale.ROOT) + dimensionName.substring(1).replace('_',' ')));
		}
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public WarpColors getColor() {
		return WarpColors.BOUND;
	}

}
