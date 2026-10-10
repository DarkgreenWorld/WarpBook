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
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.codec.ByteBufCodecs;
import org.jetbrains.annotations.NotNull;

// The client does not know what lies on a lectern, so the server sends the book and the page a warp lectern was left open at
public record S2COpenLecternPacket(BlockPos pos, ItemStack book, int page) implements IPacket
{
	public static final CustomPacketPayload.Type<S2COpenLecternPacket> TYPE = new CustomPacketPayload.Type<>(Database.rl("packet_open_lectern"));
	public static final StreamCodec<RegistryFriendlyByteBuf, S2COpenLecternPacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC,
			S2COpenLecternPacket :: pos,
			ItemStack.STREAM_CODEC,
			S2COpenLecternPacket :: book,
			ByteBufCodecs.VAR_INT,
			S2COpenLecternPacket :: page,
			S2COpenLecternPacket :: new);

	@Override
	public void handle(@NotNull Player player)
	{
		ClientHooks.openLecternGui(player, book, pos, page);
	}

	@Override
	public @NotNull Type<S2COpenLecternPacket> type()
	{
		return TYPE;
	}
}
