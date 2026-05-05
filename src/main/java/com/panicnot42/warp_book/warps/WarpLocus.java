/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, Darkgreen_World
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.warps;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.core.WarpColors;
import com.panicnot42.warp_book.registration.Registration;
import com.panicnot42.warp_book.util.WarpUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
import java.util.Locale;

public class WarpLocus extends Warp
{
	@Override
	public Component getSubName(@NotNull ItemStack stack)
	{
		if (stack.hasTag() && stack.getTag().contains(Database.TAG_NAME_IN_BOOK))
			return Component.literal(stack.getTag().getString(Database.TAG_NAME_IN_BOOK));
		return Component.literal(unbound);
	}
	
	@Override
	public GlobalPos getWaypoint(Player player, ItemStack stack)
	{
		GlobalPos pos = getGlobalPos(stack);
		if(pos != null)
		{
			return pos;
		}
		
		player.sendSystemMessage(Component.translatable(Database.MESSAGE_ERROR_INVALID_POSITION));
		return null;
	}
	
	@Override
	public boolean hasValidData(@NotNull ItemStack stack)
	{
		return stack.hasTag() && stack.getTag().contains(Database.TAG_NAME_IN_BOOK);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, TooltipFlag flagIn)
	{
		tooltip.add(Component.literal(ttprefix).append(getSubName(stack)));
		GlobalPos pos = getGlobalPos(stack);
		if (pos != null) 
		{
			String dimensionName = pos.dimension().location().getPath();
			tooltip.add(Component.translatable(Database.GUI_TEXT_WARP_BOOK_BIND_TOOLTIP,
				pos.pos().getX(),
				pos.pos().getY(),
				pos.pos().getZ(),
				dimensionName.substring(0, 1).toUpperCase(Locale.ROOT) + dimensionName.substring(1).replace('_',' ')));
		}
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public WarpColors getColor() 
	{
		return WarpColors.BOUND;
	}
	
    public static GlobalPos getGlobalPos(ItemStack stack) 
    {
    	if (stack.hasTag())
    	{
    		CompoundTag tag = stack.getTag();
    		if (tag.contains(Database.TAG_TARGET_POS))
    		{
    			CompoundTag pos = tag.getCompound(Database.TAG_TARGET_POS);
    			if(pos.contains("x") && pos.contains("y") && pos.contains("z") && pos.contains("dimension"))
    			{   
    				BlockPos blockPos = new BlockPos(pos.getInt("x"), pos.getInt("y"), pos.getInt("z"));
    				ResourceLocation dimRl = new ResourceLocation(pos.getString("dimension"));
    				ResourceKey<Level> dimKey = ResourceKey.create(Registries.DIMENSION, dimRl);
    				
    				return GlobalPos.of(dimKey, blockPos);
    			}
    		}
    	}
        return null;
    }
}
