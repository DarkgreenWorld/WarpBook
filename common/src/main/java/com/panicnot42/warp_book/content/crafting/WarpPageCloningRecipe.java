/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2016-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
/*
package com.panicnot42.warp_book.content.crafting;

import com.panicnot42.warp_book.content.item.UnboundWarpPageItem;
import com.panicnot42.warp_book.content.item.WarpItem;
import com.panicnot42.warp_book.registration.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

// One bound page plus any number of unbound pages, the bound page stays in the grid like vanilla book cloning
public class WarpPageCloningRecipe extends CustomRecipe
{
	public WarpPageCloningRecipe(CraftingBookCategory category)
	{
		super(category);
	}

	@Override
	public boolean matches(@NotNull CraftingInput input, @NotNull Level level)
	{
		return !assemble(input, level.registryAccess()).isEmpty();
	}

	@Override
	public @NotNull ItemStack assemble(@NotNull CraftingInput input, HolderLookup.@NotNull Provider registries)
	{
		ItemStack source = ItemStack.EMPTY;
		int copies = 0;
		for (int i = 0; i < input.size(); i++)
		{
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty())
				continue;
			if (stack.getItem() instanceof UnboundWarpPageItem)
				copies++;
			else if (source.isEmpty() && WarpItem.isCloneable(stack))
				source = stack;
			else
				return ItemStack.EMPTY;
		}
		return source.isEmpty() || copies == 0 ? ItemStack.EMPTY : source.copyWithCount(copies);
	}

	@Override
	public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingInput input)
	{
		NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);
		for (int i = 0; i < input.size(); i++)
			if (WarpItem.isCloneable(input.getItem(i)))
				remaining.set(i, input.getItem(i).copyWithCount(1));
		return remaining;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height)
	{
		return width * height >= 2;
	}

	@Override
	public @NotNull RecipeSerializer<?> getSerializer()
	{
		return Registration.RecipeSerializerRegistry.PAGE_CLONING.get();
	}
}
*/