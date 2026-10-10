/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.gui.inventory;

import com.panicnot42.warp_book.content.gui.inventory.container.ContainerWarpBook;
import com.panicnot42.warp_book.content.item.WarpItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SlotWarpBook extends Slot
{
	private final ContainerData lockedSlots;

	public SlotWarpBook(ContainerWarpBook inventory, int i, int j, int k, ContainerData lockedSlots)
	{
		super(inventory, i, j, k);
		this.lockedSlots = lockedSlots;
	}
	
	public static boolean itemValid(@NotNull ItemStack itemStack)
	{
		return itemStack.getItem() instanceof WarpItem warpItem && warpItem.canGoInBook();
	}
	
	public boolean isLocked()
	{
		return lockedSlots.get(getContainerSlot()) != 0;
	}

	// Clicks, drags, number keys, dropping and double click collecting all ask these two first
	@Override
	public boolean mayPlace(@NotNull ItemStack itemStack) {
		return !isLocked() && itemValid(itemStack);
	}

	@Override
	public boolean mayPickup(@NotNull Player player)
	{
		return !isLocked();
	}

	// Shift clicking stacks onto equal pages after checking only this size, not mayPlace
	@Override
	public int getMaxStackSize(@NotNull ItemStack stack)
	{
		return isLocked() ? getItem().getCount() : super.getMaxStackSize(stack);
	}
	
}
