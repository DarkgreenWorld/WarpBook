/**
 * @author ArcAnc, MelonVRneu, DarkgreenWorld, Panicnot42, FerreusVeritas
 * Created at: 07.08.2024
 * Copyright (c) 2024-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.util;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.block.WarpLecternBlockEntity;
import com.panicnot42.warp_book.content.gui.inventory.MenuWarpBook;
import com.panicnot42.warp_book.content.gui.inventory.container.ContainerWarpBook;
import com.panicnot42.warp_book.content.gui.inventory.container.ContainerWarpBookSpecial;
import com.panicnot42.warp_book.content.item.WarpBookItem;
import com.panicnot42.warp_book.content.network.NetworkEngine;
import com.panicnot42.warp_book.content.network.packet.S2COpenLecternPacket;
import com.panicnot42.warp_book.content.savedData.DeathSavedData;
import com.panicnot42.warp_book.platform.Services;
import com.panicnot42.warp_book.registration.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import com.mojang.datafixers.util.Pair;

import java.util.List;

// Called by the event listeners in each loader's WarpBook class
public class EventHandler
{
    public static void onDeath(@NotNull final Player player)
    {
        if (!Database.deathPagesEnabled)
            return;

        Level level = player.level();
        if(!level.isClientSide)
        {
            if (!player.getInventory().contains(stack ->
            		(stack.is(Registration.ItemRegistry.WARP_BOOK.get()) && WarpBookItem.getRespawnsLeft(stack) > 0)
            		 || stack.is(Registration.ItemRegistry.WARP_PAGE_ITEM_DEATHLY.get())
               )) return;

            outer:
            for (NonNullList<ItemStack> list : List.of(player.getInventory().items, player.getInventory().offhand))
            {
                for (int q = 0; q < list.size(); q++)
                {
                    ItemStack item = list.get(q);
                    if (item.is(Registration.ItemRegistry.WARP_BOOK.get()) && WarpBookItem.getRespawnsLeft(item) > 0)
                    {

                    	WarpBookItem.decrRespawnsLeft(item);
                    }
                    else if(item.is(Registration.ItemRegistry.WARP_PAGE_ITEM_DEATHLY.get()))
                    {
                    	item.shrink(1);
                    }
                    else continue;

                    DeathSavedData data = DeathSavedData.getInstance(player.getServer());
                    GlobalPos pos = new GlobalPos(player.level().dimension(),player.blockPosition());
                    data.POSITIONS.put(player.getUUID(),pos);
                    data.setDirty();

                    break outer;
                }
            }
        }
    }

    // Returns true when the click was handled and the vanilla interaction must not run
    public static boolean onUseBlock(@NotNull Player player, @NotNull Level level, @NotNull InteractionHand hand, @NotNull BlockPos pos)
    {
        if (hand != InteractionHand.MAIN_HAND)
            return false;
        // The client cannot see what lies on a vanilla lectern, so it only claims clicks on a warp lectern
        if (level.isClientSide())
            return level.getBlockState(pos).is(Registration.BlockRegistry.WARP_LECTERN.get());

        ItemStack book = WarpLecternBlockEntity.findBook(level, pos);
        if (book.isEmpty() || player.isSpectator() || !(player instanceof ServerPlayer serverPlayer))
            return false;

        BlockEntity lectern = level.getBlockEntity(pos);
        boolean warpLectern = lectern instanceof WarpLecternBlockEntity;
        Component title = warpLectern ? lectern.components().getOrDefault(DataComponents.CUSTOM_NAME, Component.translatable(Database.GUI_TEXT_WARP_LECTERN_INVENTORY_TITLE)) : book.getHoverName();

        if (!player.isSecondaryUseActive())
            NetworkEngine.sendToPlayer(serverPlayer, new S2COpenLecternPacket(pos, book, warpLectern ? ((WarpLecternBlockEntity) lectern).getPage() : 0));
            // Like vanilla, sneaking with an item in hand on a vanilla lectern uses the item instead
        else if (warpLectern || player.getMainHandItem().isEmpty() && player.getOffhandItem().isEmpty())
            Services.PLATFORM.openMenu(serverPlayer, new SimpleMenuProvider(
                    (containerId, inventory, menuPlayer) -> new MenuWarpBook(containerId, inventory, new ContainerWarpBook(book), new ContainerWarpBookSpecial(book), pos, warpLectern),
                    title), Pair.of(book, warpLectern), MenuWarpBook.OPENING_DATA);
        else
            return false;
        return true;
    }

    public static void onPlayerRespawn(@NotNull final Player player, boolean endConquered)
    {
        if (Database.deathPagesEnabled && !endConquered)
        {
            if (!player.level().isClientSide())
            {
                DeathSavedData data = DeathSavedData.getInstance(player.getServer());
                GlobalPos death = data.POSITIONS.get(player.getUUID());
                if(death != null)
                {
                	ItemStack page = new ItemStack(Registration.ItemRegistry.WARP_PAGE_ITEM_LOCATION.get(), 1);
                	WarpUtils.bindItemStackToLocation(page, "Death...", death);
                	if (!player.addItem(page))
                	{
                		EntityType.ITEM.spawn((ServerLevel) player.level(), itemEntity -> itemEntity.setItem(page), player.getOnPos(), MobSpawnType.EVENT, false, false);
                	}
                	data.removeDeath(player.getGameProfile().getId());
                }
            }
        }
    }
}
