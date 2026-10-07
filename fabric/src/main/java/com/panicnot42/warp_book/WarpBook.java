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
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WarpBook implements ModInitializer
{
	public static final Logger logger = LogManager.getLogger(Database.MOD_ID);

	@Override
	public void onInitialize()
	{
		// Fabric registers immediately, in the order Registration.init() touches the registries
		Registration.init();

		PlatformHelper.registerPackets();

		// Deaths are forwarded by MixinServerPlayer, see there for why it isn't an event
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
				EventHandler.onPlayerRespawn(newPlayer, alive));
	}
}
