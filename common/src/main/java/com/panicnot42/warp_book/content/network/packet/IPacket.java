/**
 * @author ArcAnc, MelonVRneu, DarkgreenWorld, Panicnot42, FerreusVeritas
 * Created at: 07.08.2024
 * Copyright (c) 2024-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.network.packet;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public interface IPacket extends CustomPacketPayload
{
    // Runs on the main thread, the player is the sender on the server and the local player on the client
    void handle(Player player);
}
