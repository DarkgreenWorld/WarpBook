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
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// Takes a warp book off a vanilla lectern, mirroring the take button of the vanilla lectern screen
public record C2STakeBookPacket(BlockPos pos) {

    public static void encode(C2STakeBookPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
    }

    public static C2STakeBookPacket decode(FriendlyByteBuf buf) {
        return new C2STakeBookPacket(buf.readBlockPos());
    }

    public static void handle(C2STakeBookPacket msg, Supplier<NetworkEvent.Context> ctxGetter) {
        NetworkEvent.Context ctx = ctxGetter.get();

        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            Level level = player.level();
            if (!player.mayBuild() || !msg.pos.closerToCenterThan(player.position(), 8.0) || !(level.getBlockEntity(msg.pos) instanceof LecternBlockEntity lectern) || !(lectern.getBook().getItem() instanceof WarpBookItem))
                return;

            ItemStack book = lectern.getBook();
            lectern.setBook(ItemStack.EMPTY);
            LecternBlock.resetBookState(player, level, msg.pos, level.getBlockState(msg.pos), false);
            if (!player.getInventory().add(book))
                player.drop(book, false);
        });

        ctx.setPacketHandled(true);
    }
}
