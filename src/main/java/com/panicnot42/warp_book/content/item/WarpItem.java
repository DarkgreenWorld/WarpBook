/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.item;

import com.panicnot42.warp_book.content.core.IDeclareWarp;
import com.panicnot42.warp_book.content.core.WarpColors;
import com.panicnot42.warp_book.registration.Registration;
import com.panicnot42.warp_book.warps.Warp;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WarpItem extends Item implements IDeclareWarp, IColorable, Registration.ItemRegistry.IMustBeAddedToCreative
{
	
	public Warp warp = new Warp();
	public boolean cloneable = false;
	
	public WarpItem(@NotNull Item.Properties props)
	{
		super(new Item.Properties());
	}
	
	public WarpItem setWarp(Warp warp)
	{
		this.warp = warp;
		return this;
	}

	public WarpItem setCloneable(boolean is)
	{
		this.cloneable = is;
		return this;
	}
	
	@Override
	public Component getName(TooltipContext ctx, ItemStack stack) {
		return warp.getName(ctx, stack);
	}
	
	@Override
	public GlobalPos getWaypoint(Player player, ItemStack stack)
	{
		return warp.getWaypoint(player, stack);
	}
	
	@Override
	public boolean hasValidData(ItemStack stack) {
		return warp.hasValidData(stack);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag)
	{
		warp.addInformation(stack, context, tooltipComponents, tooltipFlag);
	}
	
	public boolean canGoInBook() {
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getColor(ItemStack stack, int tintIndex) {
		return 0xFFFFFFFF;
	}
	
	@OnlyIn(Dist.CLIENT)
	public WarpColors getWarpColor() {
		return warp.getColor();
	}

	@Override
	public boolean addToCreative()
	{
		return true;
	}

	@Override
	public @NotNull String getDescriptionId()
	{
		return getDescription(this);
	}

	public static @NotNull String getDescription(Item item)
	{
		return BuiltInRegistries.ITEM.getKey(item).withPrefix("item.").toLanguageKey();
	}
}