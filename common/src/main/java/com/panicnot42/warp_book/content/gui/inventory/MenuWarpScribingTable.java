/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2016-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.gui.inventory;

import com.panicnot42.warp_book.content.item.UnboundWarpPageItem;
import com.panicnot42.warp_book.content.item.WarpBookItem;
import com.panicnot42.warp_book.registration.Registration;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class MenuWarpScribingTable extends AbstractContainerMenu
{
	private final Container inputs;
	private final Container result = new SimpleContainer(1);
	private final ContainerLevelAccess access;

	public MenuWarpScribingTable(int id, Inventory inventoryPlayer, Container inputs, ContainerLevelAccess access)
	{
		super(Registration.MenuTypeRegistry.WARP_SCRIBING_TABLE.get(), id);
		checkContainerSize(inputs, 3);
		this.inputs = inputs;
		this.access = access;

		addSlot(inputSlot(0, 16, 48, stack -> stack.getItem() instanceof WarpBookItem && WarpBookItem.getCopyCost(stack) > 0));
		addSlot(inputSlot(1, 80, 24, stack -> stack.getItem() instanceof UnboundWarpPageItem));
		addSlot(inputSlot(2, 80, 72, stack -> stack.getItem() instanceof WarpBookItem && WarpBookItem.getContent(stack).nonEmptyStream().findAny().isEmpty() && WarpBookItem.getRespawnsLeft(stack) == 0));
		addSlot(new Slot(result, 0, 144, 48)
		{
			@Override
			public boolean mayPlace(@NotNull ItemStack stack)
			{
				return false;
			}

			// Another player may have changed the inputs since the result was shown
			@Override
			public boolean mayPickup(@NotNull Player player)
			{
				return ItemStack.matches(getItem(), createResult());
			}

			@Override
			public void onTake(@NotNull Player player, @NotNull ItemStack stack)
			{
				inputs.removeItem(1, WarpBookItem.getCopyCost(inputs.getItem(0)));
				inputs.removeItem(2, 1);
				super.onTake(player, stack);
			}
		});

		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				addSlot(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 98 + i * 18));

		for (int i = 0; i < 9; ++i)
			addSlot(new Slot(inventoryPlayer, i, 8 + i * 18, 156));
	}

	private Slot inputSlot(int index, int x, int y, Predicate<ItemStack> filter)
	{
		return new Slot(inputs, index, x, y)
		{
			@Override
			public boolean mayPlace(@NotNull ItemStack stack)
			{
				return filter.test(stack);
			}
		};
	}

	private ItemStack createResult()
	{
		ItemStack template = inputs.getItem(0);
		int cost = WarpBookItem.getCopyCost(template);
		if (cost == 0 || inputs.getItem(1).getCount() < cost || inputs.getItem(2).isEmpty())
			return ItemStack.EMPTY;
		return WarpBookItem.copyBook(template);
	}

	// Runs every tick on the server, so every player looking at this table sees the same result
	@Override
	public void broadcastChanges()
	{
		result.setItem(0, createResult());
		super.broadcastChanges();
	}

	@Override
	public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotNum)
	{
		Slot slot = slots.get(slotNum);
		if (!slot.hasItem())
			return ItemStack.EMPTY;

		ItemStack moving = slot.getItem();
		ItemStack original = moving.copy();
		if (slotNum < 4 ? !moveItemStackTo(moving, 4, 40, true) : !moveItemStackTo(moving, 0, 3, false))
			return ItemStack.EMPTY;

		if (moving.isEmpty())
			slot.setByPlayer(ItemStack.EMPTY);
		else
			slot.setChanged();
		slot.onTake(player, moving);
		return original;
	}

	@Override
	public boolean stillValid(@NotNull Player player)
	{
		return stillValid(access, player, Registration.BlockRegistry.WARP_SCRIBING_TABLE.get());
	}
}
