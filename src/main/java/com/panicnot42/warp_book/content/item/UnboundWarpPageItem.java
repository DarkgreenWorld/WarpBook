/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.item;

import com.panicnot42.warp_book.content.gui.GuiBook;
import com.panicnot42.warp_book.content.gui.GuiWaypointName;
import com.panicnot42.warp_book.registration.Registration;
import com.panicnot42.warp_book.util.WarpUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UnboundWarpPageItem extends WarpPageItem
{

	public UnboundWarpPageItem(@NotNull Item.Properties props) {
		super(props);
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand)
	{
		ItemStack stack = player.getItemInHand(usedHand);

		if (player.isCrouching())
		{
			if(!level.isClientSide())
			{
				ItemStack newPage = WarpUtils.bindItemStackToPlayer(new ItemStack(Registration.ItemRegistry.WARP_PAGE_ITEM_PLAYER.get()), player);
				if(newPage == null || newPage.isEmpty())
				{
					return InteractionResultHolder.fail(stack);
				}
				
				if (!player.addItem(newPage))
				{
					ItemEntity entityItem = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), newPage);
					player.level().addFreshEntity(entityItem);
				}
				
				stack.shrink(1);
			}
			return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
		}
		else
		{
			if (level.isClientSide())
				setWaypointName(player, usedHand);
		}

		return InteractionResultHolder.success(stack);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void setWaypointName(Player player, InteractionHand usedHand)
	{
		Minecraft.getInstance().setScreen(new GuiWaypointName(player, usedHand));
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull Level level, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag)
	{
	}

	@Override
	public boolean canGoInBook()
	{
		return false;
	}
}
