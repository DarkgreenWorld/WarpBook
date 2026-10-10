/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.util;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.client.ClientHooks;
import com.panicnot42.warp_book.content.gui.GuiWarpScribingTable;
import com.panicnot42.warp_book.content.gui.GuiWarpBookItemInventory;
import com.panicnot42.warp_book.content.item.IColorable;
import com.panicnot42.warp_book.registration.Registration;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;
import com.panicnot42.warp_book.client.WarpLecternRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Database.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientHandler
{
	@SubscribeEvent
	public static void onClientSetup(@NotNull final FMLClientSetupEvent event)
	{
		event.enqueueWork(() ->
		{
			MenuScreens.register(Registration.MenuTypeRegistry.WARP_BOOK.get(), GuiWarpBookItemInventory :: new);
			MenuScreens.register(Registration.MenuTypeRegistry.WARP_SCRIBING_TABLE.get(), GuiWarpScribingTable :: new);
			MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TickEvent.ClientTickEvent.Post.class, tick -> ClientHooks.tickWarpingVision());
		});
	}

	@SubscribeEvent
	public static void registerRenderers(@NotNull final EntityRenderersEvent.RegisterRenderers event)
	{
		event.registerBlockEntityRenderer(Registration.BlockEntityRegistry.WARP_LECTERN.get(), WarpLecternRenderer :: new);
		event.registerBlockEntityRenderer(BlockEntityType.LECTERN, WarpLecternRenderer :: new);
	}

	@SubscribeEvent
	public static void registerItemColor(@NotNull final RegisterColorHandlersEvent.Item event)
	{
		Registration.ItemRegistry.ITEMS.stream().
				map(Supplier::get).
				filter(item -> item instanceof IColorable).
				forEach(item ->
						event.register((stack, tintIndex) -> ((IColorable)item).getColor(stack, tintIndex), item));
	}
}
