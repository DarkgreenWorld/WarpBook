/**
 * @author DarkgreenWorld, Claude Opus 5.5
 * Created at: 2026.10.10
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.network.packet;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.item.WarpBookItem;
import com.panicnot42.warp_book.content.network.NetworkEngine;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.jetbrains.annotations.NotNull;

// The client asks whether a vanilla lectern holds a warp book, vanilla never sends the book itself
public record C2SLecternBookPacket(BlockPos pos) implements IPacket
{
	public static final PacketType<C2SLecternBookPacket> TYPE = PacketType.create(Database.rl("packet_lectern_book_query"), C2SLecternBookPacket :: new);

	public C2SLecternBookPacket(FriendlyByteBuf buf)
	{
		this(buf.readBlockPos());
	}

	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeBlockPos(pos);
	}

	@Override
	public void handle(@NotNull Player player)
	{
		// Only lecterns close enough to be drawn, and never one that would load a chunk
		if (!(player instanceof ServerPlayer serverPlayer) || !player.level().isLoaded(pos) || !pos.closerToCenterThan(player.getEyePosition(), 68.0))
			return;
		boolean warpBook = player.level().getBlockEntity(pos) instanceof LecternBlockEntity lectern && lectern.getBook().getItem() instanceof WarpBookItem;
		NetworkEngine.sendToPlayer(serverPlayer, new S2CLecternBookPacket(pos, warpBook));
	}

	@Override
	public @NotNull PacketType<?> getType()
	{
		return TYPE;
	}
}
