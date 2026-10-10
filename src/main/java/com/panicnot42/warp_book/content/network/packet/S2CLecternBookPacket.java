/**
 * @author DarkgreenWorld, Claude Opus 5.5
 * Created at: 2026.10.10
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.network.packet;

import com.panicnot42.warp_book.client.ClientHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// The answer to C2SLecternBookPacket
public record S2CLecternBookPacket(BlockPos pos, boolean warpBook) {

    public static void encode(S2CLecternBookPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeBoolean(msg.warpBook);
    }

    public static S2CLecternBookPacket decode(FriendlyByteBuf buf) {
        return new S2CLecternBookPacket(buf.readBlockPos(), buf.readBoolean());
    }

    public static void handle(S2CLecternBookPacket msg, Supplier<NetworkEvent.Context> ctxGetter) {
        NetworkEvent.Context ctx = ctxGetter.get();

        ctx.enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHooks.setLecternBook(msg.pos, msg.warpBook))
        );

        ctx.setPacketHandled(true);
    }
}
