/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.item;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.core.WarpColors;
import com.panicnot42.warp_book.content.gui.GuiBook;
import com.panicnot42.warp_book.content.gui.inventory.MenuWarpBook;
import com.panicnot42.warp_book.content.gui.inventory.container.ContainerWarpBook;
import com.panicnot42.warp_book.content.gui.inventory.container.ContainerWarpBookSpecial;
import com.panicnot42.warp_book.registration.Registration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.function.Consumer;

public class WarpBookItem extends Item implements IColorable, Registration.ItemRegistry.IMustBeAddedToCreative
{
	
	public WarpBookItem()
	{
		super(new Item.Properties().stacksTo(1).
				component(DataComponents.CONTAINER, ItemContainerContents.fromItems(NonNullList.withSize(54, ItemStack.EMPTY))).
				component(Registration.DataComponentRegistry.WARP_BOOK_DEATHLY.get(), 0));
	}

	@Override
	public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity)
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
				serverPlayer.openMenu(new MenuProvider()
				{
					@Override
					public @NotNull Component getDisplayName() 
					{
						return itemStack.getHoverName();
					}

					@Override
					public @NotNull MenuWarpBook createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player)
					{
						return new MenuWarpBook(containerId, playerInventory, new ContainerWarpBook(itemStack), new ContainerWarpBookSpecial(itemStack));
					}
				}, 
				buf -> 
				{
				    if (buf instanceof RegistryFriendlyByteBuf registryBuf) 
				    {
				        ItemStack.OPTIONAL_STREAM_CODEC.encode(registryBuf, itemStack);
				    } 
				    else 
				    {
				        ItemStack.OPTIONAL_STREAM_CODEC.encode((RegistryFriendlyByteBuf) buf, itemStack);
				    }
				});
		}
		else
		{
			if (level.isClientSide())
				Minecraft.getInstance().setScreen(new GuiBook(player, usedHand));
		}

		return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
	}

	@Override
	public void appendHoverText(
			@NotNull ItemStack stack,
			@NotNull TooltipContext context,
			@NotNull List<Component> tooltipComponents,
			@NotNull TooltipFlag tooltipFlag)
	{
		int amount = countNonEmptyAmount(WarpBookItem.getContent(stack).nonEmptyStream().collect(Collectors.toList()));

		tooltipComponents.add(Component.translatable(Database.GUI_TEXT_WARP_BOOK_TOOLTIP, amount));
	}

	//@Override
	//public boolean isRepairable(ItemStack stack)
	//{
	//	return false;
	//}

	public static @NotNull ItemContainerContents getContent(@NotNull ItemStack stack)
	{
		return stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.fromItems(NonNullList.withSize(54, ItemStack.EMPTY)));
	}

	public static void setWarpBookContent(@NotNull ItemStack stack, @NotNull ItemContainerContents content)
	{
		stack.set(DataComponents.CONTAINER, content);
	}

	public static int getRespawnsLeft(@NotNull ItemStack item)
	{
		return item.getOrDefault(Registration.DataComponentRegistry.WARP_BOOK_DEATHLY.get(), 0);
	}
	
	public static void setRespawnsLeft(@NotNull ItemStack item, int deaths)
	{
		if (item.has(Registration.DataComponentRegistry.WARP_BOOK_DEATHLY.get()))
			item.set(Registration.DataComponentRegistry.WARP_BOOK_DEATHLY.get(), deaths);
	}
	
	public static void decrRespawnsLeft(ItemStack item)
	{
		setRespawnsLeft(item, getRespawnsLeft(item) - 1);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
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
