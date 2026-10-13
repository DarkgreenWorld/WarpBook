/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.registration;

import com.google.gson.JsonObject;
import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.block.WarpLecternBlock;
import com.panicnot42.warp_book.content.block.WarpLecternBlockEntity;
import com.panicnot42.warp_book.content.block.WarpScribingTableBlock;
import com.panicnot42.warp_book.content.block.WarpScribingTableBlockEntity;
//import com.panicnot42.warp_book.content.crafting.WarpPageCloningRecipe;
import com.panicnot42.warp_book.content.crafting.WarpShapedRecipe;
import com.panicnot42.warp_book.content.crafting.WarpShapelessRecipe;
import com.panicnot42.warp_book.content.effect.WarpingMobEffect;
import com.panicnot42.warp_book.content.gui.inventory.MenuWarpBook;
import com.panicnot42.warp_book.content.gui.inventory.MenuWarpScribingTable;
import com.panicnot42.warp_book.content.gui.inventory.container.ContainerWarpBook;
import com.panicnot42.warp_book.content.gui.inventory.container.ContainerWarpBookSpecial;
import com.panicnot42.warp_book.content.item.DeathlyWarpPageItem;
import com.panicnot42.warp_book.content.item.UnboundWarpPageItem;
import com.panicnot42.warp_book.content.item.WarpBookItem;
import com.panicnot42.warp_book.content.item.WarpPageItem;
import com.panicnot42.warp_book.warps.WarpLocus;
import com.panicnot42.warp_book.warps.WarpPlayer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.flag.FeatureFlags;
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
//import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
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

    public static final class BlockRegistry
    {
        public static final Supplier<WarpLecternBlock> WARP_LECTERN = register(BuiltInRegistries.BLOCK, Database.BLOCK_NAME_WARP_LECTERN,
                () -> new WarpLecternBlock(BlockBehaviour.Properties.copy(Blocks.LECTERN)));
        public static final Supplier<WarpScribingTableBlock> WARP_SCRIBING_TABLE = register(BuiltInRegistries.BLOCK, Database.BLOCK_NAME_WARP_SCRIBING_TABLE,
                () -> new WarpScribingTableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(10.0F, 20.0F).requiresCorrectToolForDrops().sound(SoundType.STONE).noOcclusion()));

        // Same odds as the vanilla lectern, vanilla keeps them per block so a copy of the properties is not enough
        public static void makeFlammable()
        {
            FlammableBlockRegistry.getDefaultInstance().add(WARP_LECTERN.get(), 30, 20);
        }

        static void init()
        {
        }
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
        // The supplier interface is opened by Fabric API's transitive access wideners
        public static final Supplier<BlockEntityType<WarpLecternBlockEntity>> WARP_LECTERN = register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Database.BLOCK_NAME_WARP_LECTERN,
                () -> BlockEntityType.Builder.of(WarpLecternBlockEntity::new, BlockRegistry.WARP_LECTERN.get()).build(null));
        public static final Supplier<BlockEntityType<WarpScribingTableBlockEntity>> WARP_SCRIBING_TABLE = register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Database.BLOCK_NAME_WARP_SCRIBING_TABLE,
                () -> BlockEntityType.Builder.of(WarpScribingTableBlockEntity::new, BlockRegistry.WARP_SCRIBING_TABLE.get()).build(null));

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
                                        filter(item -> !(item instanceof ItemRegistry.IMustBeAddedToCreative toCreative) || toCreative.addToCreative()).
                                        map(Item :: getDefaultInstance).
                                        toList())).
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
        // The client builds its copy of the menu from the book and the warp lectern flag, see MenuWarpBook.open
        public static final Supplier<MenuType<MenuWarpBook>> WARP_BOOK = register(BuiltInRegistries.MENU, Database.CONTAINER_WARP_BOOK,
                () -> new ExtendedScreenHandlerType<MenuWarpBook>((windowId, inv, buf) ->
                {
                    ItemStack bookStack = buf.readItem();
                    return new MenuWarpBook(windowId, inv, new ContainerWarpBook(bookStack), new ContainerWarpBookSpecial(bookStack), null, buf.readBoolean());
                }));
        // The constructor is opened by Fabric API's transitive access wideners
        public static final Supplier<MenuType<MenuWarpScribingTable>> WARP_SCRIBING_TABLE = register(BuiltInRegistries.MENU, Database.BLOCK_NAME_WARP_SCRIBING_TABLE,
                () -> new MenuType<>((windowId, inv) -> new MenuWarpScribingTable(windowId, inv, new SimpleContainer(3), ContainerLevelAccess.NULL), FeatureFlags.VANILLA_SET));

        static void init()
        {
        }
    }

    public static final class RecipeSerializerRegistry
    {
        public static final Supplier<RecipeSerializer<WarpShapedRecipe>> SHAPED = register(BuiltInRegistries.RECIPE_SERIALIZER, Database.RECIPE_NAME_SHAPED,
                () -> serializer(RecipeSerializer.SHAPED_RECIPE, WarpShapedRecipe::new, recipe -> recipe.warpingPotionTest));
        public static final Supplier<RecipeSerializer<WarpShapelessRecipe>> SHAPELESS = register(BuiltInRegistries.RECIPE_SERIALIZER, Database.RECIPE_NAME_SHAPELESS,
                () -> serializer(RecipeSerializer.SHAPELESS_RECIPE, WarpShapelessRecipe::new, recipe -> recipe.warpingPotionTest));
        /*
        public static final Supplier<RecipeSerializer<WarpPageCloningRecipe>> PAGE_CLONING = register(BuiltInRegistries.RECIPE_SERIALIZER, Database.RECIPE_NAME_PAGE_CLONING,
                () -> new SimpleCraftingRecipeSerializer<>(WarpPageCloningRecipe::new));
         */

        // The vanilla serializer reads the recipe, this one adds the optional warping_potion_test field
        private static <V extends Recipe<?>, T extends V> RecipeSerializer<T> serializer(RecipeSerializer<V> vanilla, BiFunction<V, Optional<Boolean>, T> factory, Function<T, Optional<Boolean>> warpingPotionTest)
        {
            return new RecipeSerializer<>()
            {
                @Override
                public @NotNull T fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json)
                {
                    return factory.apply(vanilla.fromJson(id, json), json.has("warping_potion_test") ? Optional.of(GsonHelper.getAsBoolean(json, "warping_potion_test")) : Optional.empty());
                }

                @Override
                public @NotNull T fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf)
                {
                    return factory.apply(vanilla.fromNetwork(id, buf), buf.readOptional(FriendlyByteBuf::readBoolean));
                }

                @Override
                public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull T recipe)
                {
                    vanilla.toNetwork(buf, recipe);
                    buf.writeOptional(warpingPotionTest.apply(recipe), FriendlyByteBuf::writeBoolean);
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
                () -> new Potion(Database.MOD_ID + "." + Database.POTION_NAME_WARPING, new MobEffectInstance(MobEffectRegistry.WARPING.get(), 1800)));

        // Glowstone and redstone do nothing because no stronger or longer version is registered, addMix is opened by Fabric API
        public static void addBrewingRecipes()
        {
            if (Database.WARPING_POTION_TEST)
                PotionBrewing.addMix(Potions.AWKWARD, Items.ENDER_PEARL, WARPING.get());
        }

        static void init()
        {
        }
    }

    // Nested classes only register when touched, blocks before items, effects before potions
    public static void init()
    {
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
