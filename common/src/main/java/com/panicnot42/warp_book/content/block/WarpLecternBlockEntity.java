/**
 * @author DarkgreenWorld, Claude Opus 5.5
 * Created at: 2026.10.10
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.block;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.item.WarpBookItem;
import com.panicnot42.warp_book.registration.Registration;
import com.panicnot42.warp_book.content.core.IDeclareWarp;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class WarpLecternBlockEntity extends BlockEntity
{
	private ItemStack book = new ItemStack(Registration.ItemRegistry.WARP_BOOK.get());
	private final NonNullList<UUID> owners = NonNullList.withSize(54, Util.NIL_UUID);
	private int page;

	public WarpLecternBlockEntity(BlockPos pos, BlockState state)
	{
		super(Registration.BlockEntityRegistry.WARP_LECTERN.get(), pos, state);
		// Nobody carries this book when dying, so it gets no death slot
		book.remove(Registration.DataComponentRegistry.WARP_BOOK_DEATHLY.get());
	}

	// The book of a warp lectern, or a warp book lying on a vanilla lectern
	public static @NotNull ItemStack findBook(@NotNull Level level, @NotNull BlockPos pos)
	{
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof WarpLecternBlockEntity warpLectern)
			return warpLectern.book;
		if (blockEntity instanceof LecternBlockEntity lectern && lectern.getBook().getItem() instanceof WarpBookItem)
			return lectern.getBook();
		return ItemStack.EMPTY;
	}

	// With the rule on, a slot holding another player's page cannot be touched
	public boolean isLocked(int slot, @NotNull Player player)
	{
		UUID owner = owners.get(slot);
		return Database.onlyOwnerTakesPages && !owner.equals(Util.NIL_UUID) && !owner.equals(player.getUUID());
	}

	// A slot belongs to the last player who put a different page into it
	public void updateOwners(@NotNull Player player, @NotNull ItemContainerContents before)
	{
		NonNullList<ItemStack> oldPages = NonNullList.withSize(owners.size(), ItemStack.EMPTY);
		NonNullList<ItemStack> newPages = NonNullList.withSize(owners.size(), ItemStack.EMPTY);
		before.copyInto(oldPages);
		WarpBookItem.getContent(book).copyInto(newPages);
		for (int i = 0; i < owners.size(); i++)
		{
			if (newPages.get(i).isEmpty())
				owners.set(i, Util.NIL_UUID);
			else if (!ItemStack.isSameItemSameComponents(oldPages.get(i), newPages.get(i)))
				owners.set(i, player.getUUID());
		}
		setChanged();
	}

	private void setOwners(@NotNull List<UUID> list)
	{
		for (int i = 0; i < owners.size(); i++)
			owners.set(i, i < list.size() ? list.get(i) : Util.NIL_UUID);
	}

	public int getPage()
	{
		return Math.min(page, WarpBookItem.getListPageCount(book) - 1);
	}

	public void setPage(int newPage)
	{
		newPage = Mth.clamp(newPage, 0, WarpBookItem.getListPageCount(book) - 1);
		if (level == null || newPage == getPage())
			return;
		page = newPage;
		setChanged();
		LecternBlock.signalPageChange(level, worldPosition, getBlockState());
	}

	public int getRedstoneSignal()
	{
		int pages = WarpBookItem.getListPageCount(book);
		float progress = pages > 1 ? getPage() / (pages - 1.0F) : 1.0F;
		return Mth.floor(progress * 14.0F) + 1;
	}

	@Override
	protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries)
	{
		super.loadAdditional(tag, registries);
		if (tag.contains(Database.TAG_BOOK))
			book = ItemStack.parse(registries, tag.getCompound(Database.TAG_BOOK)).orElse(book);
		setOwners(Registration.DataComponentRegistry.PAGE_OWNERS.get().codecOrThrow().parse(NbtOps.INSTANCE, tag.get(Database.TAG_OWNERS)).result().orElse(List.of()));
		page = tag.getInt(Database.TAG_PAGE);
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries)
	{
		super.saveAdditional(tag, registries);
		tag.put(Database.TAG_BOOK, book.save(registries));
		tag.put(Database.TAG_OWNERS, Registration.DataComponentRegistry.PAGE_OWNERS.get().codecOrThrow().encodeStart(NbtOps.INSTANCE, owners).getOrThrow());
		tag.putInt(Database.TAG_PAGE, page);
	}

	// Moves the pages between the placed block and its item, the loot table copies these components
	@Override
	protected void applyImplicitComponents(@NotNull DataComponentInput input)
	{
		super.applyImplicitComponents(input);
		WarpBookItem.setWarpBookContent(book, input.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
		setOwners(input.getOrDefault(Registration.DataComponentRegistry.PAGE_OWNERS.get(), List.of()));
	}

	@Override
	protected void collectImplicitComponents(@NotNull DataComponentMap.Builder components)
	{
		super.collectImplicitComponents(components);
		components.set(DataComponents.CONTAINER, WarpBookItem.getContent(book));
		components.set(Registration.DataComponentRegistry.PAGE_OWNERS.get(), List.copyOf(owners));
	}
}
