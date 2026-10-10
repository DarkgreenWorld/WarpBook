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
import com.panicnot42.warp_book.WarpBook;
import com.panicnot42.warp_book.content.core.IDeclareWarp;
import com.panicnot42.warp_book.content.item.WarpBookItem;
import com.panicnot42.warp_book.registration.Registration;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class WarpLecternBlockEntity extends BlockEntity implements Nameable
{
	private ItemStack book = new ItemStack(Registration.ItemRegistry.WARP_BOOK.get());
	private final NonNullList<UUID> owners = NonNullList.withSize(54, Util.NIL_UUID);
	// The page of the warp list, turned by whoever reads the lectern
	private int page;
	private @Nullable Component name;

	public WarpLecternBlockEntity(BlockPos pos, BlockState state)
	{
		super(Registration.BlockEntityRegistry.WARP_LECTERN.get(), pos, state);
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
		return WarpBook.onlyOwnerTakesPages && !owner.equals(Util.NIL_UUID) && !owner.equals(player.getUUID());
	}

	// A slot belongs to the last player who put a different page into it
	public void updateOwners(@NotNull Player player, @NotNull NonNullList<ItemStack> before)
	{
		NonNullList<ItemStack> pages = WarpBookItem.getContent(book);
		for (int i = 0; i < owners.size(); i++)
		{
			if (pages.get(i).isEmpty())
				owners.set(i, Util.NIL_UUID);
			else if (!ItemStack.isSameItemSameTags(before.get(i), pages.get(i)))
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
		return Math.min(page, getPageCount() - 1);
	}

	// Same as turning a page of a vanilla lectern: a redstone pulse below, a page sound and a new comparator output
	public void setPage(int newPage)
	{
		newPage = Mth.clamp(newPage, 0, getPageCount() - 1);
		if (level == null || newPage == getPage())
			return;
		page = newPage;
		setChanged();
		LecternBlock.signalPageChange(level, worldPosition, getBlockState());
	}

	// Same formula as LecternBlockEntity.getRedstoneSignal, a warp lectern always has its book
	public int getRedstoneSignal()
	{
		int pages = getPageCount();
		float progress = pages > 1 ? getPage() / (pages - 1.0F) : 1.0F;
		return Mth.floor(progress * 14.0F) + 1;
	}

	private int getPageCount()
	{
		long warps = WarpBookItem.getContent(book).stream().filter(stack -> stack.getItem() instanceof IDeclareWarp).count();
		return Math.max(1, (int) ((warps + Database.WARPS_PER_PAGE - 1) / Database.WARPS_PER_PAGE));
	}

	// The loot table copies the book and the owners into the item's BlockEntityTag, placing the item loads them back here
	@Override
	public void load(@NotNull CompoundTag tag)
	{
		super.load(tag);
		if (tag.contains(Database.TAG_BOOK))
			book = ItemStack.of(tag.getCompound(Database.TAG_BOOK));
		setOwners(UUIDUtil.CODEC.listOf().parse(NbtOps.INSTANCE, tag.get(Database.TAG_OWNERS)).result().orElse(List.of()));
		page = tag.getInt(Database.TAG_PAGE);
		name = tag.contains(Database.TAG_CUSTOM_NAME, Tag.TAG_STRING) ? Component.Serializer.fromJson(tag.getString(Database.TAG_CUSTOM_NAME)) : null;
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		super.saveAdditional(tag);
		tag.put(Database.TAG_BOOK, book.save(new CompoundTag()));
		UUIDUtil.CODEC.listOf().encodeStart(NbtOps.INSTANCE, owners).result().ifPresent(list -> tag.put(Database.TAG_OWNERS, list));
		tag.putInt(Database.TAG_PAGE, page);
		if (name != null)
			tag.putString(Database.TAG_CUSTOM_NAME, Component.Serializer.toJson(name));
	}

	// An anvil name given to the warp lectern item, shown as the title of both screens
	@Override
	public @NotNull Component getName()
	{
		return name != null ? name : Component.translatable(Database.GUI_TEXT_WARP_LECTERN_INVENTORY_TITLE);
	}

	@Override
	public @Nullable Component getCustomName()
	{
		return name;
	}

	public void setCustomName(@Nullable Component name)
	{
		this.name = name;
		setChanged();
	}
}
