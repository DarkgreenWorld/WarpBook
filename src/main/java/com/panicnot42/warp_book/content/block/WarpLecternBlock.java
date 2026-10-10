/**
 * @author DarkgreenWorld, Claude Opus 5.5
 * Created at: 2026.10.10
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.block;

import com.panicnot42.warp_book.Database;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// Shapes, redstone and comparator copy the vanilla lectern, without being one for villagers or other mods
public class WarpLecternBlock extends HorizontalDirectionalBlock implements EntityBlock
{
	public WarpLecternBlock(Properties properties)
	{
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(BlockStateProperties.POWERED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder)
	{
		builder.add(FACING, BlockStateProperties.POWERED);
	}

	@Override
	public BlockState getStateForPlacement(@NotNull BlockPlaceContext context)
	{
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context)
	{
		return switch (state.getValue(FACING))
		{
			case NORTH -> LecternBlock.SHAPE_NORTH;
			case SOUTH -> LecternBlock.SHAPE_SOUTH;
			case EAST -> LecternBlock.SHAPE_EAST;
			case WEST -> LecternBlock.SHAPE_WEST;
			default -> LecternBlock.SHAPE_COMMON;
		};
	}

	@Override
	public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context)
	{
		return LecternBlock.SHAPE_COLLISION;
	}

	@Override
	public @NotNull VoxelShape getOcclusionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos)
	{
		return LecternBlock.SHAPE_COMMON;
	}

	@Override
	public boolean useShapeForLightOcclusion(@NotNull BlockState state)
	{
		return true;
	}

	@Override
	public boolean isPathfindable(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull PathComputationType type)
	{
		return false;
	}

	@Override
	public boolean isSignalSource(@NotNull BlockState state)
	{
		return true;
	}

	@Override
	public int getSignal(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull Direction direction)
	{
		return state.getValue(BlockStateProperties.POWERED) ? 15 : 0;
	}

	// Strong power only reaches the block below
	@Override
	public int getDirectSignal(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull Direction direction)
	{
		return direction == Direction.UP && state.getValue(BlockStateProperties.POWERED) ? 15 : 0;
	}

	// Ends the pulse that LecternBlock.signalPageChange started when a page was turned
	@Override
	public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random)
	{
		level.setBlock(pos, state.setValue(BlockStateProperties.POWERED, false), Block.UPDATE_ALL);
		level.updateNeighborsAt(pos.below(), this);
	}

	@Override
	public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean movedByPiston)
	{
		super.onRemove(state, level, pos, newState, movedByPiston);
		if (!state.is(newState.getBlock()) && state.getValue(BlockStateProperties.POWERED))
			level.updateNeighborsAt(pos.below(), this);
	}

	@Override
	public boolean hasAnalogOutputSignal(@NotNull BlockState state)
	{
		return true;
	}

	@Override
	public int getAnalogOutputSignal(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos)
	{
		return level.getBlockEntity(pos) instanceof WarpLecternBlockEntity lectern ? lectern.getRedstoneSignal() : 0;
	}

	// Same odds as the vanilla lectern, vanilla keeps them per block so a copy of the properties is not enough
	@Override
	public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction)
	{
		return 20;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction)
	{
		return 30;
	}

	@Override
	public @NotNull BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state)
	{
		return new WarpLecternBlockEntity(pos, state);
	}

	// The pages were already loaded from the item, pages that came from crafting have no owner yet and go to whoever places the lectern
	@Override
	public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack)
	{
		super.setPlacedBy(level, pos, state, placer, stack);
		if (!(level.getBlockEntity(pos) instanceof WarpLecternBlockEntity lectern))
			return;
		if (stack.hasCustomHoverName())
			lectern.setCustomName(stack.getHoverName());
		CompoundTag data = BlockItem.getBlockEntityData(stack);
		if (placer instanceof Player player && (data == null || !data.contains(Database.TAG_OWNERS)))
			lectern.updateOwners(player, NonNullList.withSize(54, ItemStack.EMPTY));
	}
}
