/**
 * @author ArcAnc, MelonVRneu, DarkgreenWorld, Panicnot42, FerreusVeritas
 * Created at: 07.08.2024
 * Copyright (c) 2024-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.network.packet;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.client.ClientHooks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public record S2CPacketEffect(boolean enter, int x, int y, int z) implements IPacket
{

	public static final CustomPacketPayload.Type<S2CPacketEffect> TYPE = new CustomPacketPayload.Type<>(Database.rl("packet_effect"));
	public static final StreamCodec<FriendlyByteBuf, S2CPacketEffect> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL,
			S2CPacketEffect :: enter,
			ByteBufCodecs.INT,
			S2CPacketEffect :: x,
			ByteBufCodecs.INT,
			S2CPacketEffect :: y,
			ByteBufCodecs.INT,
			S2CPacketEffect :: z,
			S2CPacketEffect :: new);

	@Override
	public void handle(@NotNull Player player)
	{
		ClientHooks.playWarpEffect(player, enter, x, y, z);
	}

	@Override
	public @NotNull Type<S2CPacketEffect> type()
	{
		return TYPE;
	}
}
