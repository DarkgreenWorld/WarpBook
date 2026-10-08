/**
 * @author ArcAnc, MelonVRneu, DarkgreenWorld, Panicnot42, FerreusVeritas
 * Created at: 07.08.2024
 * Copyright (c) 2024-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.network.packet;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.minecraft.world.entity.player.Player;

public interface IPacket extends FabricPacket
{
    /**
     * Runs on the main thread. For C2S packets the player is the sender (a ServerPlayer),
     * for S2C packets it is the local client player.
     */
    void handle(Player player);
}
