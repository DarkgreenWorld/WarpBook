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
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// The client does not know what lies on a lectern, so the server sends the book and the page a warp lectern was left open at
public record S2COpenLecternPacket(BlockPos pos, ItemStack book, int page) {

    public static void encode(S2COpenLecternPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeItem(msg.book);
        buf.writeVarInt(msg.page);
    }

    public static S2COpenLecternPacket decode(FriendlyByteBuf buf) {
        return new S2COpenLecternPacket(buf.readBlockPos(), buf.readItem(), buf.readVarInt());
    }

    public static void handle(S2COpenLecternPacket msg, Supplier<NetworkEvent.Context> ctxGetter) {
        NetworkEvent.Context ctx = ctxGetter.get();

        ctx.enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHooks.openLecternGui(msg.book, msg.pos, msg.page))
        );

        ctx.setPacketHandled(true);
    }
}
