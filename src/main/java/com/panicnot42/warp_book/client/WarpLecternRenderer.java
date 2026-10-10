/**
 * @author DarkgreenWorld, Claude Opus 5.5
 * Created at: 2026.10.10
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.panicnot42.warp_book.Database;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.EnchantTableRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

// Same transforms as vanilla LecternRenderer, used for warp lecterns and in place of it for vanilla lecterns
public class WarpLecternRenderer implements BlockEntityRenderer<BlockEntity>
{
	private static final Material WARP_LECTERN_BOOK = new Material(TextureAtlas.LOCATION_BLOCKS, Database.rl("block/warp_lectern_book"));
	private static final Material WARP_BOOK_ON_LECTERN = new Material(TextureAtlas.LOCATION_BLOCKS, Database.rl("block/warp_book_on_lectern"));

	private final BookModel bookModel;

	public WarpLecternRenderer(BlockEntityRendererProvider.Context context)
	{
		bookModel = new BookModel(context.bakeLayer(ModelLayers.BOOK));
	}

	@Override
	public void render(@NotNull BlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay)
	{
		BlockState state = blockEntity.getBlockState();
		Material book = WARP_LECTERN_BOOK;
		// A vanilla lectern draws nothing without a book and the vanilla book unless it holds a warp book
		if (blockEntity instanceof LecternBlockEntity)
		{
			boolean hasBook = state.getValue(LecternBlock.HAS_BOOK);
			boolean warpBook = ClientHooks.isWarpBookOnLectern(blockEntity.getBlockPos(), hasBook);
			if (!hasBook)
				return;
			book = warpBook ? WARP_BOOK_ON_LECTERN : EnchantTableRenderer.BOOK_LOCATION;
		}

		poseStack.pushPose();
		poseStack.translate(0.5F, 1.0625F, 0.5F);
		poseStack.mulPose(Axis.YP.rotationDegrees(-state.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise().toYRot()));
		poseStack.mulPose(Axis.ZP.rotationDegrees(67.5F));
		poseStack.translate(0.0F, -0.125F, 0.0F);
		bookModel.setupAnim(0.0F, 0.1F, 0.9F, 1.2F);
		bookModel.render(poseStack, book.buffer(bufferSource, RenderType::entitySolid), packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
		poseStack.popPose();
	}
}
