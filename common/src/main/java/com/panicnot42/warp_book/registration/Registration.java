/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.registration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.block.WarpScribingTableBlock;
import com.panicnot42.warp_book.content.block.WarpScribingTableBlockEntity;
import com.panicnot42.warp_book.content.block.WarpLecternBlock;
import com.panicnot42.warp_book.content.block.WarpLecternBlockEntity;
import com.panicnot42.warp_book.content.crafting.WarpPageCloningRecipe;
import com.panicnot42.warp_book.content.crafting.WarpShapedRecipe;
import com.panicnot42.warp_book.content.crafting.WarpShapelessRecipe;
import com.panicnot42.warp_book.content.effect.WarpingMobEffect;
import com.panicnot42.warp_book.content.gui.inventory.MenuWarpScribingTable;
import com.panicnot42.warp_book.content.gui.inventory.MenuWarpBook;
import com.panicnot42.warp_book.content.gui.inventory.container.ContainerWarpBook;
import com.panicnot42.warp_book.content.gui.inventory.container.ContainerWarpBookSpecial;
import com.panicnot42.warp_book.content.item.DeathlyWarpPageItem;
import com.panicnot42.warp_book.content.item.UnboundWarpPageItem;
import com.panicnot42.warp_book.content.item.WarpBookItem;
import com.panicnot42.warp_book.content.item.WarpPageItem;
import com.panicnot42.warp_book.platform.Services;
import com.panicnot42.warp_book.warps.WarpLocus;
import com.panicnot42.warp_book.warps.WarpPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.material.MapColor;
import com.mojang.datafixers.util.Pair;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Registration
{
    private static <R, T extends R> Supplier<T> register(Registry<R> registry, String name, Supplier<T> factory)
    {
        return Services.PLATFORM.register(registry, name, factory);
    }

    public static final class DataComponentRegistry
    {
        public static final Supplier<DataComponentType<UUID>> TARGET_UUID = register(BuiltInRegistries.DATA_COMPONENT_TYPE, Database.DATA_COMPONENT_PLAYER_UUID,
                () -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).cacheEncoding().build());

        public static final Supplier<DataComponentType<GlobalPos>> TARGET_POSITION = register(BuiltInRegistries.DATA_COMPONENT_TYPE, Database.DATA_COMPONENT_COORDINATES,
                () -> DataComponentType.<GlobalPos>builder().persistent(GlobalPos.CODEC).networkSynchronized(GlobalPos.STREAM_CODEC).cacheEncoding().build());

        public static final Supplier<DataComponentType<String>> NAME_IN_BOOK = register(BuiltInRegistries.DATA_COMPONENT_TYPE, Database.DATA_COMPONENT_NAME_IN_BOOK,
                () -> DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).cacheEncoding().build());

        public static final Supplier<DataComponentType<Integer>> WARP_BOOK_DEATHLY = register(BuiltInRegistries.DATA_COMPONENT_TYPE, Database.DATA_COMPONENT_WARP_BOOK_DEATHLY,
                () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).cacheEncoding().build());

        public static final Supplier<DataComponentType<List<UUID>>> PAGE_OWNERS = register(BuiltInRegistries.DATA_COMPONENT_TYPE, Database.DATA_COMPONENT_PAGE_OWNERS,
                () -> DataComponentType.<List<UUID>>builder().persistent(UUIDUtil.CODEC.listOf()).networkSynchronized(UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list())).cacheEncoding().build());

        static void init()
        {
        }
    }

    public static final class BlockRegistry
    {
        public static final Supplier<WarpLecternBlock> WARP_LECTERN = register(BuiltInRegistries.BLOCK, Database.BLOCK_NAME_WARP_LECTERN,
                () -> new WarpLecternBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LECTERN)));
        public static final Supplier<WarpScribingTableBlock> WARP_SCRIBING_TABLE = register(BuiltInRegistries.BLOCK, Database.BLOCK_NAME_WARP_SCRIBING_TABLE,
                () -> new WarpScribingTableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(10.0F, 20.0F).requiresCorrectToolForDrops().sound(SoundType.STONE).noOcclusion()));

        public static void makeFlammable()
        {
            ((FireBlock) Blocks.FIRE).setFlammable(WARP_LECTERN.get(), 30, 20);
        }

        static void init()
        {
        }
    }

    public static final class ItemRegistry
    {
        // Must stay above the item fields
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

        public static final Supplier<Item> WARP_QUILL = item(Database.ITEM_NAME_WARP_QUILL, () -> new Item(new Item.Properties()));

        public static final Supplier<BlockItem> WARP_LECTERN = item(Database.BLOCK_NAME_WARP_LECTERN, () -> new BlockItem(BlockRegistry.WARP_LECTERN.get(), new Item.Properties()));
        public static final Supplier<BlockItem> WARP_SCRIBING_TABLE = item(Database.BLOCK_NAME_WARP_SCRIBING_TABLE, () -> new BlockItem(BlockRegistry.WARP_SCRIBING_TABLE.get(), new Item.Properties()));

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

    public static final class BlockEntityRegistry
    {
        public static final Supplier<BlockEntityType<WarpLecternBlockEntity>> WARP_LECTERN = register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Database.BLOCK_NAME_WARP_LECTERN,
                () -> Services.PLATFORM.createBlockEntityType(WarpLecternBlockEntity::new, BlockRegistry.WARP_LECTERN.get()));
        public static final Supplier<BlockEntityType<WarpScribingTableBlockEntity>> WARP_SCRIBING_TABLE = register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Database.BLOCK_NAME_WARP_SCRIBING_TABLE,
                () -> Services.PLATFORM.createBlockEntityType(WarpScribingTableBlockEntity::new, BlockRegistry.WARP_SCRIBING_TABLE.get()));

        static void init()
        {
        }
    }

    public static final class CreativeTabRegistry
    {
        // The no-arg builder is a NeoForge addition, so use the vanilla one
        public static final Supplier<CreativeModeTab> MAIN_TAB = register(BuiltInRegistries.CREATIVE_MODE_TAB, "main", () -> CreativeModeTab.
                        builder(CreativeModeTab.Row.TOP, 0).
                        title(Component.translatable(Database.CREATIVE_TAB_TITLE)).
                        icon(() -> ItemRegistry.WARP_BOOK.get().getDefaultInstance()).
                        displayItems((parameters, output) ->
                                output.acceptAll(ItemRegistry.ITEMS.stream().
                                        map(Supplier::get).
                                        filter(item -> !(item instanceof ItemRegistry.IMustBeAddedToCreative toCreative) || toCreative.addToCreative()).
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
        public static final Supplier<MenuType<MenuWarpBook>> WARP_BOOK = register(BuiltInRegistries.MENU, Database.CONTAINER_WARP_BOOK,
                () -> Services.PLATFORM.<MenuWarpBook, Pair<ItemStack, Boolean>>createMenuType(
                        (windowId, inv, data) -> new MenuWarpBook(windowId, inv, new ContainerWarpBook(data.getFirst()), new ContainerWarpBookSpecial(data.getFirst()), null, data.getSecond()),
                        MenuWarpBook.OPENING_DATA));
        public static final Supplier<MenuType<MenuWarpScribingTable>> WARP_SCRIBING_TABLE = register(BuiltInRegistries.MENU, Database.BLOCK_NAME_WARP_SCRIBING_TABLE,
                () -> Services.PLATFORM.<MenuWarpScribingTable, BlockPos>createMenuType(
                        (windowId, inv, pos) -> new MenuWarpScribingTable(windowId, inv, new SimpleContainer(3), ContainerLevelAccess.NULL),
                        BlockPos.STREAM_CODEC));

        static void init()
        {
        }
    }

    public static final class RecipeSerializerRegistry
    {
        public static final Supplier<RecipeSerializer<WarpShapedRecipe>> SHAPED = register(BuiltInRegistries.RECIPE_SERIALIZER, Database.RECIPE_NAME_SHAPED,
                () -> serializer(WarpShapedRecipe.CODEC, WarpShapedRecipe.STREAM_CODEC));
        public static final Supplier<RecipeSerializer<WarpShapelessRecipe>> SHAPELESS = register(BuiltInRegistries.RECIPE_SERIALIZER, Database.RECIPE_NAME_SHAPELESS,
                () -> serializer(WarpShapelessRecipe.CODEC, WarpShapelessRecipe.STREAM_CODEC));
        public static final Supplier<RecipeSerializer<WarpPageCloningRecipe>> PAGE_CLONING = register(BuiltInRegistries.RECIPE_SERIALIZER, Database.RECIPE_NAME_PAGE_CLONING,
                () -> new SimpleCraftingRecipeSerializer<>(WarpPageCloningRecipe::new));

        private static <T extends Recipe<?>> RecipeSerializer<T> serializer(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec)
        {
            return new RecipeSerializer<>()
            {
                @Override
                public @NotNull MapCodec<T> codec()
                {
                    return codec;
                }

                @Override
                public @NotNull StreamCodec<RegistryFriendlyByteBuf, T> streamCodec()
                {
                    return streamCodec;
                }
            };
        }

        static void init()
        {
        }
    }

    public static final class MobEffectRegistry
    {
        public static final Supplier<WarpingMobEffect> WARPING = register(BuiltInRegistries.MOB_EFFECT, Database.EFFECT_NAME_WARPING, WarpingMobEffect::new);

        static void init()
        {
        }
    }

    public static final class PotionRegistry
    {
        public static final Supplier<Potion> WARPING = register(BuiltInRegistries.POTION, Database.POTION_NAME_WARPING,
                () -> new Potion(Database.MOD_ID + "." + Database.POTION_NAME_WARPING, new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(MobEffectRegistry.WARPING.get()), 3600)));

        // Glowstone and redstone do nothing because no stronger or longer version is registered
        public static void addBrewingRecipes(@NotNull PotionBrewing.Builder builder)
        {
            if (Database.WARPING_POTION_TEST)
                builder.addMix(Potions.AWKWARD, Items.ENDER_PEARL, BuiltInRegistries.POTION.wrapAsHolder(WARPING.get()));
        }

        static void init()
        {
        }
    }

    // Nested classes only register when touched, data components and blocks before items, effects before potions
    public static void init()
    {
        DataComponentRegistry.init();
        BlockRegistry.init();
        ItemRegistry.init();
        BlockEntityRegistry.init();
        CreativeTabRegistry.init();
        SoundRegistry.init();
        MenuTypeRegistry.init();
        RecipeSerializerRegistry.init();
        MobEffectRegistry.init();
        PotionRegistry.init();
    }
}
