/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.util;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.gui.GuiWarpBookItemInventory;
import com.panicnot42.warp_book.content.item.IColorable;
import com.panicnot42.warp_book.registration.Registration;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@EventBusSubscriber(modid = Database.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientHandler
{
	@SubscribeEvent
	public static void registerItemColor(@NotNull final RegisterColorHandlersEvent.Item event)
	{
		Registration.ItemRegistry.ITEMS.stream().
				map(Supplier::get).
				filter(item -> item instanceof IColorable).
				forEach(item ->
						event.register((stack, tintIndex) -> ((IColorable)item).getColor(stack, tintIndex), item));
	}

	@SubscribeEvent
	public static void registerMenuScreens(@NotNull final RegisterMenuScreensEvent event)
	{
		event.register(Registration.MenuTypeRegistry.WARP_BOOK.get(), GuiWarpBookItemInventory :: new);
	}
}
