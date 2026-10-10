/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book;

import com.panicnot42.warp_book.content.core.WarpDrive;
import com.panicnot42.warp_book.content.network.NetworkEngine;
import com.panicnot42.warp_book.registration.Registration;
import com.panicnot42.warp_book.util.EventHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

@Mod(Database.MOD_ID)
public class WarpBook
{
	public static final Logger logger = LogManager.getLogger(Database.MOD_ID);

	public static WarpDrive warpDrive = new WarpDrive();

	public static double exhaustionCoefficient =  0.d;
	public static double minExhaustionDistance = 256.d;
	public static double maxExhaustionDistance = 16384.d;
	public static double distanceCoefficient = 1/256d;
	public static boolean deathPagesEnabled = true;
	public static boolean onlyOwnerTakesPages = false;
	// Warping potion test switch, turns on the brewing and swaps in the warping potion recipes
	public static final boolean WARPING_POTION_TEST = false;


	public WarpBook()
	{
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		loadConfig(FMLPaths.CONFIGDIR.get());
		
		Registration.init(modEventBus);

		EventHandler.initEvents(modEventBus);
		
		NetworkEngine.register();
	}

	private static void loadConfig(@NotNull Path configDir)
	{
		Path file = configDir.resolve(Database.MOD_ID + ".properties");
		Properties properties = new Properties();
		try
		{
			if (Files.exists(file))
				try (Reader reader = Files.newBufferedReader(file))
				{
					properties.load(reader);
				}
			onlyOwnerTakesPages = Boolean.parseBoolean(properties.getProperty(Database.CONFIG_ONLY_OWNER_TAKES_PAGES, String.valueOf(onlyOwnerTakesPages)));
			properties.setProperty(Database.CONFIG_ONLY_OWNER_TAKES_PAGES, String.valueOf(onlyOwnerTakesPages));
			Files.createDirectories(configDir);
			try (Writer writer = Files.newBufferedWriter(file))
			{
				properties.store(writer, Database.NAME);
			}
		}
		catch (IOException e)
		{
			logger.warn("Could not load {}", file, e);
		}
	}
}
