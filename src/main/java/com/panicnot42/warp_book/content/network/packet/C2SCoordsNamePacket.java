/**
 * @author Panicnot42, ArcAnc, Darkgreen_World
 * Copyright (c) 2016-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.network.packet;

import com.panicnot42.warp_book.registration.Registration;
import com.panicnot42.warp_book.util.WarpUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record C2SCoordsNamePacket(String name, UUID playerId, int hand) {

    public static void encode(C2SCoordsNamePacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.name);
        buf.writeUUID(msg.playerId);
        buf.writeInt(msg.hand);
    }

    public static C2SCoordsNamePacket decode(FriendlyByteBuf buf) {
        return new C2SCoordsNamePacket(
                buf.readUtf(),
                buf.readUUID(),
                buf.readInt()
        );
    }

    public static void handle(C2SCoordsNamePacket msg, Supplier<NetworkEvent.Context> ctxGetter) {
        NetworkEvent.Context ctx = ctxGetter.get();

        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            Player targetPlayer = player.level().getPlayerByUUID(msg.playerId);
            if (targetPlayer == null) return;

            InteractionHand hand = InteractionHand.values()[msg.hand];
            ItemStack stack = targetPlayer.getItemInHand(hand);

            stack.shrink(1);

            ItemStack newPage = WarpUtils.bindItemStackToLocation(
                    new ItemStack(Registration.ItemRegistry.WARP_PAGE_ITEM_LOCATION.get()),
                    msg.name,
                    targetPlayer
            );

            if (!targetPlayer.addItem(newPage)) {
                targetPlayer.level().addFreshEntity(
                        new ItemEntity(
                                targetPlayer.level(),
                                targetPlayer.getX(),
                                targetPlayer.getY(),
                                targetPlayer.getZ(),
                                newPage
                        )
                );
            }
        });

        ctx.setPacketHandled(true);
    }
}