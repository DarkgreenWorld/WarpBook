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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

// The answer to C2SLecternBookPacket
public record S2CLecternBookPacket(BlockPos pos, boolean warpBook) implements IPacket
{
    public static final CustomPacketPayload.Type<S2CLecternBookPacket> TYPE = new CustomPacketPayload.Type<>(Database.rl("packet_lectern_book"));
    public static final StreamCodec<FriendlyByteBuf, S2CLecternBookPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            S2CLecternBookPacket :: pos,
            ByteBufCodecs.BOOL,
            S2CLecternBookPacket :: warpBook,
            S2CLecternBookPacket :: new);

    @Override
    public void handle(@NotNull Player player)
    {
        ClientHooks.setLecternBook(pos, warpBook);
    }

    @Override
    public @NotNull Type<S2CLecternBookPacket> type()
    {
        return TYPE;
    }
}