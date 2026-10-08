/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2014-2026
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
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Registration
{
    // Fabric registers immediately; the Supplier keeps every call site the same as the 1.21.1 version
    private static <R, T extends R> Supplier<T> register(Registry<R> registry, String name, Supplier<T> factory)
    {
        T value = Registry.register(registry, Database.rl(name), factory.get());
        return () -> value;
    }

    public static final class ItemRegistry
    {
        // Every item of this mod in registration order. Must stay above the item fields.
        public static final List<Supplier<? extends Item>> ITEMS = new ArrayList<>();

        public static final Supplier<WarpBookItem> WARP_BOOK = item(Database.ITEM_NAME_WARP_BOOK, WarpBookItem :: new);

        public static final Supplier<WarpPageItem> WARP_PAGE_ITEM_PLAYER = item(Database.ITEM_NAME_WARP_PAGE_PLAYER, () -> new WarpPageItem(new Item.Properties()).
                setWarp(new WarpPlayer()).
                setCloneable(false));
        public static final Supplier<WarpPageItem> WARP_PAGE_ITEM_LOCATION = item(Database.ITEM_NAME_WARP_PAGE_LOCATION, () -> new WarpPageItem(new Item.Properties()).
                setWarp(new WarpLocus()).
                setCloneable(true));
        public static final Supplier<DeathlyWarpPageItem> WARP_PAGE_ITEM_DEATHLY = item(Database.ITEM_NAME_WARP_PAGE_DEATHLY, () -> new DeathlyWarpPageItem(new Item.Properties()));

        public static final Supplier<UnboundWarpPageItem> WARP_PAGE_ITEM_UNBOUND = item(Database.ITEM_NAME_WARP_PAGE_UNBOUND, () -> new UnboundWarpPageItem(new Item.Properties()));

        private static <T extends Item> Supplier<T> item(String name, Supplier<T> factory)
        {
            Supplier<T> supplier = register(BuiltInRegistries.ITEM, name, factory);
            ITEMS.add(supplier);
            return supplier;
        }

        public interface IMustBeAddedToCreative
        {
            boolean addToCreative();
        }

        static void init()
        {
        }
    }

    public static final class CreativeTabRegistry
    {
        public static final Supplier<CreativeModeTab> MAIN_TAB = register(BuiltInRegistries.CREATIVE_MODE_TAB, "main", () -> FabricItemGroup.
                        builder().
                        title(Component.translatable(Database.CREATIVE_TAB_TITLE)).
                        icon(() -> ItemRegistry.WARP_BOOK.get().getDefaultInstance()).
                        displayItems((parameters, output) ->
                                output.acceptAll(ItemRegistry.ITEMS.stream().
                                        map(Supplier::get).
                                        filter(item -> item instanceof ItemRegistry.IMustBeAddedToCreative toCreative && toCreative.addToCreative()).
                                        map(Item :: getDefaultInstance).
                                        collect(Collectors.toSet()))).
                        build()
                );

        static void init()
        {
        }
    }

    public static final class SoundRegistry
    {
        public static final Supplier<SoundEvent> DEPART = register(BuiltInRegistries.SOUND_EVENT, Database.SOUND_NAME_DEPART,
                () -> SoundEvent.createVariableRangeEvent(Database.rl(Database.SOUND_NAME_DEPART)));
        public static final Supplier<SoundEvent> ARRIVE = register(BuiltInRegistries.SOUND_EVENT, Database.SOUND_NAME_ARRIVE,
                () -> SoundEvent.createVariableRangeEvent(Database.rl(Database.SOUND_NAME_ARRIVE)));

        static void init()
        {
        }
    }

    public static final class MenuTypeRegistry
    {
        // The client builds its copy of the menu from the book stack the server wrote in WarpBookItem.use()
        public static final Supplier<MenuType<MenuWarpBook>> WARP_BOOK = register(BuiltInRegistries.MENU, Database.CONTAINER_WARP_BOOK,
                () -> new ExtendedScreenHandlerType<MenuWarpBook>((windowId, inv, buf) ->
                {
                    ItemStack bookStack = buf.readItem();
                    return new MenuWarpBook(windowId, inv, new ContainerWarpBook(bookStack), new ContainerWarpBookSpecial(bookStack));
                }));

        static void init()
        {
        }
    }

    // Nested classes only run their static initializers (and thus register) when touched
    public static void init()
    {
        ItemRegistry.init();
        CreativeTabRegistry.init();
        SoundRegistry.init();
        MenuTypeRegistry.init();
    }
}
