/**
 * @author MelonVRneu, ArcAnc, DarkgreenWorld, Panicnot42, FerreusVeritas
 * Created at: 07.08.2024
 * Copyright (c) 2024-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.network.packet;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.registration.Registration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.network.CustomPayloadEvent;

import org.jetbrains.annotations.NotNull;

public record S2CPacketEffect(boolean enter, int x, int y, int z) implements IPacket {
    public static final CustomPacketPayload.Type<S2CPacketEffect> TYPE = new CustomPacketPayload.Type<>(Database.rl("packet_effect"));

    public static final StreamCodec<FriendlyByteBuf, S2CPacketEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, S2CPacketEffect::enter,
            ByteBufCodecs.INT, S2CPacketEffect::x,
            ByteBufCodecs.INT, S2CPacketEffect::y,
            ByteBufCodecs.INT, S2CPacketEffect::z,
            S2CPacketEffect::new
    );

    @Override
    public void handle(CustomPayloadEvent.Context ctx) 
    {
    	ServerPlayer player = ctx.getSender();
        Level level = player.level();
        RandomSource rand = level.random;
        int particles = (2 - Minecraft.getInstance().options.particles().get().getId()) * 50;

        if (enter) 
        {
            for (int i = 0; i < (5 * particles); ++i) 
            {
                level.addParticle(ParticleTypes.LARGE_SMOKE, x + 0.5, y + (rand.nextDouble() * 2), z + 0.5, 0, 0, 0);
            }
            level.playLocalSound(x, y, z, Registration.SoundRegistry.ARRIVE.get(), SoundSource.PLAYERS, 1.0f, 1.0f, false);
        } 
        else 
        {
            for (int i = 0; i < particles; ++i) 
            {
                level.addParticle(ParticleTypes.PORTAL, x + 0.5, y + (rand.nextDouble() * 2), z + 0.5, rand.nextDouble() - 0.5, rand.nextDouble() - 0.5, rand.nextDouble() - 0.5);
            }
            level.playLocalSound(x, y, z, Registration.SoundRegistry.DEPART.get(), SoundSource.PLAYERS, 1.0f, 1.0f, false);
        }
    }

    @Override
    public @NotNull Type<S2CPacketEffect> type() {
        return TYPE;
    }
}