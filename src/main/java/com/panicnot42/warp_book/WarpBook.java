/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book;

import com.panicnot42.warp_book.content.network.NetworkEngine;
import com.panicnot42.warp_book.registration.Registration;
import com.panicnot42.warp_book.util.EventHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Database.MOD_ID)
public class WarpBook
{
	public static final Logger logger = LogManager.getLogger(Database.MOD_ID);

	public WarpBook()
	{
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		Database.loadConfig(FMLPaths.CONFIGDIR.get());
		
		Registration.init(modEventBus);

		EventHandler.initEvents(modEventBus);
		
		NetworkEngine.register();
	}

}
