/**
 * @author Panicnot42, ArcAnc, Darkgreen_World
 * Copyright (c) 2016-2026
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
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WarpItem extends Item implements IDeclareWarp, IColorable, Registration.ItemRegistry.IMustBeAddedToCreative
{
	
    public Warp warp = new Warp();
    public boolean cloneable = false;

    public WarpItem(@NotNull Item.Properties props)
    {
        super(props);
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
    public @NotNull Component getSubName(@NotNull ItemStack stack) {
        return warp.getSubName(stack);
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

    // 1.20.1 签名：使用 Level 而不是 TooltipContext
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag)
    {
        warp.addInformation(stack, level, tooltipComponents, tooltipFlag);
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