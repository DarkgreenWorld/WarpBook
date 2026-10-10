/**
 * @author DarkgreenWorld, Claude Opus 5.5
 * Created at: 2026.10.07
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.platform.services;

import com.panicnot42.warp_book.content.network.packet.IPacket;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import java.util.function.BiFunction;

import java.nio.file.Path;
import java.util.function.Supplier;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {

        return isDevelopmentEnvironment() ? "development" : "production";
    }

    // NeoForge and Forge register lazily, Fabric immediately, so only read objects through the returned supplier
    <R, T extends R> Supplier<T> register(Registry<R> registry, String name, Supplier<T> factory);

    // BlockEntityType.Builder.of takes a supplier type that is package private in vanilla
    <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> factory, Block... blocks);

    // The client side menu is built from extra data sent by the server
    <T extends AbstractContainerMenu, D> MenuType<T> createMenuType(MenuFactory<T, D> factory, StreamCodec<? super RegistryFriendlyByteBuf, D> dataCodec);

    <D> void openMenu(ServerPlayer player, MenuProvider provider, D data, StreamCodec<? super RegistryFriendlyByteBuf, D> dataCodec);

    Path getConfigDir();

    void sendToServer(IPacket packet);

    void sendToPlayer(ServerPlayer player, IPacket packet);

    void sendToPlayersNear(ServerLevel level, @Nullable ServerPlayer exclude, Vec3 pos, double radius, IPacket packet);

    // Lets other mods adjust the target, null means the teleport was cancelled
    @Nullable
    Vec3 fireTeleportEvent(Player player, double x, double y, double z);

    @FunctionalInterface
    interface MenuFactory<T extends AbstractContainerMenu, D> {

        T create(int containerId, Inventory inventory, D data);
    }
}
