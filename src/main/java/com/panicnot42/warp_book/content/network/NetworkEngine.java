/**
 * @author ArcAnc, MelonVRneu, DarkgreenWorld, Panicnot42, FerreusVeritas
 * Created at: 07.08.2024
 * Copyright (c) 2024-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.network;

import com.panicnot42.warp_book.content.network.packet.C2SCoordsNamePacket;
import com.panicnot42.warp_book.content.network.packet.C2SWarpPacket;
import com.panicnot42.warp_book.content.network.packet.IPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetworkEngine
{
    // Fabric calls these on the server thread. The S2C receiver is registered in ClientHandler.
    public static void setupMessages()
    {
        ServerPlayNetworking.registerGlobalReceiver(C2SWarpPacket.TYPE, (packet, player, responseSender) -> packet.handle(player));
        ServerPlayNetworking.registerGlobalReceiver(C2SCoordsNamePacket.TYPE, (packet, player, responseSender) -> packet.handle(player));
    }

    // Only ever called from client code
    public static void sendToServer(@NotNull final IPacket packet)
    {
        ClientPlayNetworking.send(packet);
    }

    public static void sendToPlayerNear(@NotNull ServerLevel level, @Nullable ServerPlayer exclude, @NotNull Vec3 position, double radius, @NotNull IPacket packet)
    {
        for (ServerPlayer target : PlayerLookup.around(level, position, radius))
            if (target != exclude)
                ServerPlayNetworking.send(target, packet);
    }
}
