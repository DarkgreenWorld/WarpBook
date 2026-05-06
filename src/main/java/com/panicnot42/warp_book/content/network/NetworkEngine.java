/**
 * @author ArcAnc, MelonVRneu, DarkgreenWorld, Panicnot42, FerreusVeritas
 * Created at: 07.08.2024
 * Copyright (c) 2024-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */

package com.panicnot42.warp_book.content.network;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.network.packet.C2SCoordsNamePacket;
import com.panicnot42.warp_book.content.network.packet.C2SWarpPacket;
import com.panicnot42.warp_book.content.network.packet.IPacket;
import com.panicnot42.warp_book.content.network.packet.S2CPacketEffect;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class NetworkEngine
{
	private static final int PROTOCOL_VERSION = 1;

    public static final SimpleChannel INSTANCE = ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(Database.MOD_ID, "main"))
            .networkProtocolVersion(PROTOCOL_VERSION)
            .clientAcceptedVersions((status, version) -> true)
            .serverAcceptedVersions((status, version) -> true)
            .simpleChannel();

    private static int packetId = 0;

    private static int nextId() 
    {
        return packetId++;
    }

    public static void setupMessages() {
    	int id = 0;
    	
    	INSTANCE.messageBuilder(S2CPacketEffect.class, id++)
        .codec(S2CPacketEffect.STREAM_CODEC)
        .consumerMainThread(IPacket::handle)
        .add();

    	INSTANCE.messageBuilder(C2SWarpPacket.class, id++)
        .codec(C2SWarpPacket.STREAM_CODEC)
        .consumerMainThread(IPacket::handle)
        .add();

    	INSTANCE.messageBuilder(C2SCoordsNamePacket.class, id++)
        .codec(C2SCoordsNamePacket.STREAM_CODEC)
        .consumerMainThread(IPacket::handle)
        .add();
    }

    public static void sendToServer(@NotNull final IPacket packet) 
    {
        INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
    }

    public static void sendToAllClients(@NotNull final IPacket packet) 
    {
        INSTANCE.send(packet, PacketDistributor.ALL.noArg());
    }

    public static void sendToPlayer(@NotNull ServerPlayer player, @NotNull final IPacket packet) 
    {
        INSTANCE.send(packet, PacketDistributor.PLAYER.with(player));
    }
    
    public static void sendToPlayerNear(@NotNull ServerLevel level, @Nullable ServerPlayer exclude, @NotNull Vec3 position, double radius, @NotNull IPacket packet) 
    {
        INSTANCE.send(packet, PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(exclude, position.x(), position.y(), position.z(), radius, level.dimension())));
    }
}
