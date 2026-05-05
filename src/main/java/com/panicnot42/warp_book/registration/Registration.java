/**
 * @author ArcAnc, MelonVRneu, Darkgreen_World, Panicnot42, FerreusVeritas
 * Created at: 07.08.2024
 * Copyright (c) 2024-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.registration;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.gui.inventory.MenuWarpBook;
import com.panicnot42.warp_book.content.gui.inventory.container.ContainerWarpBook;
import com.panicnot42.warp_book.content.gui.inventory.container.ContainerWarpBookSpecial;
import com.panicnot42.warp_book.content.item.DeathlyWarpPageItem;
import com.panicnot42.warp_book.content.item.UnboundWarpPageItem;
import com.panicnot42.warp_book.content.item.WarpBookItem;
import com.panicnot42.warp_book.content.item.WarpPageItem;
import com.panicnot42.warp_book.warps.WarpLocus;
import com.panicnot42.warp_book.warps.WarpPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class Registration {

    public static final class ItemRegistry {
        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Database.MOD_ID);

        public static final RegistryObject<WarpBookItem> WARP_BOOK = ITEMS.register(Database.ITEM_NAME_WARP_BOOK, WarpBookItem::new);

        public static final RegistryObject<WarpPageItem> WARP_PAGE_ITEM_PLAYER = ITEMS.register(Database.ITEM_NAME_WARP_PAGE_PLAYER, 
                () -> new WarpPageItem(new Item.Properties()).setWarp(new WarpPlayer()).setCloneable(false));
        
        public static final RegistryObject<WarpPageItem> WARP_PAGE_ITEM_LOCATION = ITEMS.register(Database.ITEM_NAME_WARP_PAGE_LOCATION, 
                () -> new WarpPageItem(new Item.Properties()).setWarp(new WarpLocus()).setCloneable(true));
        
        public static final RegistryObject<DeathlyWarpPageItem> WARP_PAGE_ITEM_DEATHLY = ITEMS.register(Database.ITEM_NAME_WARP_PAGE_DEATHLY, 
                () -> new DeathlyWarpPageItem(new Item.Properties()));

        public static final RegistryObject<UnboundWarpPageItem> WARP_PAGE_ITEM_UNBOUND = ITEMS.register(Database.ITEM_NAME_WARP_PAGE_UNBOUND, 
                () -> new UnboundWarpPageItem(new Item.Properties()));

        public interface IMustBeAddedToCreative {
            boolean addToCreative();
        }

        public static void init(final IEventBus modEventBus) {
            ITEMS.register(modEventBus);
        }
    }

    public static final class CreativeTabRegistry {
        public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Database.MOD_ID);

        public static final RegistryObject<CreativeModeTab> MAIN_TAB = CREATIVE_TABS.register("main", () -> CreativeModeTab.builder()
                .title(Component.translatable(Database.CREATIVE_TAB_TITLE))
                .icon(() -> ItemRegistry.WARP_BOOK.get().getDefaultInstance())
                .displayItems((parameters, output) ->
                        output.acceptAll(ItemRegistry.ITEMS.getEntries().stream()
                                .map(RegistryObject::get)
                                .filter(item -> item instanceof ItemRegistry.IMustBeAddedToCreative toCreative && toCreative.addToCreative())
                                .map(ItemStack::new)
                                .collect(Collectors.toSet())))
                .build()
        );

        public static void init(final IEventBus modEventBus) {
            CREATIVE_TABS.register(modEventBus);
        }
    }

    public static final class SoundRegistry {
        public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Database.MOD_ID);

        public static final RegistryObject<SoundEvent> DEPART = SOUNDS.register(Database.SOUND_NAME_DEPART, 
                () -> SoundEvent.createVariableRangeEvent(Database.rl(Database.SOUND_NAME_DEPART)));
        
        public static final RegistryObject<SoundEvent> ARRIVE = SOUNDS.register(Database.SOUND_NAME_ARRIVE, 
                () -> SoundEvent.createVariableRangeEvent(Database.rl(Database.SOUND_NAME_ARRIVE)));

        public static void init(@NotNull final IEventBus modEventBus) {
            SOUNDS.register(modEventBus);
        }
    }

    public static final class MenuTypeRegistry {
        public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Database.MOD_ID);
        /*
         * public static final RegistryObject<MenuType<MenuWarpBook>> WARP_BOOK = MENU_TYPES.register(Database.CONTAINER_WARP_BOOK, () ->
         *      new MenuType<>((windowId, inv) ->
         *              new MenuWarpBook(windowId, inv, new ContainerWarpBook(ItemStack.EMPTY), new ContainerWarpBookSpecial(ItemStack.EMPTY)), FeatureFlags.DEFAULT_FLAGS));
        */
        public static final RegistryObject<MenuType<MenuWarpBook>> WARP_BOOK = MenuTypeRegistry.MENU_TYPES.register
        		(
        				Database.CONTAINER_WARP_BOOK, () -> IForgeMenuType.create
        				(
        						(windowId, inv, data) -> 
        						{
        							ItemStack heldItem = data.readItem(); 
        							return new MenuWarpBook(windowId, inv, 
        									new ContainerWarpBook(heldItem), 
        									new ContainerWarpBookSpecial(heldItem));
        						}
        				)
        		);

        public static void init(@NotNull final IEventBus modEventBus) {
            MENU_TYPES.register(modEventBus);
        }
    }

    public static void init(final IEventBus modEventBus) {
        ItemRegistry.init(modEventBus);
        CreativeTabRegistry.init(modEventBus);
        SoundRegistry.init(modEventBus);
        MenuTypeRegistry.init(modEventBus);
    }
}