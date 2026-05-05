/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book;

import com.panicnot42.warp_book.content.core.WarpDrive;
import com.panicnot42.warp_book.registration.Registration;
import com.panicnot42.warp_book.util.EventHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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


	public WarpBook(final IEventBus modEventBus, final ModContainer modContainer)
	{
		Registration.init(modEventBus);

		EventHandler.initEvents(modEventBus);
	}
}
