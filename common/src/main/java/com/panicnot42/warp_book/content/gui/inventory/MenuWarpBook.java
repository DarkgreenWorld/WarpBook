/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.gui.inventory;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.block.WarpLecternBlockEntity;
import com.panicnot42.warp_book.content.gui.inventory.container.ContainerWarpBook;
import com.panicnot42.warp_book.content.gui.inventory.container.ContainerWarpBookSpecial;
import com.panicnot42.warp_book.content.item.WarpBookItem;
import com.panicnot42.warp_book.registration.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class MenuWarpBook extends AbstractContainerMenu
{
	public static final StreamCodec<RegistryFriendlyByteBuf, Pair<ItemStack, Boolean>> OPENING_DATA = StreamCodec.composite(
			ItemStack.OPTIONAL_STREAM_CODEC, Pair::getFirst,
			ByteBufCodecs.BOOL, Pair::getSecond,
			Pair::of);
	public final ContainerWarpBook inventory;
	private final @Nullable BlockPos lecternPos;
	public final boolean warpLectern;

	public MenuWarpBook(int id, Inventory inventoryPlayer, @NotNull ContainerWarpBook inventoryItem, @NotNull ContainerWarpBookSpecial inventorySpecial)
	{
		this(id, inventoryPlayer, inventoryItem, inventorySpecial, null, false);
	}
	public MenuWarpBook(int id, Inventory inventoryPlayer, @NotNull ContainerWarpBook inventoryItem, @NotNull ContainerWarpBookSpecial inventorySpecial, @Nullable BlockPos lecternPos, boolean warpLectern)
	{
		super(Registration.MenuTypeRegistry.WARP_BOOK.get(), id);
		inventory = inventoryItem;
		this.lecternPos = lecternPos;
		this.warpLectern = warpLectern;
		Player player = inventoryPlayer.player;
		// The server reads the owners live, the client receives the same flags as synced container data
		ContainerData lockedSlots = lecternPos == null ? new SimpleContainerData(inventoryItem.getContainerSize()) : new ContainerData()
		{
			@Override
			public int get(int index)
			{
				return player.level().getBlockEntity(lecternPos) instanceof WarpLecternBlockEntity lectern && lectern.isLocked(index, player) ? 1 : 0;
			}

			@Override
			public void set(int index, int value)
			{
			}

			@Override
			public int getCount()
			{
				return inventoryItem.getContainerSize();
			}
		};
		addDataSlots(lockedSlots);

		for (int i = 0; i < inventoryItem.getContainerSize(); i++)
			this.addSlot(new SlotWarpBook(inventory, i, 8 + (18 * (i % 9)), 18 + (18 * (i / 9)), lockedSlots));

		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				this.addSlot(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 140 + i * 18));

		for (int i = 0; i < 9; ++i)
			this.addSlot(new SlotWarpBookInventory(inventoryPlayer, i, 8 + i * 18, 198));

		if (Database.deathPagesEnabled && !warpLectern)
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
	public void clicked(int slotId, int button, @NotNull ClickType clickType, @NotNull Player player)
	{
		if (!stillValid(player))
			return;

		BlockEntity lectern = lecternPos == null ? null : player.level().getBlockEntity(lecternPos);
		ItemContainerContents before = WarpBookItem.getContent(inventory.heldStack);
		int pages = WarpBookItem.getListPageCount(inventory.heldStack);
		super.clicked(slotId, button, clickType, player);
		if (lectern instanceof WarpLecternBlockEntity warpLectern)
			warpLectern.updateOwners(player, before);
		else if (lectern != null)
			lectern.setChanged();
		if (lectern instanceof LecternBlockEntity vanillaLectern && WarpBookItem.getListPageCount(inventory.heldStack) != pages)
			vanillaLectern.setBook(vanillaLectern.getBook());
	}

	// Closes the menu once the book is no longer on the lectern or the player walked away
	@Override
	public boolean stillValid(@NotNull Player player)
	{
		return lecternPos == null || player.canInteractWithBlock(lecternPos, 4.0) && WarpLecternBlockEntity.findBook(player.level(), lecternPos) == inventory.heldStack;
	}
}
