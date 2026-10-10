/**
 * @author ArcAnc, MelonVRneu, DarkgreenWorld, Panicnot42, FerreusVeritas
 * Created at: 07.08.2024
 * Copyright (c) 2024-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.util;

import com.panicnot42.warp_book.WarpBook;
import com.panicnot42.warp_book.client.ClientHooks;
import com.panicnot42.warp_book.client.WarpLecternRenderer;
import com.panicnot42.warp_book.content.block.WarpLecternBlockEntity;
import com.panicnot42.warp_book.content.gui.GuiWarpBookItemInventory;
import com.panicnot42.warp_book.content.gui.GuiWarpScribingTable;
import com.panicnot42.warp_book.content.gui.inventory.MenuWarpBook;
import com.panicnot42.warp_book.content.gui.inventory.container.ContainerWarpBook;
import com.panicnot42.warp_book.content.gui.inventory.container.ContainerWarpBookSpecial;
import com.panicnot42.warp_book.content.item.IColorable;
import com.panicnot42.warp_book.content.item.WarpBookItem;
import com.panicnot42.warp_book.content.network.NetworkEngine;
import com.panicnot42.warp_book.content.network.packet.S2COpenLecternPacket;
import com.panicnot42.warp_book.content.savedData.DeathSavedData;
import com.panicnot42.warp_book.registration.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EventHandler {

    public static void initEvents(@NotNull final IEventBus modEventBus) {
        MinecraftForge.EVENT_BUS.addListener(EventHandler::onDeath);
        MinecraftForge.EVENT_BUS.addListener(EventHandler::onPlayerRespawn);
        MinecraftForge.EVENT_BUS.addListener(EventHandler::onUseBlock);
        modEventBus.addListener(EventHandler::onCommonSetup);

        if (FMLLoader.getDist().isClient()) {
            modEventBus.addListener(EventHandler::registerItemColor);
            modEventBus.addListener(EventHandler::onClientSetup);
            modEventBus.addListener(EventHandler::registerRenderers);
            MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TickEvent.ClientTickEvent.class, event ->
            {
                if (event.phase == TickEvent.Phase.END)
                    ClientHooks.tickWarpingVision();
            });
        }
    }

    public static void onDeath(@NotNull final LivingDeathEvent event) {
        if (!WarpBook.deathPagesEnabled || !(event.getEntity() instanceof Player player))
            return;

        Level level = player.level();
        if (!level.isClientSide) {
            outer:
            for (NonNullList<ItemStack> list : List.of(player.getInventory().items, player.getInventory().offhand)) {
                for (int q = 0; q < list.size(); q++) {
                    ItemStack item = list.get(q);
                    if (item.is(Registration.ItemRegistry.WARP_BOOK.get()) && WarpBookItem.getRespawnsLeft(item) > 0) {
                        WarpBookItem.decrRespawnsLeft(item);
                    } else if (item.is(Registration.ItemRegistry.WARP_PAGE_ITEM_DEATHLY.get())) {
                        item.shrink(1);
                    } else continue;

                    DeathSavedData data = DeathSavedData.getInstance();
                    GlobalPos pos = GlobalPos.of(player.level().dimension(), player.blockPosition());
                    data.POSITIONS.put(player.getUUID(), pos);
                    data.setDirty();

                    break outer;
                }
            }
        }
    }

    public static void onPlayerRespawn(@NotNull final PlayerEvent.PlayerRespawnEvent event) {
        if (WarpBook.deathPagesEnabled && !event.isEndConquered()) {
        	Player player = event.getEntity();
        	
            if (!player.level().isClientSide()) {
                DeathSavedData data = DeathSavedData.getInstance();
                GlobalPos death = data.POSITIONS.get(player.getUUID());
                if (death != null) {
                    ItemStack page = new ItemStack(Registration.ItemRegistry.WARP_PAGE_ITEM_LOCATION.get(), 1);
                    WarpUtils.bindItemStackToLocation(page, "Death...", death);
                    
                    if (!player.addItem(page)) {
                        ItemEntity itemEntity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), page);
                        player.level().addFreshEntity(itemEntity);
                    }
                    data.removeDeath(player.getUUID());
                }
            }
        }
    }

    public static void onUseBlock(@NotNull final PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() == InteractionHand.MAIN_HAND && useLectern(event.getEntity(), event.getLevel(), event.getPos())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    // Returns true when the click was handled and the vanilla interaction must not run
    private static boolean useLectern(@NotNull Player player, @NotNull Level level, @NotNull BlockPos pos) {
        // The client cannot see what lies on a vanilla lectern, so it only claims clicks on a warp lectern
        if (level.isClientSide())
            return level.getBlockState(pos).is(Registration.BlockRegistry.WARP_LECTERN.get());

        ItemStack book = WarpLecternBlockEntity.findBook(level, pos);
        if (book.isEmpty() || player.isSpectator() || !(player instanceof ServerPlayer serverPlayer))
            return false;

        BlockEntity lectern = level.getBlockEntity(pos);
        boolean warpLectern = lectern instanceof WarpLecternBlockEntity;
        Component title = warpLectern ? ((WarpLecternBlockEntity) lectern).getName() : book.getHoverName();
        if (!player.isSecondaryUseActive())
            NetworkEngine.sendToPlayer(new S2COpenLecternPacket(pos, book, warpLectern ? ((WarpLecternBlockEntity) lectern).getPage() : 0), serverPlayer);
        // Like vanilla, sneaking with an item in hand on a vanilla lectern uses the item instead
        else if (warpLectern || player.getMainHandItem().isEmpty() && player.getOffhandItem().isEmpty())
            NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider(
                    (containerId, inventory, menuPlayer) -> new MenuWarpBook(containerId, inventory, new ContainerWarpBook(book), new ContainerWarpBookSpecial(book), pos, warpLectern),
                    title), buf -> {
                buf.writeItem(book);
                buf.writeBoolean(warpLectern);
            });
        else
            return false;
        return true;
    }

    private static void onCommonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(Registration.PotionRegistry::addBrewingRecipes);
    }

    public static void registerItemColor(@NotNull final RegisterColorHandlersEvent.Item event) {
        Registration.ItemRegistry.ITEMS.getEntries().stream()
                .map(RegistryObject::get)
                .filter(item -> item instanceof IColorable)
                .forEach(item ->
                        event.register((stack, tintIndex) -> ((IColorable) item).getColor(stack, tintIndex), item));
    }

    private static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            net.minecraft.client.gui.screens.MenuScreens.register(
                Registration.MenuTypeRegistry.WARP_BOOK.get(), 
                GuiWarpBookItemInventory::new
            );
            net.minecraft.client.gui.screens.MenuScreens.register(
                Registration.MenuTypeRegistry.WARP_SCRIBING_TABLE.get(),
                GuiWarpScribingTable::new
            );
        });
    }

    private static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(Registration.BlockEntityRegistry.WARP_LECTERN.get(), WarpLecternRenderer::new);
        // Takes the place of vanilla LecternRenderer, so a warp book on a vanilla lectern gets its own texture
        event.registerBlockEntityRenderer(BlockEntityType.LECTERN, WarpLecternRenderer::new);
    }
}
