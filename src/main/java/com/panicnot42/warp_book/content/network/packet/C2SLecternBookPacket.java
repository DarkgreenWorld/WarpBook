/**
 * @author DarkgreenWorld, Claude Opus 5.5
 * Created at: 2026.10.10
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.network.packet;

import com.panicnot42.warp_book.content.item.WarpBookItem;
import com.panicnot42.warp_book.content.network.NetworkEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// The client asks whether a vanilla lectern holds a warp book, vanilla never sends the book itself
public record C2SLecternBookPacket(BlockPos pos) {

    public static void encode(C2SLecternBookPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
    }

    public static C2SLecternBookPacket decode(FriendlyByteBuf buf) {
        return new C2SLecternBookPacket(buf.readBlockPos());
    }

    public static void handle(C2SLecternBookPacket msg, Supplier<NetworkEvent.Context> ctxGetter) {
        NetworkEvent.Context ctx = ctxGetter.get();

        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            // Only lecterns close enough to be drawn, and never one that would load a chunk
            if (player == null || !player.level().isLoaded(msg.pos) || !msg.pos.closerToCenterThan(player.getEyePosition(), 68.0))
                return;

            boolean warpBook = player.level().getBlockEntity(msg.pos) instanceof LecternBlockEntity lectern && lectern.getBook().getItem() instanceof WarpBookItem;
            NetworkEngine.sendToPlayer(new S2CLecternBookPacket(msg.pos, warpBook), player);
        });

        ctx.setPacketHandled(true);
    }
}
