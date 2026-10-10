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
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

// The client does not know what lies on a lectern, so the server sends the book and the page a warp lectern was left open at
public record S2COpenLecternPacket(BlockPos pos, ItemStack book, int page) implements IPacket
{
	public static final PacketType<S2COpenLecternPacket> TYPE = PacketType.create(Database.rl("packet_open_lectern"), S2COpenLecternPacket :: new);

	public S2COpenLecternPacket(FriendlyByteBuf buf)
	{
		this(buf.readBlockPos(), buf.readItem(), buf.readVarInt());
	}

	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeBlockPos(pos);
		buf.writeItem(book);
		buf.writeVarInt(page);
	}

	@Override
	public void handle(@NotNull Player player)
	{
		ClientHooks.openLecternGui(player, book, pos, page);
	}

	@Override
	public @NotNull PacketType<?> getType()
	{
		return TYPE;
	}
}
