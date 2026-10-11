/**
 * @author ArcAnc, MelonVRneu, DarkgreenWorld, Panicnot42, FerreusVeritas
 * Created at: 07.08.2024
 * Copyright (c) 2024-2026
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
import com.panicnot42.warp_book.content.crafting.WarpPageCloningRecipe;
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
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Registration {

    public static final class BlockRegistry {
        public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Database.MOD_ID);

        public static final RegistryObject<WarpLecternBlock> WARP_LECTERN = BLOCKS.register(Database.BLOCK_NAME_WARP_LECTERN,
                () -> new WarpLecternBlock(BlockBehaviour.Properties.copy(Blocks.LECTERN)));

        public static final RegistryObject<WarpScribingTableBlock> WARP_SCRIBING_TABLE = BLOCKS.register(Database.BLOCK_NAME_WARP_SCRIBING_TABLE,
                () -> new WarpScribingTableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(10.0F, 20.0F).requiresCorrectToolForDrops().sound(SoundType.STONE).noOcclusion()));

        public static void init(final IEventBus modEventBus) {
            BLOCKS.register(modEventBus);
        }
    }

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

        public static final RegistryObject<Item> WARP_QUILL = ITEMS.register(Database.ITEM_NAME_WARP_QUILL,
                () -> new Item(new Item.Properties()));

        public static final RegistryObject<BlockItem> WARP_LECTERN = ITEMS.register(Database.BLOCK_NAME_WARP_LECTERN,
                () -> new BlockItem(BlockRegistry.WARP_LECTERN.get(), new Item.Properties()));

        public static final RegistryObject<BlockItem> WARP_SCRIBING_TABLE = ITEMS.register(Database.BLOCK_NAME_WARP_SCRIBING_TABLE,
                () -> new BlockItem(BlockRegistry.WARP_SCRIBING_TABLE.get(), new Item.Properties()));

        public interface IMustBeAddedToCreative {
            boolean addToCreative();
        }

        public static void init(final IEventBus modEventBus) {
            ITEMS.register(modEventBus);
        }
    }

    public static final class BlockEntityRegistry {
        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Database.MOD_ID);

        public static final RegistryObject<BlockEntityType<WarpLecternBlockEntity>> WARP_LECTERN = BLOCK_ENTITY_TYPES.register(Database.BLOCK_NAME_WARP_LECTERN,
                () -> BlockEntityType.Builder.of(WarpLecternBlockEntity::new, BlockRegistry.WARP_LECTERN.get()).build(null));

        public static final RegistryObject<BlockEntityType<WarpScribingTableBlockEntity>> WARP_SCRIBING_TABLE = BLOCK_ENTITY_TYPES.register(Database.BLOCK_NAME_WARP_SCRIBING_TABLE,
                () -> BlockEntityType.Builder.of(WarpScribingTableBlockEntity::new, BlockRegistry.WARP_SCRIBING_TABLE.get()).build(null));

        public static void init(final IEventBus modEventBus) {
            BLOCK_ENTITY_TYPES.register(modEventBus);
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
                                .filter(item -> !(item instanceof ItemRegistry.IMustBeAddedToCreative toCreative) || toCreative.addToCreative())
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
        // The server writes the book and whether it lies on a warp lectern
        public static final RegistryObject<MenuType<MenuWarpBook>> WARP_BOOK = MenuTypeRegistry.MENU_TYPES.register
        		(
        				Database.CONTAINER_WARP_BOOK, () -> IForgeMenuType.create
        				(
        						(windowId, inv, data) -> 
        						{
        							ItemStack heldItem = data.readItem(); 
        							return new MenuWarpBook(windowId, inv, 
        									new ContainerWarpBook(heldItem), 
        									new ContainerWarpBookSpecial(heldItem),
        									null, data.readBoolean());
        						}
        				)
        		);

        public static final RegistryObject<MenuType<MenuWarpScribingTable>> WARP_SCRIBING_TABLE = MENU_TYPES.register(Database.BLOCK_NAME_WARP_SCRIBING_TABLE,
                () -> IForgeMenuType.create((windowId, inv, data) -> new MenuWarpScribingTable(windowId, inv, new SimpleContainer(3), ContainerLevelAccess.NULL)));

        public static void init(@NotNull final IEventBus modEventBus) {
            MENU_TYPES.register(modEventBus);
        }
    }

    public static final class RecipeSerializerRegistry {
        public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Database.MOD_ID);

        public static final RegistryObject<RecipeSerializer<WarpShapedRecipe>> SHAPED = RECIPE_SERIALIZERS.register(Database.RECIPE_NAME_SHAPED,
                () -> serializer(RecipeSerializer.SHAPED_RECIPE, WarpShapedRecipe::new, recipe -> recipe.warpingPotionTest));

        public static final RegistryObject<RecipeSerializer<WarpShapelessRecipe>> SHAPELESS = RECIPE_SERIALIZERS.register(Database.RECIPE_NAME_SHAPELESS,
                () -> serializer(RecipeSerializer.SHAPELESS_RECIPE, WarpShapelessRecipe::new, recipe -> recipe.warpingPotionTest));

        public static final RegistryObject<RecipeSerializer<WarpPageCloningRecipe>> PAGE_CLONING = RECIPE_SERIALIZERS.register(Database.RECIPE_NAME_PAGE_CLONING,
                () -> new SimpleCraftingRecipeSerializer<>(WarpPageCloningRecipe::new));

        // The vanilla serializer reads the recipe, this one adds the optional warping_potion_test field
        private static <V extends Recipe<?>, T extends V> RecipeSerializer<T> serializer(RecipeSerializer<V> vanilla, BiFunction<V, Optional<Boolean>, T> factory, Function<T, Optional<Boolean>> warpingPotionTest) {
            return new RecipeSerializer<>() {
                @Override
                public @NotNull T fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
                    return factory.apply(vanilla.fromJson(id, json), json.has("warping_potion_test") ? Optional.of(GsonHelper.getAsBoolean(json, "warping_potion_test")) : Optional.empty());
                }

                @Override
                public T fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
                    return factory.apply(vanilla.fromNetwork(id, buf), buf.readOptional(FriendlyByteBuf::readBoolean));
                }

                @Override
                public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull T recipe) {
                    vanilla.toNetwork(buf, recipe);
                    buf.writeOptional(warpingPotionTest.apply(recipe), FriendlyByteBuf::writeBoolean);
                }
            };
        }

        public static void init(final IEventBus modEventBus) {
            RECIPE_SERIALIZERS.register(modEventBus);
        }
    }

    public static final class MobEffectRegistry {
        public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Database.MOD_ID);

        public static final RegistryObject<WarpingMobEffect> WARPING = MOB_EFFECTS.register(Database.EFFECT_NAME_WARPING, WarpingMobEffect::new);

        public static void init(final IEventBus modEventBus) {
            MOB_EFFECTS.register(modEventBus);
        }
    }

    public static final class PotionRegistry {
        public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, Database.MOD_ID);

        public static final RegistryObject<Potion> WARPING = POTIONS.register(Database.POTION_NAME_WARPING,
                () -> new Potion(Database.MOD_ID + "." + Database.POTION_NAME_WARPING, new MobEffectInstance(MobEffectRegistry.WARPING.get(), 1800)));

        // Glowstone and redstone do nothing because no stronger or longer version is registered
        public static void addBrewingRecipes() {
            if (Database.WARPING_POTION_TEST)
                BrewingRecipeRegistry.addRecipe(StrictNBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)),
                        Ingredient.of(Items.ENDER_PEARL),
                        PotionUtils.setPotion(new ItemStack(Items.POTION), WARPING.get()));
        }

        public static void init(final IEventBus modEventBus) {
            POTIONS.register(modEventBus);
        }
    }

    public static void init(final IEventBus modEventBus) {
        BlockRegistry.init(modEventBus);
        ItemRegistry.init(modEventBus);
        BlockEntityRegistry.init(modEventBus);
        CreativeTabRegistry.init(modEventBus);
        SoundRegistry.init(modEventBus);
        MenuTypeRegistry.init(modEventBus);
        RecipeSerializerRegistry.init(modEventBus);
        MobEffectRegistry.init(modEventBus);
        PotionRegistry.init(modEventBus);
    }
}
