/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, Darkgreen_World
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.gui.inventory;

import com.panicnot42.warp_book.WarpBook;
import com.panicnot42.warp_book.content.gui.inventory.container.ContainerWarpBook;
import com.panicnot42.warp_book.content.gui.inventory.container.ContainerWarpBookSpecial;
import com.panicnot42.warp_book.registration.Registration;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MenuWarpBook extends AbstractContainerMenu
{
	public final ContainerWarpBook inventory;
	
	public MenuWarpBook(int id, Inventory inventoryPlayer, @NotNull ContainerWarpBook inventoryItem, @NotNull ContainerWarpBookSpecial inventorySpecial)
	{
		super(Registration.MenuTypeRegistry.WARP_BOOK.get(), id);
		inventory = inventoryItem;
		for (int i = 0; i < inventoryItem.getContainerSize(); i++)
			this.addSlot(new SlotWarpBook(inventory, i, 8 + (18 * (i % 9)), 18 + (18 * (i / 9))));

		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				this.addSlot(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 140 + i * 18));

		for (int i = 0; i < 9; ++i)
			this.addSlot(new SlotWarpBookInventory(inventoryPlayer, i, 8 + i * 18, 198));

		if (WarpBook.deathPagesEnabled)
			this.addSlot(new SlotWarpBookDeathly(inventorySpecial, 0, 174, 72));
	}

	@Override
	public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotNum)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotNum);

		if (slot.hasItem())
		{
			ItemStack moving = slot.getItem();
			itemstack = moving.copy();

			if (0 <= slotNum && slotNum <= 53)
			{// moving from book

				if (!this.moveItemStackTo(moving, 54, 89, true))
				// to inv
					return ItemStack.EMPTY;

				slot.onQuickCraft(moving, itemstack);
			}
			else if (SlotWarpBook.itemValid(slot.getItem()) && !this.moveItemStackTo(moving, 0, 54, false)) // moving from inv to book
				return ItemStack.EMPTY;

			if (moving.getCount() == 0)
				slot.setByPlayer(ItemStack.EMPTY);
			else
				slot.setChanged();

			if (moving.getCount() == itemstack.getCount())
				return ItemStack.EMPTY;
		}

		return itemstack;
	}

	@Override
	public boolean stillValid(@NotNull Player player)
	{
		return true;
	}
}
