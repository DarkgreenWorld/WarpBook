/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2016-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.block;

import com.panicnot42.warp_book.registration.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class WarpScribingTableBlockEntity extends BlockEntity
{
	// Template, unbound pages and the empty cover
	public final SimpleContainer items = new SimpleContainer(3);

	public WarpScribingTableBlockEntity(BlockPos pos, BlockState state)
	{
		super(Registration.BlockEntityRegistry.WARP_SCRIBING_TABLE.get(), pos, state);
		items.addListener(container -> setChanged());
	}

	@Override
	public void load(@NotNull CompoundTag tag)
	{
		super.load(tag);
		ContainerHelper.loadAllItems(tag, items.items);
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		super.saveAdditional(tag);
		ContainerHelper.saveAllItems(tag, items.items);
	}
}
