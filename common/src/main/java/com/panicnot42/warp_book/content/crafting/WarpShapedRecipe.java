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
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.item.WarpBookItem;
import com.panicnot42.warp_book.registration.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

// minecraft:crafting_shaped plus the rules every Warp Book recipe shares, see the static helpers below
public class WarpShapedRecipe extends ShapedRecipe
{
	// The pattern is kept here because ShapedRecipe hides its own
	public static final MapCodec<WarpShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipe::getGroup),
			CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapedRecipe::category),
			ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
			ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.getResultItem(null)),
			Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(ShapedRecipe::showNotification),
			Codec.BOOL.optionalFieldOf("warping_potion_test").forGetter(recipe -> recipe.warpingPotionTest)
	).apply(instance, WarpShapedRecipe::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, WarpShapedRecipe> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, ShapedRecipe::getGroup,
			CraftingBookCategory.STREAM_CODEC, ShapedRecipe::category,
			ShapedRecipePattern.STREAM_CODEC, recipe -> recipe.pattern,
			ItemStack.STREAM_CODEC, recipe -> recipe.getResultItem(null),
			ByteBufCodecs.BOOL, ShapedRecipe::showNotification,
			ByteBufCodecs.optional(ByteBufCodecs.BOOL), recipe -> recipe.warpingPotionTest,
			WarpShapedRecipe::new);

	private final ShapedRecipePattern pattern;
	private final Optional<Boolean> warpingPotionTest;
	private NonNullList<Ingredient> shownIngredients;

	public WarpShapedRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification, Optional<Boolean> warpingPotionTest)
	{
		super(group, category, pattern, result, showNotification);
		this.pattern = pattern;
		this.warpingPotionTest = warpingPotionTest;
	}

	@Override
	public boolean matches(@NotNull CraftingInput input, @NotNull Level level)
	{
		return isActive(warpingPotionTest) && super.matches(input, level) && onlyWarpingPotions(input);
	}

	// A warp book ingredient carries its pages over to the result
	@Override
	public @NotNull ItemStack assemble(@NotNull CraftingInput input, HolderLookup.@NotNull Provider registries)
	{
		ItemStack result = super.assemble(input, registries);
		for (int i = 0; i < input.size(); i++)
		{
			ItemStack book = input.getItem(i);
			if (book.getItem() instanceof WarpBookItem)
				result.set(DataComponents.CONTAINER, WarpBookItem.getContent(book));
		}
		return result;
	}

	@Override
	public @NotNull NonNullList<Ingredient> getIngredients()
	{
		if (shownIngredients == null)
			shownIngredients = showWarpingPotion(super.getIngredients());
		return shownIngredients;
	}

	@Override
	public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingInput input)
	{
		return remainingItems(super.getRemainingItems(input), input);
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
		return warpingPotionTest.map(test -> test == Database.WARPING_POTION_TEST).orElse(true);
	}

	// JSON cannot ask for a potion type, so wherever these recipes take a potion it has to be the warping one
	static boolean onlyWarpingPotions(CraftingInput input)
	{
		for (int i = 0; i < input.size(); i++)
		{
			ItemStack stack = input.getItem(i);
			if (stack.is(Items.POTION) && !stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).is(BuiltInRegistries.POTION.wrapAsHolder(Registration.PotionRegistry.WARPING.get())))
				return false;
		}
		return true;
	}

	// A plain potion ingredient shows up in the recipe book as an uncraftable potion, so show the warping one
	static NonNullList<Ingredient> showWarpingPotion(NonNullList<Ingredient> ingredients)
	{
		ItemStack potion = new ItemStack(Items.POTION);
		Ingredient warping = Ingredient.of(PotionContents.createItemStack(Items.POTION, BuiltInRegistries.POTION.wrapAsHolder(Registration.PotionRegistry.WARPING.get())));
		NonNullList<Ingredient> shown = NonNullList.createWithCapacity(ingredients.size());
		for (Ingredient ingredient : ingredients)
			shown.add(ingredient.test(potion) ? warping : ingredient);
		return shown;
	}

	// Potions leave their bottle like honey does, warp books hand back their deathly pages
	static NonNullList<ItemStack> remainingItems(NonNullList<ItemStack> remaining, CraftingInput input)
	{
		for (int i = 0; i < input.size(); i++)
		{
			ItemStack stack = input.getItem(i);
			int respawns = WarpBookItem.getRespawnsLeft(stack);
			if (stack.is(Items.POTION))
				remaining.set(i, new ItemStack(Items.GLASS_BOTTLE));
			else if (respawns > 0)
				remaining.set(i, new ItemStack(Registration.ItemRegistry.WARP_PAGE_ITEM_DEATHLY.get(), respawns));
		}
		return remaining;
	}
}
