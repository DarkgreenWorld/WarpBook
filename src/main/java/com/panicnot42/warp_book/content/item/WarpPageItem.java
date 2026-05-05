/**
 * @author Panicnot42, ArcAnc, Darkgreen_World
 * Copyright (c) 2016-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.item;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.WarpBook;
import com.panicnot42.warp_book.content.core.WarpColors;
import com.panicnot42.warp_book.warps.Warp;

import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class WarpPageItem extends WarpItem
{
	
	public WarpPageItem(@NotNull Item.Properties properties)
	{
		super(properties.stacksTo(64));
	}

	@Override
	public int getUseDuration(@NotNull ItemStack stack)
	{
		return 1;
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(
			@NotNull Level level,
			@NotNull Player player,
			@NotNull InteractionHand usedHand)
	{
		ItemStack itemStack = player.getItemInHand(usedHand);
		if(!level.isClientSide())
		{
			if (itemStack.getItem() instanceof WarpItem item)
			{
				GlobalPos pos = item.getWaypoint(player, itemStack);
				if(pos != null)
				{
					WarpBook.warpDrive.processWarp(player, pos);
					player.getItemInHand(usedHand).shrink(1);
				}
			}
		}

		return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
	}
	@Override
	public boolean canGoInBook()
	{
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getColor(@NotNull ItemStack stack, int tintIndex)
	{
		return switch (tintIndex)
		{
            case 0 -> pageColor();
            case 1 -> symbolColor();
            default -> 0xFFFFFFFF;
        };
	}
	
	@OnlyIn(Dist.CLIENT)
	public int pageColor()
	{
		return WarpColors.UNBOUND.getColor();
	}
	
	@OnlyIn(Dist.CLIENT)
	public int symbolColor()
	{
		return getWarpColor().getColor();
	}

	@Override
	public WarpPageItem setWarp(Warp warp)
	{
		return (WarpPageItem) super.setWarp(warp);
	}

	@Override
	public WarpPageItem setCloneable(boolean is)
	{
		return (WarpPageItem) super.setCloneable(is);
	}
}
