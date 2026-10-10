/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2016-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.crafting;

import com.panicnot42.warp_book.registration.Registration;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

// minecraft:crafting_shapeless with the same shared rules as WarpShapedRecipe
public class WarpShapelessRecipe extends ShapelessRecipe
{
	public final Optional<Boolean> warpingPotionTest;
	private @Nullable NonNullList<Ingredient> shownIngredients;

	public WarpShapelessRecipe(ShapelessRecipe recipe, Optional<Boolean> warpingPotionTest)
	{
		super(recipe.getId(), recipe.getGroup(), recipe.category(), recipe.getResultItem(null), recipe.getIngredients());
		this.warpingPotionTest = warpingPotionTest;
	}

	@Override
	public boolean matches(@NotNull CraftingContainer container, @NotNull Level level)
	{
		return WarpShapedRecipe.isActive(warpingPotionTest) && super.matches(container, level) && WarpShapedRecipe.onlyWarpingPotions(container);
	}

	// Built on first use, see WarpShapedRecipe.getIngredients
	@Override
	public @NotNull NonNullList<Ingredient> getIngredients()
	{
		if (shownIngredients == null)
			shownIngredients = WarpShapedRecipe.showWarpingPotions(super.getIngredients());
		return shownIngredients;
	}

	@Override
	public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingContainer container)
	{
		return WarpShapedRecipe.remainingItems(super.getRemainingItems(container), container);
	}

	@Override
	public boolean isSpecial()
	{
		return !WarpShapedRecipe.isActive(warpingPotionTest);
	}

	@Override
	public @NotNull RecipeSerializer<?> getSerializer()
	{
		return Registration.RecipeSerializerRegistry.SHAPELESS.get();
	}
}
