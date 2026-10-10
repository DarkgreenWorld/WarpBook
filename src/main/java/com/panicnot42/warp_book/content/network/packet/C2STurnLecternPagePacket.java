/**
 * @author DarkgreenWorld, Claude Opus 5.5
 * Created at: 2026.10.10
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.network.packet;

import com.panicnot42.warp_book.content.block.WarpLecternBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// Sent when a player turns the page of a warp lectern's warp list
public record C2STurnLecternPagePacket(BlockPos pos, int page) {

    public static void encode(C2STurnLecternPagePacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeVarInt(msg.page);
    }

    public static C2STurnLecternPagePacket decode(FriendlyByteBuf buf) {
        return new C2STurnLecternPagePacket(buf.readBlockPos(), buf.readVarInt());
    }

    public static void handle(C2STurnLecternPagePacket msg, Supplier<NetworkEvent.Context> ctxGetter) {
        NetworkEvent.Context ctx = ctxGetter.get();

        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null && msg.pos.closerToCenterThan(player.position(), 8.0) && player.level().getBlockEntity(msg.pos) instanceof WarpLecternBlockEntity lectern)
                lectern.setPage(msg.page);
        });

        ctx.setPacketHandled(true);
    }
}
