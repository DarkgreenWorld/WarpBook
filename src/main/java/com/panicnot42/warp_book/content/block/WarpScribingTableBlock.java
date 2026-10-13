/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2016-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.block;

import com.panicnot42.warp_book.content.gui.inventory.MenuWarpScribingTable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WarpScribingTableBlock extends Block implements EntityBlock
{
	private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 12, 16);

	public WarpScribingTableBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context)
	{
		return SHAPE;
	}

	@Override
	public @NotNull BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state)
	{
		return new WarpScribingTableBlockEntity(pos, state);
	}

	@Override
	public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult)
	{
		if (player instanceof ServerPlayer serverPlayer && level.getBlockEntity(pos) instanceof WarpScribingTableBlockEntity table)
			NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider(
					(containerId, inventory, menuPlayer) -> new MenuWarpScribingTable(containerId, inventory, table.items, ContainerLevelAccess.create(level, pos)),
					table.getName()), pos);
		return InteractionResult.sidedSuccess(level.isClientSide());
	}

	@Override
	public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean movedByPiston)
	{
		if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof WarpScribingTableBlockEntity table)
			Containers.dropContents(level, pos, table.items);
		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	@Override
	public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack)
	{
		super.setPlacedBy(level, pos, state, placer, stack);
		if (stack.hasCustomHoverName() && level.getBlockEntity(pos) instanceof WarpScribingTableBlockEntity table)
			table.setCustomName(stack.getHoverName());
	}
}
