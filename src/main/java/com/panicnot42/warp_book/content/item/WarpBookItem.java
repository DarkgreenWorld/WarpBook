/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.item;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.client.ClientHooks;
import com.panicnot42.warp_book.content.core.IDeclareWarp;
import com.panicnot42.warp_book.content.core.WarpColors;
import com.panicnot42.warp_book.content.gui.inventory.MenuWarpBook;
import com.panicnot42.warp_book.registration.Registration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WarpBookItem extends Item implements IColorable, Registration.ItemRegistry.IMustBeAddedToCreative
{
	
	public WarpBookItem()
	{
		super(new Item.Properties().stacksTo(1));
	}

	@Override
	public int getUseDuration(@NotNull ItemStack stack)
	{
		return 1;
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(
			@NotNull Level level,
			@NotNull Player player,
			@NotNull InteractionHand usedHand)
	{
		ItemStack itemStack = player.getItemInHand(usedHand);

		if (player.isCrouching())
		{
			if (player instanceof ServerPlayer serverPlayer)
				MenuWarpBook.open(serverPlayer, itemStack.getHoverName(), itemStack, null, false);
		}
		else
		{
			if (level.isClientSide())
				ClientHooks.openWarpBookGui(player, usedHand);
		}

		return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
	}

	// Like the vanilla books, a 1.20.1 lectern lets the book place itself, the lectern_books tag only lets the click through
	@Override
	public @NotNull InteractionResult useOn(@NotNull UseOnContext context)
	{
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);
		return state.is(Blocks.LECTERN) && LecternBlock.tryPlaceBook(context.getPlayer(), level, pos, state, context.getItemInHand()) ? InteractionResult.sidedSuccess(level.isClientSide()) : InteractionResult.PASS;
	}

	@Override
	public void appendHoverText(
			@NotNull ItemStack stack,
			@Nullable Level level,
			@NotNull List<Component> tooltipComponents,
			@NotNull TooltipFlag tooltipFlag)
	{
		int amount = countNonEmptyAmount(WarpBookItem.getContent(stack));

		tooltipComponents.add(Component.translatable(Database.GUI_TEXT_WARP_BOOK_TOOLTIP, amount));
	}

	// The pages live in the book's NBT as a 54 slot inventory ("warp_book_content" -> "Items")
	public static @NotNull NonNullList<ItemStack> getContent(@NotNull ItemStack stack)
	{
		NonNullList<ItemStack> items = NonNullList.withSize(54, ItemStack.EMPTY);
		CompoundTag content = stack.getTagElement(Database.TAG_WARP_BOOK_CONTENT);
		if (content != null)
			ContainerHelper.loadAllItems(content, items);
		return items;
	}

	public static void setContent(@NotNull ItemStack stack, @NotNull NonNullList<ItemStack> items)
	{
		ContainerHelper.saveAllItems(stack.getOrCreateTagElement(Database.TAG_WARP_BOOK_CONTENT), items);
	}

	public static int getCopyCost(@NotNull ItemStack book)
	{
		return getContent(book).stream().filter(WarpItem::isCloneable).mapToInt(ItemStack::getCount).sum();
	}

	public static int getListPageCount(@NotNull ItemStack book)
	{
		long warps = getContent(book).stream().filter(stack -> stack.getItem() instanceof IDeclareWarp).count();
		return Math.max(1, (int) ((warps + Database.WARPS_PER_PAGE - 1) / Database.WARPS_PER_PAGE));
	}

	// A new book with only the cloneable pages, each in the same slot as before
	public static @NotNull ItemStack copyBook(@NotNull ItemStack book)
	{
		NonNullList<ItemStack> pages = getContent(book);
		pages.replaceAll(page -> WarpItem.isCloneable(page) ? page : ItemStack.EMPTY);
		ItemStack copy = new ItemStack(Registration.ItemRegistry.WARP_BOOK.get());
		setContent(copy, pages);
		return copy;
	}

	public static int getRespawnsLeft(@NotNull ItemStack item)
	{
		CompoundTag tag = item.getTag();
		return tag == null ? 0 : tag.getInt(Database.TAG_WARP_BOOK_DEATHLY);
	}
	
	public static void setRespawnsLeft(@NotNull ItemStack item, int deaths)
	{
		if (item.getItem() instanceof WarpBookItem)
			item.getOrCreateTag().putInt(Database.TAG_WARP_BOOK_DEATHLY, deaths);
	}
	
	public static void decrRespawnsLeft(ItemStack item)
	{
		setRespawnsLeft(item, getRespawnsLeft(item) - 1);
	}
	
	@Override
	public int getColor(ItemStack stack, int tintIndex)
	{
        return switch (tintIndex)
		{
            case 0 -> WarpColors.LEATHER.getColor();
            case 1 -> WarpColors.UNBOUND.getColor();
            default -> 0xFFFFFFFF;
        };
	}

	@Override
	public boolean addToCreative()
	{
		return true;
	}

	@Override
	public @NotNull String getDescriptionId()
	{
		return WarpItem.getDescription(this);
	}

	public static boolean inventoryIsEmpty(@NotNull List<ItemStack> list)
	{
		for (ItemStack stack : list)
			if (!stack.isEmpty())
				return false;
		return true;
	}

	public static int countNonEmptyAmount(@NotNull List<ItemStack> list)
	{
		int q = 0;

		for (ItemStack stack : list)
			if (!stack.isEmpty())
				q++;
		return q;
	}
}
