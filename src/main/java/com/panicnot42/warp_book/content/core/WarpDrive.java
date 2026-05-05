/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, Darkgreen_World
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.core;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.WarpBook;
import com.panicnot42.warp_book.content.network.NetworkEngine;
import com.panicnot42.warp_book.content.network.packet.S2CPacketEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class WarpDrive {

    public void processWarp(Player player, GlobalPos globalPos) {

        if (globalPos == null || globalPos.pos() == null) {
            player.sendSystemMessage(Component.translatable(Database.MESSAGE_ERROR_INVALID_POSITION));
            return;
        }

        Level level = player.level();

        if (level.isClientSide()) {
            return;
        }

        ServerLevel currentLevel = (ServerLevel) level;
        ServerLevel targetLevel = currentLevel.getServer().getLevel(globalPos.dimension());

        if (targetLevel == null) {
            player.sendSystemMessage(Component.translatable(Database.MESSAGE_ERROR_INVALID_POSITION));
            return;
        }

        Vec3 oldPos = player.position();
        Vec3 newPos = globalPos.pos().getCenter();

        boolean sameDim = currentLevel.dimension().equals(globalPos.dimension());

        NetworkEngine.sendToTracking(
                new S2CPacketEffect(false, (int) oldPos.x, (int) oldPos.y, (int) oldPos.z),
                currentLevel,
                BlockPos.containing(oldPos),
                64
        );

        teleport(player, targetLevel, newPos);

        NetworkEngine.sendToTracking(
                new S2CPacketEffect(true, (int) newPos.x, (int) newPos.y, (int) newPos.z),
                targetLevel,
                BlockPos.containing(newPos),
                64
        );

        double distance;

        if (sameDim) {
            distance = oldPos.distanceTo(newPos);
        } else {
            distance = Double.POSITIVE_INFINITY;
        }

        player.causeFoodExhaustion((float) calculateExhaustion(player.level().getDifficulty(), distance));
    }

    private static void teleport(Player player, ServerLevel level, Vec3 pos) {
        performTeleport(player, level, pos.x, pos.y, pos.z, player.getYRot(), player.getXRot());
    }

    private static void performTeleport(
            Player player,
            ServerLevel level,
            double x,
            double y,
            double z,
            float yaw,
            float pitch
    ) {
        BlockPos blockPos = BlockPos.containing(x, y, z);

        if (!Level.isInSpawnableBounds(blockPos)) {
            player.sendSystemMessage(Component.translatable(Database.MESSAGE_ERROR_INVALID_POSITION));
            return;
        }

        float yRot = Mth.wrapDegrees(yaw);
        float xRot = Mth.wrapDegrees(pitch);

        if (player.teleportTo(level, x, y, z, EnumSet.noneOf(RelativeMovement.class), yRot, xRot)) {
            player.setDeltaMovement(player.getDeltaMovement().multiply(1.0, 0.0, 1.0));
            player.setOnGround(true);
        }
    }

    private static double calculateExhaustion(Difficulty difficulty, double distance) {

        distance = Mth.clamp(distance, WarpBook.minExhaustionDistance, WarpBook.maxExhaustionDistance);
        double distanceFactor = distance * WarpBook.distanceCoefficient;

        float scale = switch (difficulty) {
            case EASY -> 1.0f;
            case NORMAL -> 1.5f;
            case HARD -> 2.0f;
            case PEACEFUL -> 0.0f;
        };

        return WarpBook.exhaustionCoefficient * scale * distanceFactor;
    }
}