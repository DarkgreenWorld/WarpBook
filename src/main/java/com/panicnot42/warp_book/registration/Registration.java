/**
 * @author ArcAnc, Panicnot42, Darkgreen_World
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
import com.mojang.serialization.Codec;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.stream.Collectors;

public class Registration
{

    public static final class DataComponentRegistry
    {
        public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Database.MOD_ID);

        public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> TARGET_UUID = DATA_COMPONENTS.registerComponentType(Database.DATA_COMPONENT_PLAYER_UUID,
                uuidBuilder -> uuidBuilder.persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).cacheEncoding());

        public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPos>> TARGET_POSITION = DATA_COMPONENTS.registerComponentType(Database.DATA_COMPONENT_COORDINATES,
                posBuilder -> posBuilder.persistent(GlobalPos.CODEC).networkSynchronized(GlobalPos.STREAM_CODEC).cacheEncoding());

        public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> NAME_IN_BOOK = DATA_COMPONENTS.registerComponentType(Database.DATA_COMPONENT_NAME_IN_BOOK,
                stringBuilder -> stringBuilder.persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).cacheEncoding());

        /*public static final DeferredHolder<DataComponentType<?>, DataComponentType<ContainerWarpBook.WarpBookContent>> WARP_BOOK_CONTENT = DATA_COMPONENTS.registerComponentType(Database.DATA_COMPONENT_WARP_BOOK_CONTENT,
                warpBookBuilder -> warpBookBuilder.persistent(ContainerWarpBook.WarpBookContent.CODEC).networkSynchronized(ContainerWarpBook.WarpBookContent.STREAM_CODEC).cacheEncoding());
        */
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> WARP_BOOK_DEATHLY = DATA_COMPONENTS.registerComponentType(Database.DATA_COMPONENT_WARP_BOOK_DEATHLY,
                builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).cacheEncoding());

        public static void init (final IEventBus modEventBus)
        {
            DATA_COMPONENTS.register(modEventBus);
        }
    }

    public static final class ItemRegistry
    {

        public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Database.MOD_ID);

        public static final DeferredItem<WarpBookItem> WARP_BOOK = ITEMS.register(Database.ITEM_NAME_WARP_BOOK, WarpBookItem :: new);

        public static final DeferredItem<WarpPageItem> WARP_PAGE_ITEM_PLAYER = ITEMS.registerItem(Database.ITEM_NAME_WARP_PAGE_PLAYER, properties -> new WarpPageItem(properties).
                setWarp(new WarpPlayer()).
                setCloneable(false));
        public static final DeferredItem<WarpPageItem> WARP_PAGE_ITEM_LOCATION = ITEMS.registerItem(Database.ITEM_NAME_WARP_PAGE_LOCATION, properties -> new WarpPageItem(properties).
                setWarp(new WarpLocus()).
                setCloneable(true));
        public static final DeferredItem<DeathlyWarpPageItem> WARP_PAGE_ITEM_DEATHLY = ITEMS.registerItem(Database.ITEM_NAME_WARP_PAGE_DEATHLY, DeathlyWarpPageItem :: new);

        public static final DeferredItem<UnboundWarpPageItem> WARP_PAGE_ITEM_UNBOUND = ITEMS.registerItem(Database.ITEM_NAME_WARP_PAGE_UNBOUND, UnboundWarpPageItem :: new);

        public interface IMustBeAddedToCreative
        {
            boolean addToCreative();
        }

        public static void init (final IEventBus modEventBus)
        {
            ITEMS.register(modEventBus);
        }
    }

    public static final class CreativeTabRegistry
    {
        public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, Database.MOD_ID);

        public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_TABS.register("main", () -> CreativeModeTab.
                        builder().
                        title(Component.translatable(Database.CREATIVE_TAB_TITLE)).
                        icon(() -> ItemRegistry.WARP_BOOK.get().getDefaultInstance()).
                        displayItems((parameters, output) ->
                                output.acceptAll(ItemRegistry.ITEMS.getEntries().stream().
                                        map(DeferredHolder::get).
                                        filter(item -> item instanceof ItemRegistry.IMustBeAddedToCreative toCreative && toCreative.addToCreative()).
                                        map(Item :: getDefaultInstance).
                                        collect(Collectors.toSet()))).
                        build()
                );

        public static void init (final IEventBus modEventBus)
        {
            CREATIVE_TABS.register(modEventBus);
        }
    }

    public static final class SoundRegistry
    {
        public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Database.MOD_ID);

        public static final DeferredHolder<SoundEvent, SoundEvent> DEPART = SOUNDS.
                register(Database.SOUND_NAME_DEPART, () -> SoundEvent.createVariableRangeEvent(Database.rl(Database.SOUND_NAME_DEPART)));
        public static final DeferredHolder<SoundEvent, SoundEvent> ARRIVE = SOUNDS.
                register(Database.SOUND_NAME_ARRIVE, () -> SoundEvent.createVariableRangeEvent(Database.rl(Database.SOUND_NAME_ARRIVE)));

        public static void init (@NotNull final IEventBus modEventBus)
        {
            SOUNDS.register(modEventBus);
        }
    }

    public static final class MenuTypeRegistry
    {
        public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, Database.MOD_ID);

        public static final DeferredHolder<MenuType<?>, MenuType<MenuWarpBook>> WARP_BOOK = MENU_TYPES.register(Database.CONTAINER_WARP_BOOK, () ->
                new MenuType<>((windowId, inv) ->
                        new MenuWarpBook(windowId, inv, new ContainerWarpBook(ItemStack.EMPTY), new ContainerWarpBookSpecial(ItemStack.EMPTY)), FeatureFlags.DEFAULT_FLAGS));

        public static void init(@NotNull final IEventBus modEventBus)
        {
            MENU_TYPES.register(modEventBus);
        }
    }
    public static void init(final IEventBus modEventBus)
    {
        DataComponentRegistry.init(modEventBus);
        ItemRegistry.init(modEventBus);
        CreativeTabRegistry.init(modEventBus);
        SoundRegistry.init(modEventBus);
        MenuTypeRegistry.init(modEventBus);
    }
}
