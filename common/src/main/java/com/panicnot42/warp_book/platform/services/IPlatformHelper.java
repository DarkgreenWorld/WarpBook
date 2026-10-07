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

    /**
     * Registers an object. NeoForge registers lazily, Fabric immediately, so only read it through the returned supplier.
     */
    <R, T extends R> Supplier<T> register(Registry<R> registry, String name, Supplier<T> factory);

    /**
     * Creates a menu type whose client side instance is built from extra data sent by the server.
     */
    <T extends AbstractContainerMenu, D> MenuType<T> createMenuType(MenuFactory<T, D> factory, StreamCodec<? super RegistryFriendlyByteBuf, D> dataCodec);

    /**
     * Opens a menu created by {@link #createMenuType} and sends the extra data to the client.
     */
    <D> void openMenu(ServerPlayer player, MenuProvider provider, D data, StreamCodec<? super RegistryFriendlyByteBuf, D> dataCodec);

    void sendToServer(IPacket packet);

    void sendToPlayersNear(ServerLevel level, @Nullable ServerPlayer exclude, Vec3 pos, double radius, IPacket packet);

    /**
     * Lets other mods adjust or cancel a teleport.
     *
     * @return The (possibly changed) target, or null if the teleport was cancelled.
     */
    @Nullable
    Vec3 fireTeleportEvent(Player player, double x, double y, double z);

    @FunctionalInterface
    interface MenuFactory<T extends AbstractContainerMenu, D> {

        T create(int containerId, Inventory inventory, D data);
    }
}
