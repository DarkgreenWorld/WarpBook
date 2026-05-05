/**
 * @author ArcAnc, MelonVRneu, Darkgreen_World, Panicnot42, FerreusVeritas
 * Created at: 07.08.2024
 * Copyright (c) 2024-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.network.packet;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.registration.Registration;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

public record S2CPacketEffect(boolean enter, int x, int y, int z) {

    public static void encode(S2CPacketEffect msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.enter);
        buf.writeInt(msg.x);
        buf.writeInt(msg.y);
        buf.writeInt(msg.z);
    }

    public static S2CPacketEffect decode(FriendlyByteBuf buf) {
        return new S2CPacketEffect(
                buf.readBoolean(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt()
        );
    }

    public static void handle(S2CPacketEffect msg, Supplier<NetworkEvent.Context> ctxGetter) {
        NetworkEvent.Context ctx = ctxGetter.get();

        ctx.enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {

                    LocalPlayer player = Minecraft.getInstance().player;
                    if (player == null) return;

                    Level level = player.level();
                    RandomSource rand = level.random;

                    int particlesSetting = Minecraft.getInstance().options.particles().get().getId();
                    int particles = (2 - particlesSetting) * 50;

                    if (msg.enter) {
                        for (int i = 0; i < 5 * particles; i++) {
                            level.addParticle(ParticleTypes.LARGE_SMOKE,
                                    msg.x,
                                    msg.y + rand.nextDouble() * 2,
                                    msg.z,
                                    rand.nextDouble() / 10 - 0.05,
                                    0,
                                    rand.nextDouble() / 10 - 0.05);
                        }
                        level.playSound(player, player.getX(), player.getY(), player.getZ(),
                                Registration.SoundRegistry.ARRIVE.get(),
                                SoundSource.PLAYERS, 1F, 1F);
                    } else {
                        for (int i = 0; i < particles; i++) {
                            level.addParticle(ParticleTypes.PORTAL,
                                    msg.x + 0.5,
                                    msg.y + rand.nextDouble() * 2,
                                    msg.z + 0.5,
                                    rand.nextDouble() - 0.5,
                                    rand.nextDouble() - 0.5,
                                    rand.nextDouble() - 0.5);
                        }
                        level.playSound(player, player.getX(), player.getY(), player.getZ(),
                                Registration.SoundRegistry.DEPART.get(),
                                SoundSource.PLAYERS, 1F, 1F);
                    }
                })
        );

        ctx.setPacketHandled(true);
    }
}