/**
 * @author MelonVRneu, ArcAnc, DarkgreenWorld, Panicnot42, FerreusVeritas
 * Created at: 07.08.2024
 * Copyright (c) 2024-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.util;

import com.google.common.eventbus.Subscribe;
import com.panicnot42.warp_book.WarpBook;
import com.panicnot42.warp_book.content.gui.GuiWarpBookItemInventory;
import com.panicnot42.warp_book.content.item.IColorable;
import com.panicnot42.warp_book.content.item.WarpBookItem;
import com.panicnot42.warp_book.content.network.NetworkEngine;
import com.panicnot42.warp_book.content.savedData.DeathSavedData;
import com.panicnot42.warp_book.registration.Registration;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient.ClientEvents;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.List;

public class EventHandler
{
    public static void initEvents(@NotNull final IEventBus modEventBus) 
    {
        MinecraftForge.EVENT_BUS.register(EventHandler.class);

        NetworkEngine.setupMessages();

        if (FMLLoader.getDist() == Dist.CLIENT) {
            modEventBus.register(ClientEvents.class);
        }
    }

    @SubscribeEvent
    public static void onDeath(@NotNull final LivingDeathEvent event)
    {
        if (!WarpBook.deathPagesEnabled || !(event.getEntity() instanceof Player player))
            return;
        
        Level level = player.level();
        if(!level.isClientSide)
        {
            if (!player.getInventory().contains(stack -> 
            		(stack.getItem() == Registration.ItemRegistry.WARP_BOOK.get() && WarpBookItem.getRespawnsLeft(stack) > 0) 
            		 || stack.getItem() == Registration.ItemRegistry.WARP_PAGE_ITEM_DEATHLY.get()
               )) return;

            outer:
            for (NonNullList<ItemStack> list : List.of(player.getInventory().items, player.getInventory().offhand))
            {
                for (int q = 0; q < list.size(); q++)
                {
                    ItemStack item = list.get(q);
                    if (item.getItem() == Registration.ItemRegistry.WARP_BOOK.get() && WarpBookItem.getRespawnsLeft(item) > 0)
                    {

                    	WarpBookItem.decrRespawnsLeft(item);
                    }
                    else if(item.getItem() == Registration.ItemRegistry.WARP_PAGE_ITEM_DEATHLY.get())
                    {
                    	item.shrink(1);
                    }
                    else continue;

                    DeathSavedData data = DeathSavedData.getInstance();
                    GlobalPos pos = new GlobalPos(player.level().dimension(),player.blockPosition());
                    data.POSITIONS.put(player.getUUID(),pos);
                    data.setDirty();
                        
                    break outer;
                }
            } 
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(@NotNull final PlayerEvent.PlayerRespawnEvent event)
    {
        if (WarpBook.deathPagesEnabled && event.getEntity() instanceof Player player && !event.isEndConquered())
        {
            if (!player.level().isClientSide())
            {
                DeathSavedData data = DeathSavedData.getInstance();
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

    public static class ClientEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) 
        {
            event.enqueueWork(() -> 
            {
                MenuScreens.register(
                    Registration.MenuTypeRegistry.WARP_BOOK.get(), 
                    GuiWarpBookItemInventory::new
                );
            });
        }

        @SubscribeEvent
        public static void onItemColors(RegisterColorHandlersEvent.Item event) 
        {
            Registration.ItemRegistry.ITEMS.getEntries().forEach(holder -> 
            {
                if (holder.get() instanceof IColorable colorable) 
                {
                    event.register((stack, tintIndex) -> colorable.getColor(stack, tintIndex), holder.get());
                }
            });
        }
    }
}
