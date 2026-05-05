/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.gui.inventory;

import com.panicnot42.warp_book.content.item.DeathlyWarpPageItem;
import com.panicnot42.warp_book.content.gui.inventory.container.ContainerWarpBookSpecial;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SlotWarpBookDeathly extends Slot
{
	public SlotWarpBookDeathly(ContainerWarpBookSpecial inventorySpecial, int i, int j, int k)
	{
		super(inventorySpecial, i, j, k);
	}
	
	public static boolean itemValid(ItemStack itemStack) {
		return itemStack.getItem() instanceof DeathlyWarpPageItem;
	}
	
	@Override
	public boolean mayPlace(@NotNull ItemStack itemStack) {
		return itemValid(itemStack);
	}
	
}
