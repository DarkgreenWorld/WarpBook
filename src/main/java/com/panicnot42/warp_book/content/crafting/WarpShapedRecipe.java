/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2016-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.crafting;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.WarpBook;
import com.panicnot42.warp_book.content.item.WarpBookItem;
import com.panicnot42.warp_book.registration.Registration;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

// minecraft:crafting_shaped plus the rules every Warp Book recipe shares, see the static helpers below
public class WarpShapedRecipe extends ShapedRecipe
{
	public final Optional<Boolean> warpingPotionTest;
	private @Nullable NonNullList<Ingredient> shownIngredients;

	// Wraps what the vanilla serializer read, see Registration.RecipeSerializerRegistry
	public WarpShapedRecipe(ShapedRecipe recipe, Optional<Boolean> warpingPotionTest)
	{
		super(recipe.getId(), recipe.getGroup(), recipe.category(), recipe.getWidth(), recipe.getHeight(), recipe.getIngredients(), recipe.getResultItem(null), recipe.showNotification());
		this.warpingPotionTest = warpingPotionTest;
	}

	@Override
	public boolean matches(@NotNull CraftingContainer container, @NotNull Level level)
	{
		return isActive(warpingPotionTest) && super.matches(container, level) && onlyWarpingPotions(container);
	}

	// A warp book ingredient carries its pages over to the warp lectern, which keeps them in the item's BlockEntityTag
	@Override
	public @NotNull ItemStack assemble(@NotNull CraftingContainer container, @NotNull RegistryAccess registries)
	{
		ItemStack result = super.assemble(container, registries);
		for (int i = 0; i < container.getContainerSize(); i++)
		{
			ItemStack book = container.getItem(i);
			if (book.getItem() instanceof WarpBookItem)
			{
				ItemStack pages = new ItemStack(Registration.ItemRegistry.WARP_BOOK.get());
				WarpBookItem.setWarpBookContent(pages, WarpBookItem.getContent(book));
				result.getOrCreateTagElement(BlockItem.BLOCK_ENTITY_TAG).put(Database.TAG_BOOK, pages.save(new CompoundTag()));
			}
		}
		return result;
	}

	// Built on first use, building it while recipes load would cache tag ingredients before tags are bound
	@Override
	public @NotNull NonNullList<Ingredient> getIngredients()
	{
		if (shownIngredients == null)
			shownIngredients = showWarpingPotions(super.getIngredients());
		return shownIngredients;
	}

	@Override
	public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingContainer container)
	{
		return remainingItems(super.getRemainingItems(container), container);
	}

	// Keeps a recipe the test switch turns off out of the recipe book
	@Override
	public boolean isSpecial()
	{
		return !isActive(warpingPotionTest);
	}

	@Override
	public @NotNull RecipeSerializer<?> getSerializer()
	{
		return Registration.RecipeSerializerRegistry.SHAPED.get();
	}

	// Warping potion test switch: a recipe marked true or false only works while the switch has that value
	static boolean isActive(Optional<Boolean> warpingPotionTest)
	{
		return warpingPotionTest.map(test -> test == WarpBook.WARPING_POTION_TEST).orElse(true);
	}

	// JSON cannot ask for a potion type, so wherever these recipes take a potion it has to be the warping one
	static boolean onlyWarpingPotions(CraftingContainer container)
	{
		for (int i = 0; i < container.getContainerSize(); i++)
		{
			ItemStack stack = container.getItem(i);
			if (stack.is(Items.POTION) && PotionUtils.getPotion(stack) != Registration.PotionRegistry.WARPING.get())
				return false;
		}
		return true;
	}

	// Shown in the recipe book and JEI instead of a bare uncraftable potion, matching only compares the item anyway
	static NonNullList<Ingredient> showWarpingPotions(NonNullList<Ingredient> ingredients)
	{
		ItemStack potion = PotionUtils.setPotion(new ItemStack(Items.POTION), Registration.PotionRegistry.WARPING.get());
		NonNullList<Ingredient> shown = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);
		for (int i = 0; i < ingredients.size(); i++)
			shown.set(i, ingredients.get(i).test(potion) ? Ingredient.of(potion) : ingredients.get(i));
		return shown;
	}

	// Potions leave their bottle like honey does, warp books hand back their deathly pages
	static NonNullList<ItemStack> remainingItems(NonNullList<ItemStack> remaining, CraftingContainer container)
	{
		for (int i = 0; i < container.getContainerSize(); i++)
		{
			ItemStack stack = container.getItem(i);
			int respawns = WarpBookItem.getRespawnsLeft(stack);
			if (stack.is(Items.POTION))
				remaining.set(i, new ItemStack(Items.GLASS_BOTTLE));
			else if (respawns > 0)
				remaining.set(i, new ItemStack(Registration.ItemRegistry.WARP_PAGE_ITEM_DEATHLY.get(), respawns));
		}
		return remaining;
	}
}
