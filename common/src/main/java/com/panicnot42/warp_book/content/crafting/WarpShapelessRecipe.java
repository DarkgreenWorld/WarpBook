/**
 * @author DarkgreenWorld, Claude Opus 5.5
 * Created at: 2026.10.10
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.panicnot42.warp_book.registration.Registration;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
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
	public static final MapCodec<WarpShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.STRING.optionalFieldOf("group", "").forGetter(ShapelessRecipe::getGroup),
			CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapelessRecipe::category),
			ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.getResultItem(null)),
			Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(
					list -> list.isEmpty() || list.size() > 9
							? DataResult.<NonNullList<Ingredient>>error(() -> "A shapeless recipe needs 1 to 9 ingredients")
							: DataResult.success(NonNullList.of(Ingredient.EMPTY, list.toArray(Ingredient[]::new))),
					DataResult::success).forGetter(ShapelessRecipe::getIngredients),
			Codec.BOOL.optionalFieldOf("warping_potion_test").forGetter(recipe -> recipe.warpingPotionTest)
	).apply(instance, WarpShapelessRecipe::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, WarpShapelessRecipe> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, ShapelessRecipe::getGroup,
			CraftingBookCategory.STREAM_CODEC, ShapelessRecipe::category,
			ItemStack.STREAM_CODEC, recipe -> recipe.getResultItem(null),
			Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.<RegistryFriendlyByteBuf, Ingredient, NonNullList<Ingredient>>collection(NonNullList::createWithCapacity)), ShapelessRecipe::getIngredients,
			ByteBufCodecs.optional(ByteBufCodecs.BOOL), recipe -> recipe.warpingPotionTest,
			WarpShapelessRecipe::new);

	private final Optional<Boolean> warpingPotionTest;
	private NonNullList<Ingredient> shownIngredients;

	public WarpShapelessRecipe(String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients, Optional<Boolean> warpingPotionTest)
	{
		super(group, category, result, ingredients);
		this.warpingPotionTest = warpingPotionTest;
	}

	@Override
	public boolean matches(@NotNull CraftingInput input, @NotNull Level level)
	{
		return WarpShapedRecipe.isActive(warpingPotionTest) && super.matches(input, level) && WarpShapedRecipe.onlyWarpingPotions(input);
	}

	@Override
	public @NotNull NonNullList<Ingredient> getIngredients()
	{
		if (shownIngredients == null)
			shownIngredients = WarpShapedRecipe.showWarpingPotion(super.getIngredients());
		return shownIngredients;
	}

	@Override
	public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingInput input)
	{
		return WarpShapedRecipe.remainingItems(super.getRemainingItems(input), input);
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
