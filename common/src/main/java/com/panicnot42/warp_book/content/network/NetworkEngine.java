/**
 * @author ArcAnc, MelonVRneu, DarkgreenWorld, Panicnot42, FerreusVeritas
 * Created at: 07.08.2024
 * Copyright (c) 2024-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.network;

import com.panicnot42.warp_book.content.network.packet.IPacket;
import com.panicnot42.warp_book.platform.Services;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// Packets are registered by each loader's PlatformHelper
public class NetworkEngine
{
    public static void sendToServer(@NotNull final IPacket packet)
    {
        Services.PLATFORM.sendToServer(packet);
    }

    public static void sendToPlayerNear(@NotNull ServerLevel level, @Nullable ServerPlayer exclude, @NotNull Vec3 position, double radius, @NotNull IPacket packet)
    {
        Services.PLATFORM.sendToPlayersNear(level, exclude, position, radius, packet);
    }
}
