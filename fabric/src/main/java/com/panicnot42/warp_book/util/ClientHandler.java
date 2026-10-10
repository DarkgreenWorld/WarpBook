/**
 * @author DarkgreenWorld, Claude Opus 5.5
 * Created at: 2026.10.07
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.util;

import com.panicnot42.warp_book.client.ClientHooks;
import com.panicnot42.warp_book.content.gui.GuiWarpScribingTable;
import com.panicnot42.warp_book.content.gui.GuiWarpBookItemInventory;
import com.panicnot42.warp_book.content.item.IColorable;
import com.panicnot42.warp_book.content.network.packet.S2COpenLecternPacket;
import com.panicnot42.warp_book.content.network.packet.S2CPacketEffect;
import com.panicnot42.warp_book.content.network.packet.S2CLecternBookPacket;
import com.panicnot42.warp_book.registration.Registration;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import com.panicnot42.warp_book.client.WarpLecternRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class ClientHandler implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		MenuScreens.register(Registration.MenuTypeRegistry.WARP_BOOK.get(), GuiWarpBookItemInventory :: new);
		MenuScreens.register(Registration.MenuTypeRegistry.WARP_SCRIBING_TABLE.get(), GuiWarpScribingTable :: new);

		BlockEntityRenderers.register(Registration.BlockEntityRegistry.WARP_LECTERN.get(), WarpLecternRenderer :: new);
		BlockEntityRenderers.register(BlockEntityType.LECTERN, WarpLecternRenderer :: new);

		Registration.ItemRegistry.ITEMS.stream().
				map(Supplier::get).
				filter(item -> item instanceof IColorable).
				forEach(item ->
						ColorProviderRegistry.ITEM.register((stack, tintIndex) -> ((IColorable)item).getColor(stack, tintIndex), item));

		// Fabric calls this on the client thread
		ClientPlayNetworking.registerGlobalReceiver(S2CPacketEffect.TYPE, (packet, context) -> packet.handle(context.player()));
		ClientPlayNetworking.registerGlobalReceiver(S2COpenLecternPacket.TYPE, (packet, context) -> packet.handle(context.player()));
		ClientPlayNetworking.registerGlobalReceiver(S2CLecternBookPacket.TYPE, (packet, context) -> packet.handle(context.player()));

		ClientTickEvents.END_CLIENT_TICK.register(client -> ClientHooks.tickWarpingVision());
	}
}
