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
import com.panicnot42.warp_book.content.network.packet.C2SLecternBookPacket;
import com.panicnot42.warp_book.content.network.packet.C2STakeBookPacket;
import com.panicnot42.warp_book.content.network.packet.C2STurnLecternPagePacket;
import com.panicnot42.warp_book.content.network.packet.C2SWarpPacket;
import com.panicnot42.warp_book.content.network.packet.IPacket;
import com.panicnot42.warp_book.content.network.packet.S2CLecternBookPacket;
import com.panicnot42.warp_book.content.network.packet.S2COpenLecternPacket;
import com.panicnot42.warp_book.content.network.packet.S2CPacketEffect;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class NetworkEngine {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("warp_book", "network"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    public static void register() {
        CHANNEL.registerMessage(id++,
                C2SWarpPacket.class,
                (msg, buf) -> C2SWarpPacket.encode(msg, buf),
                C2SWarpPacket::decode,
                (msg, ctx) -> C2SWarpPacket.handle(msg, ctx)
        );

        CHANNEL.registerMessage(id++,
                C2SCoordsNamePacket.class,
                (msg, buf) -> C2SCoordsNamePacket.encode(msg, buf),
                C2SCoordsNamePacket::decode,
                (msg, ctx) -> C2SCoordsNamePacket.handle(msg, ctx)
        );

        CHANNEL.registerMessage(id++,
                S2CPacketEffect.class,
                (msg, buf) -> S2CPacketEffect.encode(msg, buf),
                S2CPacketEffect::decode,
                (msg, ctx) -> S2CPacketEffect.handle(msg, ctx)
        );

        CHANNEL.registerMessage(id++,
                S2COpenLecternPacket.class,
                S2COpenLecternPacket::encode,
                S2COpenLecternPacket::decode,
                S2COpenLecternPacket::handle
        );

        CHANNEL.registerMessage(id++,
                C2STakeBookPacket.class,
                C2STakeBookPacket::encode,
                C2STakeBookPacket::decode,
                C2STakeBookPacket::handle
        );

        CHANNEL.registerMessage(id++,
                C2SLecternBookPacket.class,
                C2SLecternBookPacket::encode,
                C2SLecternBookPacket::decode,
                C2SLecternBookPacket::handle
        );

        CHANNEL.registerMessage(id++,
                S2CLecternBookPacket.class,
                S2CLecternBookPacket::encode,
                S2CLecternBookPacket::decode,
                S2CLecternBookPacket::handle
        );

        CHANNEL.registerMessage(id++,
                C2STurnLecternPagePacket.class,
                C2STurnLecternPagePacket::encode,
                C2STurnLecternPagePacket::decode,
                C2STurnLecternPagePacket::handle
        );
    }

    public static <T> void sendToServer(T msg) {
        CHANNEL.sendToServer(msg);
    }

    public static <T> void sendToPlayer(T msg, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    public static <T> void sendToTracking(T msg, Level level, BlockPos pos, double radius) {
        CHANNEL.send(PacketDistributor.NEAR.with(() ->
                new PacketDistributor.TargetPoint(
                        pos.getX(), pos.getY(), pos.getZ(),
                        radius,
                        level.dimension()
                )
        ), msg);
    }
}