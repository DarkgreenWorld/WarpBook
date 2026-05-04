/**
 * @author Panicnot42, ArcAnc, Darkgreen_World
 * Copyright (c) 2016-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.gui.inventory.container;

import com.panicnot42.warp_book.content.item.DeathlyWarpPageItem;
import com.panicnot42.warp_book.content.item.WarpBookItem;
import com.panicnot42.warp_book.registration.Registration;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerWarpBookSpecial implements Container
{
	ItemStack deathly, heldItem;
	
	public ContainerWarpBookSpecial(ItemStack heldItem)
	{
		int deaths = WarpBookItem.getRespawnsLeft(heldItem); 
		deathly = deaths == 0 ? ItemStack.EMPTY : new ItemStack(Registration.ItemRegistry.WARP_PAGE_ITEM_DEATHLY.get(), deaths);
		this.heldItem = heldItem;
	}
	
	@Override
	public int getMaxStackSize() {
		return 16;
	}
	
	@Override
	public boolean canPlaceItem(int slot, @NotNull ItemStack itemStack)
	{
		return slot == 0 && itemStack.getItem() instanceof DeathlyWarpPageItem;
	}

	@Override
	public int getContainerSize()
	{
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return deathly.isEmpty();
	}

	@Override
	public @NotNull ItemStack getItem(int slot)
	{
		return slot == 0 ? deathly : ItemStack.EMPTY;
	}

	@Override
	public @NotNull ItemStack removeItem(int slot, int amount)
	{
		ItemStack stack = getItem(slot);
		if (stack != ItemStack.EMPTY)
		{
			if (stack.getCount() > amount)
			{
				stack = stack.split(amount);
				setChanged();
			}
			else {
				setItem(slot, ItemStack.EMPTY);
			}
		}
		return stack;
	}

	@Override
	public @NotNull ItemStack removeItemNoUpdate(int slot)
	{
		ItemStack stack = getItem(slot);
		setItem(slot, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setItem(int slot, @NotNull ItemStack stack)
	{
		if (slot == 0)
			deathly = stack;
		if (stack.getCount() > getMaxStackSize())
			stack.setCount(getMaxStackSize());
		setChanged();
	}

	@Override
	public void setChanged()
	{
		WarpBookItem.setRespawnsLeft(heldItem, deathly == ItemStack.EMPTY ? 0 : deathly.getCount());
	}

	@Override
	public boolean stillValid(@NotNull Player player)
	{
		return true;
	}

	@Override
	public void clearContent()
	{
		setItem(0, ItemStack.EMPTY);
	}
}
