package com.panicnot42.warp_book.platform;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.network.packet.C2SCoordsNamePacket;
import com.panicnot42.warp_book.content.network.packet.C2SWarpPacket;
import com.panicnot42.warp_book.content.network.packet.IPacket;
/**
 * @author DarkgreenWorld, Claude Opus 5.5
 * Created at: 2026.10.07
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PlatformHelper implements IPlatformHelper {

    // One DeferredRegister per registry, created on first use
    private static final Map<ResourceKey<?>, DeferredRegister<?>> REGISTERS = new LinkedHashMap<>();

    @Override
    public String getPlatformName() {

        return "NeoForge";
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
                key -> DeferredRegister.create(registry, Database.MOD_ID));
        return register.register(name, factory);
    }

    // Must run after Registration.init() has created every entry
    public static void registerAll(IEventBus modEventBus) {

        REGISTERS.values().forEach(register -> register.register(modEventBus));
    }

    @Override
    public <T extends AbstractContainerMenu, D> MenuType<T> createMenuType(MenuFactory<T, D> factory, StreamCodec<? super RegistryFriendlyByteBuf, D> dataCodec) {

        return IMenuTypeExtension.create((containerId, inventory, buf) -> factory.create(containerId, inventory, dataCodec.decode(buf)));
    }

    @Override
    public <D> void openMenu(ServerPlayer player, MenuProvider provider, D data, StreamCodec<? super RegistryFriendlyByteBuf, D> dataCodec) {

        player.openMenu(provider, buf -> dataCodec.encode(buf, data));
    }

    public static void registerPackets(RegisterPayloadHandlersEvent event) {

        final PayloadRegistrar registrar = event.registrar(Database.MOD_ID);

        registrar.playToClient(S2CPacketEffect.TYPE, S2CPacketEffect.STREAM_CODEC, PlatformHelper::handlePacket);
        registrar.playToServer(C2SWarpPacket.TYPE, C2SWarpPacket.STREAM_CODEC, PlatformHelper::handlePacket);
        registrar.playToServer(C2SCoordsNamePacket.TYPE, C2SCoordsNamePacket.STREAM_CODEC, PlatformHelper::handlePacket);
    }

    private static void handlePacket(IPacket packet, IPayloadContext ctx) {

        ctx.enqueueWork(() -> packet.handle(ctx.player()));
    }

    @Override
    public void sendToServer(IPacket packet) {

        PacketDistributor.sendToServer(packet);
    }

    @Override
    public void sendToPlayersNear(ServerLevel level, @Nullable ServerPlayer exclude, Vec3 pos, double radius, IPacket packet) {

        PacketDistributor.sendToPlayersNear(level, exclude, pos.x(), pos.y(), pos.z(), radius, packet);
    }

    @Override
    public @Nullable Vec3 fireTeleportEvent(Player player, double x, double y, double z) {

        EntityTeleportEvent.TeleportCommand event = EventHooks.onEntityTeleportCommand(player, x, y, z);
        return event.isCanceled() ? null : new Vec3(event.getTargetX(), event.getTargetY(), event.getTargetZ());
    }
}
