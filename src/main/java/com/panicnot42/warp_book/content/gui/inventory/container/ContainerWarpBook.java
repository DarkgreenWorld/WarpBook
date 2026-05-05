/**
 * @author Panicnot42, ArcAnc, Darkgreen_World
 * Copyright (c) 2016-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.gui.inventory.container;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.gui.inventory.SlotWarpBook;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerWarpBook implements Container, Nameable {
    private final ItemStack heldStack;
    private final NonNullList<ItemStack> content;
    public static final int SLOTS_COUNT = 54;
    
    public ContainerWarpBook(ItemStack heldItem) {
    	this.heldStack = heldItem;
    	this.content = NonNullList.withSize(SLOTS_COUNT, ItemStack.EMPTY);
    
    	load();
		}

	private void load() {
		CompoundTag tag = heldStack.getOrCreateTag();
		if (tag.contains("Inventory", 10)) {
			ContainerHelper.loadAllItems(tag.getCompound("Inventory"), this.content);
		}
	}

	@Override
	public int getContainerSize() {
		return SLOTS_COUNT;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : content) {
			if (!stack.isEmpty()) return false;
		}
		return true;
	}

	@Override
	public @NotNull ItemStack getItem(int index) {
		return index >= 0 && index < content.size() ? content.get(index) : ItemStack.EMPTY;
	}

	@Override
	public @NotNull ItemStack removeItem(int index, int count) {
		ItemStack itemstack = ContainerHelper.removeItem(content, index, count);
		if (!itemstack.isEmpty()) {
			this.setChanged();
		}
		return itemstack;
	}

	@Override
	public @NotNull ItemStack removeItemNoUpdate(int index) {
		ItemStack itemstack = ContainerHelper.takeItem(content, index);
		if (!itemstack.isEmpty()) {
			this.setChanged();
		}
		return itemstack;
	}

	@Override
	public void setItem(int index, @NotNull ItemStack stack) {
		content.set(index, stack);
		if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
			stack.setCount(this.getMaxStackSize());
		}
		this.setChanged();
	}

	@Override
	public void setChanged() {
		CompoundTag inventoryTag = new CompoundTag();
		ContainerHelper.saveAllItems(inventoryTag, this.content);
		
		heldStack.getOrCreateTag().put("Inventory", inventoryTag);
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		return !heldStack.isEmpty() && (player.getMainHandItem() == heldStack || player.getOffhandItem() == heldStack);
	}

	@Override
	public void clearContent() {
		this.content.clear();
		this.setChanged();
	}

	@Override
	public @NotNull Component getName() {
		return heldStack.hasCustomHoverName() ? heldStack.getHoverName() : 
			Component.translatable(Database.GUI_TEXT_WARP_BOOK_INVENTORY_TITLE);
	}

	@Override
	public @NotNull Component getDisplayName() {
		return this.getName();
	}

	@Override
	public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
		return SlotWarpBook.itemValid(stack);
	}
}