/**
 * @author ArcAnc, MelonVRneu, Darkgreen_World, Panicnot42, FerreusVeritas
 * Created at: 07.08.2024
 * Copyright (c) 2024-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.network.packet;

import com.panicnot42.warp_book.WarpBook;
import com.panicnot42.warp_book.content.core.IDeclareWarp;
import com.panicnot42.warp_book.content.item.WarpBookItem;

import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class C2SWarpPacket
{
    private final UUID uuid;
    private final int index;

    public C2SWarpPacket(UUID uuid, int index)
    {
        this.uuid = uuid;
        this.index = index;
    }

    public static void encode(C2SWarpPacket msg, FriendlyByteBuf buf)
    {
        buf.writeUUID(msg.uuid);
        buf.writeInt(msg.index);
    }

    public static C2SWarpPacket decode(FriendlyByteBuf buf)
    {
        return new C2SWarpPacket(buf.readUUID(), buf.readInt());
    }

    public static void handle(C2SWarpPacket msg, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            Player target = player.getServer().getPlayerList().getPlayer(msg.uuid);
            if (target == null) return;

            ItemStack stack = target.getMainHandItem();

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
                            WarpBook.warpDrive.processWarp(target, pos);
                        }
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}