/**
 * @author DarkgreenWorld, Claude Opus 5.5
 * Created at: 2026.10.07
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.platform;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.network.packet.C2SCoordsNamePacket;
import com.panicnot42.warp_book.content.network.packet.C2STakeBookPacket;
import com.panicnot42.warp_book.content.network.packet.C2SWarpPacket;
import com.panicnot42.warp_book.content.network.packet.IPacket;
import com.panicnot42.warp_book.content.network.packet.S2COpenLecternPacket;
import com.panicnot42.warp_book.content.network.packet.S2CPacketEffect;
import com.panicnot42.warp_book.content.network.packet.C2SLecternBookPacket;
import com.panicnot42.warp_book.content.network.packet.S2CLecternBookPacket;
import com.panicnot42.warp_book.content.network.packet.C2STurnLecternPagePacket;
import com.panicnot42.warp_book.platform.services.IPlatformHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
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
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import java.util.function.BiFunction;

import java.nio.file.Path;
import java.util.function.Supplier;

public class PlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    // Fabric registers right away, so the order in Registration.init matters
    @Override
    public <R, T extends R> Supplier<T> register(Registry<R> registry, String name, Supplier<T> factory) {

        T value = Registry.register(registry, Database.rl(name), factory.get());
        return () -> value;
    }

    // Fabric API does not open the vanilla supplier type to other mods on 1.21.1, its own builder still works
    @Override
    @SuppressWarnings("deprecation")
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> factory, Block... blocks) {

        return FabricBlockEntityTypeBuilder.<T>create(factory::apply, blocks).build();
    }

    @Override
    public <T extends AbstractContainerMenu, D> MenuType<T> createMenuType(MenuFactory<T, D> factory, StreamCodec<? super RegistryFriendlyByteBuf, D> dataCodec) {

        return new ExtendedScreenHandlerType<>(factory::create, dataCodec);
    }

    // The codec already lives on the ExtendedScreenHandlerType, Fabric only asks for the data
    @Override
    public <D> void openMenu(ServerPlayer player, MenuProvider provider, D data, StreamCodec<? super RegistryFriendlyByteBuf, D> dataCodec) {

        player.openMenu(new ExtendedScreenHandlerFactory<D>() {

            @Override
            public D getScreenOpeningData(ServerPlayer openingPlayer) {

                return data;
            }

            @Override
            public Component getDisplayName() {

                return provider.getDisplayName();
            }

            @Override
            public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player menuPlayer) {

                return provider.createMenu(containerId, inventory, menuPlayer);
            }
        });
    }

    public static void registerPackets() {

        PayloadTypeRegistry.playS2C().register(S2CPacketEffect.TYPE, S2CPacketEffect.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(S2COpenLecternPacket.TYPE, S2COpenLecternPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(S2CLecternBookPacket.TYPE, S2CLecternBookPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(C2SWarpPacket.TYPE, C2SWarpPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(C2SCoordsNamePacket.TYPE, C2SCoordsNamePacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(C2STakeBookPacket.TYPE, C2STakeBookPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(C2SLecternBookPacket.TYPE, C2SLecternBookPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(C2STurnLecternPagePacket.TYPE, C2STurnLecternPagePacket.STREAM_CODEC);

        // Fabric calls these on the server thread; the S2C receiver is registered in ClientHandler
        ServerPlayNetworking.registerGlobalReceiver(C2SWarpPacket.TYPE, (packet, context) -> packet.handle(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(C2SCoordsNamePacket.TYPE, (packet, context) -> packet.handle(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(C2STakeBookPacket.TYPE, (packet, context) -> packet.handle(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(C2SLecternBookPacket.TYPE, (packet, context) -> packet.handle(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(C2STurnLecternPagePacket.TYPE, (packet, context) -> packet.handle(context.player()));
    }

    @Override
    public Path getConfigDir() {

        return FabricLoader.getInstance().getConfigDir();
    }

    // Only ever called from client code
    @Override
    public void sendToServer(IPacket packet) {

        ClientPlayNetworking.send(packet);
    }

    @Override
    public void sendToPlayer(ServerPlayer player, IPacket packet) {

        ServerPlayNetworking.send(player, packet);
    }

    @Override
    public void sendToPlayersNear(ServerLevel level, @Nullable ServerPlayer exclude, Vec3 pos, double radius, IPacket packet) {

        for (ServerPlayer target : PlayerLookup.around(level, pos, radius))
            if (target != exclude)
                ServerPlayNetworking.send(target, packet);
    }

    // Fabric has no teleport event, so nothing can adjust or cancel the warp
    @Override
    public @Nullable Vec3 fireTeleportEvent(Player player, double x, double y, double z) {

        return new Vec3(x, y, z);
    }
}
