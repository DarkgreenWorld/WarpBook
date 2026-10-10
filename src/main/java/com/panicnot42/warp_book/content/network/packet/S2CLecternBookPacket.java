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
import com.panicnot42.warp_book.client.ClientHooks;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

// The answer to C2SLecternBookPacket
public record S2CLecternBookPacket(BlockPos pos, boolean warpBook) implements IPacket
{
	public static final PacketType<S2CLecternBookPacket> TYPE = PacketType.create(Database.rl("packet_lectern_book"), S2CLecternBookPacket :: new);

	public S2CLecternBookPacket(FriendlyByteBuf buf)
	{
		this(buf.readBlockPos(), buf.readBoolean());
	}

	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeBlockPos(pos);
		buf.writeBoolean(warpBook);
	}

	@Override
	public void handle(@NotNull Player player)
	{
		ClientHooks.setLecternBook(pos, warpBook);
	}

	@Override
	public @NotNull PacketType<?> getType()
	{
		return TYPE;
	}
}
