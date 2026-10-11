/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.network.packet;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.block.WarpLecternBlockEntity;
import com.panicnot42.warp_book.content.core.IDeclareWarp;
import com.panicnot42.warp_book.content.item.WarpBookItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class C2SWarpPacket
{
    private final UUID uuid;
    private final int index;
    private final boolean isMainHand;
    // Set when the warp list was opened from a lectern instead of a held book
    private final Optional<BlockPos> lectern;

    public C2SWarpPacket(UUID uuid, int index, boolean isMainHand, Optional<BlockPos> lectern)
    {
        this.uuid = uuid;
        this.index = index;
        this.isMainHand = isMainHand;
        this.lectern = lectern;
    }

    public static void encode(C2SWarpPacket msg, FriendlyByteBuf buf)
    {
        buf.writeUUID(msg.uuid);
        buf.writeInt(msg.index);
        buf.writeBoolean(msg.isMainHand);
        buf.writeOptional(msg.lectern, FriendlyByteBuf::writeBlockPos);
    }

    public static C2SWarpPacket decode(FriendlyByteBuf buf)
    {
        return new C2SWarpPacket(buf.readUUID(), buf.readInt(), buf.readBoolean(), buf.readOptional(FriendlyByteBuf::readBlockPos));
    }

    public static void handle(C2SWarpPacket msg, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            Player target = player.getServer().getPlayerList().getPlayer(msg.uuid);
            if (target == null) return;

            InteractionHand usedHand = msg.isMainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            ItemStack stack = msg.lectern.map(pos -> pos.closerToCenterThan(target.position(), 8.0) ? WarpLecternBlockEntity.findBook(target.level(), pos) : ItemStack.EMPTY).
                    orElseGet(() -> target.getItemInHand(usedHand));

            if (stack.getItem() instanceof WarpBookItem)
            {
                var contents = WarpBookItem.getContent(stack);

                if (msg.index >= 0 && msg.index < contents.size())
                {
                    ItemStack page = contents.get(msg.index);

                    if (page.getItem() instanceof IDeclareWarp warp)
                    {
                        GlobalPos pos = warp.getWaypoint(target, page);
                        if (pos != null)
                        {
                            Database.warpDrive.processWarp(target, pos);
                        }
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
