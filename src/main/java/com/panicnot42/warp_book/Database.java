/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book;

import com.panicnot42.warp_book.content.core.WarpDrive;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Database
{
	public static final String MOD_ID = "warp_book";
	public static final String VERSION = "1.21";
	public static final String NAME = "Warp Book";

	public static WarpDrive warpDrive = new WarpDrive();

	public static double exhaustionCoefficient =  0.d;
	public static double minExhaustionDistance = 256.d;
	public static double maxExhaustionDistance = 16384.d;
	public static double distanceCoefficient = 1/256d;
	public static boolean deathPagesEnabled = true;
	public static boolean onlyOwnerTakesPages = false;
	// Warping potion test switch, turns on the brewing and swaps in the warping potion recipes
	public static final boolean WARPING_POTION_TEST = false;

	public static final String CONFIG_ONLY_OWNER_TAKES_PAGES = "onlyOwnerTakesPages";

	public static final String ITEM_NAME_WARP_BOOK = "warp_book";
	public static final String ITEM_NAME_WARP_PAGE_LOCATION = "warp_page_location";
	public static final String ITEM_NAME_WARP_PAGE_UNBOUND = "warp_page_unbound";
	public static final String ITEM_NAME_WARP_PAGE_PLAYER = "warp_page_player";
	public static final String ITEM_NAME_WARP_PAGE_DEATHLY = "warp_page_deathly";
	public static final String ITEM_NAME_WARP_QUILL = "warp_quill";

	public static final String BLOCK_NAME_WARP_LECTERN = "warp_lectern";
	public static final String BLOCK_NAME_WARP_SCRIBING_TABLE = "warp_scribing_table";

	public static final String POTION_NAME_WARPING = "warping";
	public static final String EFFECT_NAME_WARPING = "warping";
	public static final String RECIPE_NAME_PAGE_CLONING = "page_cloning";
	public static final String RECIPE_NAME_SHAPED = "shaped";
	public static final String RECIPE_NAME_SHAPELESS = "shapeless";

	public static final String SOUND_NAME_DEPART = "depart";
	public static final String SOUND_NAME_ARRIVE = "arrive";

	public static final String CREATIVE_TAB_TITLE = rl("creative_tab").toLanguageKey() + ".title";

	// Item NBT keys (1.20.1 has no data components; same names as the 1.21.1 components)
	public static final String TAG_PLAYER_UUID = "target_player_uuid";
	public static final String TAG_COORDINATES = "target_coordinates";
	public static final String TAG_NAME_IN_BOOK = "name_in_book";
	public static final String TAG_WARP_BOOK_CONTENT = "warp_book_content";
	public static final String TAG_WARP_BOOK_DEATHLY = "warp_book_deathly";

	public static final String CONTAINER_WARP_BOOK = "warp_book";

	public static final String GUI_BUTTON_DONE_TITLE = rl("gui.button.done").toLanguageKey() + ".title";
	public static final String GUI_BUTTON_CANCEL_TITLE = rl("gui.button.cancel").toLanguageKey() + ".title";
	public static final String GUI_TEXT_BIND_PAGE = rl("gui.text").toLanguageKey() + ".bind_page";
	public static final String GUI_TEXT_NAME_WAYPOINT = rl("gui.text").toLanguageKey() + ".name_waypoint";
	public static final String GUI_TEXT_WARP_BOOK_INVENTORY_TITLE = rl("gui.text").toLanguageKey() + ".warp_book.inventory.title";
	public static final String GUI_TEXT_WARP_LECTERN_INVENTORY_TITLE = rl("gui.text").toLanguageKey() + ".warp_lectern.inventory.title";
	public static final String GUI_TEXT_WARP_BOOK_BIND_TOOLTIP = rl("gui.text").toLanguageKey() + ".bind_msg.tooltip";
	public static final String GUI_TEXT_DO_WARP = rl("gui.text.do_warp").toLanguageKey();
	public static final String GUI_TEXT_WARP_LECTERN_DO_WARP = rl("gui.text.warp_lectern.do_warp").toLanguageKey();
	public static final String GUI_TEXT_WARP_BOOK_TOOLTIP = rl("gui.tooltip_warp_book").toLanguageKey();
	public static final String GUI_TEXT_TAKE_BOOK = "lectern.take_book";

	public static final String MESSAGE_ERROR_NO_PAGES = rl("message.error.no_pages").toLanguageKey();
	public static final String MESSAGE_ERROR_WARP_LECTERN_NO_PAGES = rl("message.error.warp_lectern.no_pages").toLanguageKey();
	public static final String MESSAGE_ERROR_INVALID_POSITION = rl("message.error.invalid_position").toLanguageKey();
	public static final String MESSAGE_ERROR_INVALID_PLAYER = rl("message.error.invalid_player").toLanguageKey();
	public static final String MESSAGE_ERROR_PLAYER_NOTFOUND = rl("message.error.player_notfound").toLanguageKey();

	public static final String SAVED_DATA_FILE_NAME = rl("saved_data").toDebugFileName();
	public static final String SAVED_DATA_INFO_LIST = "info_list";
	public static final String SAVED_DATA_UUID = "uuid";
	public static final String SAVED_DATA_POSITION = "pos";

	public static final String TAG_BOOK = "book";
	public static final String TAG_OWNERS = "owners";
	public static final String TAG_PAGE = "page";
	public static final String TAG_CUSTOM_NAME = "CustomName";

	public static final int WARPS_PER_PAGE = 10;

	@Contract("_ -> new")
	public static @NotNull ResourceLocation rl(@NotNull String str)
	{
		return new ResourceLocation(MOD_ID, str);
	}

	public static void loadConfig(@NotNull Path configDir)
	{
		Path file = configDir.resolve(MOD_ID + ".properties");
		Properties properties = new Properties();
		try
		{
			if (Files.exists(file))
				try (Reader reader = Files.newBufferedReader(file))
				{
					properties.load(reader);
				}
			onlyOwnerTakesPages = Boolean.parseBoolean(properties.getProperty(CONFIG_ONLY_OWNER_TAKES_PAGES, String.valueOf(onlyOwnerTakesPages)));
			properties.setProperty(CONFIG_ONLY_OWNER_TAKES_PAGES, String.valueOf(onlyOwnerTakesPages));
			Files.createDirectories(configDir);
			try (Writer writer = Files.newBufferedWriter(file))
			{
				properties.store(writer, NAME);
			}
		}
		catch (IOException e)
		{
			LogManager.getLogger(MOD_ID).warn("Could not load {}", file, e);
		}
	}
}
