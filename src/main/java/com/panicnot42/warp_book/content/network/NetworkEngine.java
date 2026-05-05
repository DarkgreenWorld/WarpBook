/**
 * @author MelonVRneu, ArcAnc, DarkgreenWorld, Panicnot42, FerreusVeritas
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.event.RegisterPayloadHandlersEvent;
import net.minecraftforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class NetworkEngine
{
    public static void setupMessages(final @NotNull RegisterPayloadHandlersEvent event)
    {
        final PayloadRegistrar registrar = event.registrar(Database.MOD_ID);

        registerMessage(registrar, S2CPacketEffect.STREAM_CODEC, S2CPacketEffect.TYPE, PacketFlow.CLIENTBOUND);
        registerMessage(registrar, C2SWarpPacket.STREAM_CODEC, C2SWarpPacket.TYPE, PacketFlow.SERVERBOUND);
        registerMessage(registrar, C2SCoordsNamePacket.STREAM_CODEC, C2SCoordsNamePacket.TYPE, PacketFlow.SERVERBOUND);
    }

    private <T extends IPacket> void registerMessage(
            PayloadRegistrar registrar, StreamCodec<? super RegistryFriendlyByteBuf,T> reader, CustomPacketPayload.Type<T> type
    )
    {
        registerMessage(registrar, reader, type, Optional.empty());
    }

    private static <T extends IPacket> void registerMessage(
            PayloadRegistrar registrar, StreamCodec<? super RegistryFriendlyByteBuf,T> reader, CustomPacketPayload.Type<T> type, @NotNull PacketFlow direction
    )
    {
        registerMessage(registrar, reader, type, Optional.of(direction));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static <T extends IPacket> void registerMessage(
            PayloadRegistrar registrar, StreamCodec<? super RegistryFriendlyByteBuf, T> reader, CustomPacketPayload.Type<T> type, @NotNull Optional<PacketFlow> direction
    )
    {
        if(direction.isPresent())
            if (direction.get() == PacketFlow.CLIENTBOUND)
                registrar.playToClient(type, reader, T :: handle);
            else
                registrar.playToServer(type, reader, T :: handle);
        else
            registrar.playBidirectional(type, reader, T :: handle);

    }

    public static void sendToServer(@NotNull final IPacket packet)
    {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendToAllClients(@NotNull final IPacket packet)
    {
        PacketDistributor.sendToAllPlayers(packet);
    }

    public static void sendToPlayer(@NotNull ServerPlayer player, @NotNull final IPacket packet)
    {
        PacketDistributor.sendToPlayer(player, packet);
    }

    public static void sendToPlayerNear(@NotNull ServerLevel level, @Nullable ServerPlayer exclude, @NotNull Vec3 position, double radius, @NotNull IPacket packet)
    {
        PacketDistributor.sendToPlayersNear(level, exclude, position.x(), position.y(), position.z(), radius, packet);
    }
}
