/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, Darkgreen_World
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.gui.inventory;

import com.panicnot42.warp_book.content.gui.inventory.container.ContainerWarpBook;
import com.panicnot42.warp_book.content.item.WarpItem;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SlotWarpBook extends Slot
{
	public SlotWarpBook(ContainerWarpBook inventory, int i, int j, int k)
	{
		super(inventory, i, j, k);
	}
	
	public static boolean itemValid(@NotNull ItemStack itemStack)
	{
		return itemStack.getItem() instanceof WarpItem warpItem && warpItem.canGoInBook();
	}
	
	@Override
	public boolean mayPlace(@NotNull ItemStack itemStack) {
		return itemValid(itemStack);
	}
	
}
