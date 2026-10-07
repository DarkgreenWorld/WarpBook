/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book;

import com.panicnot42.warp_book.platform.PlatformHelper;
import com.panicnot42.warp_book.registration.Registration;
import com.panicnot42.warp_book.util.EventHandler;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@Mod(Database.MOD_ID)
public class WarpBook
{
	public static final Logger logger = LogManager.getLogger(Database.MOD_ID);

	public WarpBook(final IEventBus modEventBus, final ModContainer modContainer)
	{
		// Fill the DeferredRegisters first, then attach them to the bus
		Registration.init();
		PlatformHelper.registerAll(modEventBus);

		modEventBus.addListener(PlatformHelper :: registerPackets);

		NeoForge.EVENT_BUS.addListener(WarpBook :: onDeath);
		NeoForge.EVENT_BUS.addListener(WarpBook :: onPlayerRespawn);
	}

	private static void onDeath(@NotNull final LivingDeathEvent event)
	{
		if (event.getEntity() instanceof Player player)
			EventHandler.onDeath(player);
	}

	private static void onPlayerRespawn(@NotNull final PlayerEvent.PlayerRespawnEvent event)
	{
		EventHandler.onPlayerRespawn(event.getEntity(), event.isEndConquered());
	}
}
