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
import com.panicnot42.warp_book.client.ClientHooks;
import com.panicnot42.warp_book.content.network.packet.C2SCoordsNamePacket;
import com.panicnot42.warp_book.content.network.packet.C2SWarpPacket;
import com.panicnot42.warp_book.content.network.packet.IPacket;
import com.panicnot42.warp_book.content.network.packet.S2CPacketEffect;
import com.panicnot42.warp_book.platform.services.IPlatformHelper;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PlatformHelper implements IPlatformHelper {

    // One DeferredRegister per registry, created on first use
    private static final Map<ResourceKey<?>, DeferredRegister<?>> REGISTERS = new LinkedHashMap<>();

    private static final int PROTOCOL_VERSION = 1;

    public static final SimpleChannel CHANNEL = ChannelBuilder
            .named(Database.rl("main"))
            .networkProtocolVersion(PROTOCOL_VERSION)
            .clientAcceptedVersions((status, version) -> true)
            .serverAcceptedVersions((status, version) -> true)
            .simpleChannel();

    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R, T extends R> Supplier<T> register(Registry<R> registry, String name, Supplier<T> factory) {

        DeferredRegister<R> register = (DeferredRegister<R>) REGISTERS.computeIfAbsent(registry.key(),
                key -> DeferredRegister.create(registry.key(), Database.MOD_ID));
        return register.register(name, factory);
    }

    // Must run after Registration.init() has created every entry
    public static void registerAll(IEventBus modEventBus) {

        REGISTERS.values().forEach(register -> register.register(modEventBus));
    }

    // Forge hands menus a plain FriendlyByteBuf, the ItemStack codec needs registry access
    @Override
    public <T extends AbstractContainerMenu, D> MenuType<T> createMenuType(MenuFactory<T, D> factory, StreamCodec<? super RegistryFriendlyByteBuf, D> dataCodec) {

        return IForgeMenuType.create((containerId, inventory, buf) ->
                factory.create(containerId, inventory, dataCodec.decode(new RegistryFriendlyByteBuf(buf, inventory.player.registryAccess()))));
    }

    @Override
    public <D> void openMenu(ServerPlayer player, MenuProvider provider, D data, StreamCodec<? super RegistryFriendlyByteBuf, D> dataCodec) {

        player.openMenu(provider, buf -> dataCodec.encode(new RegistryFriendlyByteBuf(buf, player.registryAccess()), data));
    }

    public static void registerPackets() {

        int id = 0;

        CHANNEL.messageBuilder(S2CPacketEffect.class, id++)
                .codec(S2CPacketEffect.STREAM_CODEC)
                .consumerMainThread(PlatformHelper::handlePacket)
                .add();

        CHANNEL.messageBuilder(C2SWarpPacket.class, id++)
                .codec(C2SWarpPacket.STREAM_CODEC)
                .consumerMainThread(PlatformHelper::handlePacket)
                .add();

        CHANNEL.messageBuilder(C2SCoordsNamePacket.class, id++)
                .codec(C2SCoordsNamePacket.STREAM_CODEC)
                .consumerMainThread(PlatformHelper::handlePacket)
                .add();
    }

    // Already on the main thread. ClientHooks is only touched on the client side.
    private static void handlePacket(IPacket packet, CustomPayloadEvent.Context ctx) {

        Player player = ctx.isClientSide() ? ClientHooks.getClientPlayer() : ctx.getSender();
        if (player != null)
            packet.handle(player);
    }

    @Override
    public void sendToServer(IPacket packet) {

        CHANNEL.send(packet, PacketDistributor.SERVER.noArg());
    }

    @Override
    public void sendToPlayersNear(ServerLevel level, @Nullable ServerPlayer exclude, Vec3 pos, double radius, IPacket packet) {

        CHANNEL.send(packet, PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(exclude, pos.x(), pos.y(), pos.z(), radius, level.dimension())));
    }

    @Override
    public @Nullable Vec3 fireTeleportEvent(Player player, double x, double y, double z) {

        EntityTeleportEvent.TeleportCommand event = ForgeEventFactory.onEntityTeleportCommand(player, x, y, z);
        return event.isCanceled() ? null : new Vec3(event.getTargetX(), event.getTargetY(), event.getTargetZ());
    }
}
