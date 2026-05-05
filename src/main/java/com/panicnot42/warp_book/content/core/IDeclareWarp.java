/**
 * @author Panicnot42, ArcAnc, Darkgreen_World
 * Copyright (c) 2016-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.core;

import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public interface IDeclareWarp
{
  /** Used by the warpbook for generating it's listing.  Must be used for warp book pages */
  Component getSubName(ItemStack stack);
  
  /** Gets the waypoint for this object */
  GlobalPos getWaypoint(Player player, ItemStack stack);
  
  /** Does this stack have valid waypoint data? */
  boolean hasValidData(ItemStack stack);
  
}
