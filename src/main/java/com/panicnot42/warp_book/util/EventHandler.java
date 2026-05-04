/**
 * @author ArcAnc, Panicnot42, Darkgreen_World
 * Created at: 07.08.2024
 * Copyright (c) 2024-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.util;

import com.panicnot42.warp_book.WarpBook;
import com.panicnot42.warp_book.content.gui.GuiWarpBookItemInventory;
import com.panicnot42.warp_book.content.item.IColorable;
import com.panicnot42.warp_book.content.item.WarpBookItem;
import com.panicnot42.warp_book.content.network.NetworkEngine;
import com.panicnot42.warp_book.content.savedData.DeathSavedData;
import com.panicnot42.warp_book.registration.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EventHandler
{
    public static void initEvents(@NotNull final IEventBus modEventBus)
    {
        NeoForge.EVENT_BUS.addListener(EventHandler :: onHurt);
        NeoForge.EVENT_BUS.addListener(EventHandler :: onPlayerRespawn);
        modEventBus.addListener(NetworkEngine :: setupMessages);

        if (FMLLoader.getDist().isClient())
        {
            modEventBus.addListener(EventHandler :: registerItemColor);
            modEventBus.addListener(EventHandler :: registerMenuScreens);
        }

    }

    public static void onHurt(@NotNull final LivingDamageEvent.Post event)
    {
        if (!WarpBook.deathPagesEnabled || !(event.getEntity() instanceof Player player))
            return;
        Level level = player.level();
        if (event.getSource() == level.damageSources().fellOutOfWorld() || player.getHealth() > event.getOriginalDamage())
            return;
        if (!player.getInventory().contains(stack -> stack.is(Registration.ItemRegistry.WARP_BOOK) && WarpBookItem.getRespawnsLeft(stack) > 0))
            return;
        for (NonNullList<ItemStack> list : List.of(player.getInventory().items, player.getInventory().offhand))
        {
            for (int q = 0; q < list.size(); q++)
            {
                ItemStack item = list.get(q);
                if (item.is(Registration.ItemRegistry.WARP_BOOK) && WarpBookItem.getRespawnsLeft(item) > 0)
                {
                    WarpBookItem.decrRespawnsLeft(item);
                    if (!player.level().isClientSide())
                    {
                        DeathSavedData data = DeathSavedData.getInstance();
                        data.POSITIONS.put(player.getGameProfile().getId(), new GlobalPos(player.level().dimension(), player.getOnPos()));
                        data.setDirty();
                    }
                    break;
                }
            }
        }

    }

    public static void onPlayerRespawn(@NotNull final PlayerEvent.PlayerRespawnEvent event)
    {
        if (WarpBook.deathPagesEnabled && event.getEntity() instanceof Player player && !event.isEndConquered())
        {
            if (!player.level().isClientSide())
            {
                DeathSavedData data = DeathSavedData.getInstance();
                GlobalPos death = data.POSITIONS.getOrDefault(player.getGameProfile().getId(), new GlobalPos(Level.OVERWORLD, BlockPos.ZERO));

                data.removeDeath(player.getGameProfile().getId());
                ItemStack page = new ItemStack(Registration.ItemRegistry.WARP_PAGE_ITEM_LOCATION.get(), 1);
                WarpUtils.bindItemStackToLocation(page, "Death...", death);
                if (!player.addItem(page))
                {
                    EntityType.ITEM.spawn((ServerLevel) player.level(), itemEntity -> itemEntity.setItem(page), player.getOnPos(), MobSpawnType.EVENT, false, false);
                }
            }
        }
    }

    public static void registerItemColor(@NotNull final RegisterColorHandlersEvent.Item event)
    {
        Registration.ItemRegistry.ITEMS.getEntries().stream().
                map(DeferredHolder::get).
                filter(item -> item instanceof IColorable).
                forEach(item ->
                        event.register((stack, tintIndex) -> ((IColorable)item).getColor(stack, tintIndex), item));
    }

    public static void registerMenuScreens(@NotNull final RegisterMenuScreensEvent event)
    {
        event.register(Registration.MenuTypeRegistry.WARP_BOOK.get(), GuiWarpBookItemInventory :: new);
    }
}
