/**
 * @author Panicnot42, ArcAnc, Darkgreen_World
 * Copyright (c) 2016-2026
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

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
				NetworkHooks.openScreen(serverPlayer, new MenuProvider()
				{
				    @Override
				    public Component getDisplayName()
				    {
				        return Component.empty();
				    }

				    @Override
				    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player)
				    {
				        return new MenuWarpBook(id, inv,
				                new ContainerWarpBook(itemStack),
				                new ContainerWarpBookSpecial(itemStack));
				    }
				}, buf -> buf.writeItem(itemStack));

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
			@NotNull Level level,
			@NotNull List<Component> tooltipComponents,
			@NotNull TooltipFlag tooltipFlag)
	{
	    int amount = (int) WarpBookItem.getContent(stack).stream().filter(itemStack -> !itemStack.isEmpty()).count();

		tooltipComponents.add(Component.translatable(Database.GUI_TEXT_WARP_BOOK_TOOLTIP, amount));
	}

	@Override
	public boolean isRepairable(ItemStack stack)
	{
		return false;
	}

	public static NonNullList<ItemStack> getContent(ItemStack stack)
	{
	    NonNullList<ItemStack> list = NonNullList.withSize(54, ItemStack.EMPTY);
	    CompoundTag rootTag = stack.getTag();
	    
	    if (rootTag == null) return list;
	    if (rootTag.contains("Inventory", 10)) 
	    {
	        CompoundTag inventoryTag = rootTag.getCompound("Inventory");
	        if (inventoryTag.contains("Items", 9)) 
	        {
	            ListTag listTag = inventoryTag.getList("Items", 10);
	            for (int i = 0; i < listTag.size(); i++) 
	            {
	                CompoundTag itemTag = listTag.getCompound(i);
	                int slot = itemTag.getInt("Slot");
	                if (slot >= 0 && slot < list.size()) 
	                {
	                    list.set(slot, ItemStack.of(itemTag));
	                }
	            }
	        }
	    }
	    return list;
	}

	public static void setWarpBookContent(ItemStack stack, NonNullList<ItemStack> list)
	{
	    CompoundTag rootTag = stack.getOrCreateTag();
	    CompoundTag inventoryTag = new CompoundTag();
	    ListTag listTag = new ListTag();
	    
	    for (int i = 0; i < list.size(); i++) 
	    {
	        ItemStack item = list.get(i);
	        if (!item.isEmpty()) 
	        {
	            CompoundTag itemTag = new CompoundTag();
	            itemTag.putInt("Slot", i); 
	            item.save(itemTag);        
	            listTag.add(itemTag);      
	        }
	    }
	    inventoryTag.put("Items", listTag);
	    rootTag.put("Inventory", inventoryTag);
	}

	public static int getRespawnsLeft(@NotNull ItemStack item)
	{
		CompoundTag tag = item.getTag();
	    return tag != null ? tag.getInt("Respawns") : 0;
	}
	
	public static void setRespawnsLeft(@NotNull ItemStack item, int deaths)
	{
		item.getOrCreateTag().putInt("Respawns", deaths);
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
