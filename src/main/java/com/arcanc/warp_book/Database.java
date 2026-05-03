package com.arcanc.warp_book;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Database
{
	public static final String MOD_ID = "warp_book";
	public static final String VERSION = "1.21";
	public static final String NAME = "Warp Book";

	public static final String ITEM_NAME_WARP_BOOK = "warp_book";
	public static final String ITEM_NAME_WARP_PAGE_LOCATION = "warp_page_location";
	public static final String ITEM_NAME_WARP_PAGE_UNBOUND = "warp_page_unbound";
	public static final String ITEM_NAME_WARP_PAGE_PLAYER = "warp_page_player";
	public static final String ITEM_NAME_WARP_PAGE_DEATHLY = "warp_page_deathly";

	public static final String SOUND_NAME_DEPART = "depart";
	public static final String SOUND_NAME_ARRIVE = "arrive";

	public static final String CREATIVE_TAB_TITLE = rl("creative_tab").toLanguageKey() + ".title";

	public static final String DATA_COMPONENT_PLAYER_UUID = "target_player_uuid";
	public static final String DATA_COMPONENT_COORDINATES = "target_coordinates";
	public static final String DATA_COMPONENT_NAME_IN_BOOK = "name_in_book";
	public static final String DATA_COMPONENT_WARP_BOOK_CONTENT = "warp_book_content";
	public static final String DATA_COMPONENT_WARP_BOOK_DEATHLY = "warp_book_deathly";

	public static final String CONTAINER_WARP_BOOK = "warp_book";

	public static final String GUI_BUTTON_DONE_TITLE = rl("gui.button.done").toLanguageKey() + ".title";
	public static final String GUI_BUTTON_CANCEL_TITLE = rl("gui.button.cancel").toLanguageKey() + ".title";
	public static final String GUI_TEXT_BIND_PAGE = rl("gui.text").toLanguageKey() + ".bind_page";
	public static final String GUI_TEXT_NAME_WAYPOINT = rl("gui.text").toLanguageKey() + ".name_waypoint";
	public static final String GUI_TEXT_WARP_BOOK_INVENTORY_TITLE = rl("gui.text").toLanguageKey() + ".warp_book.inventory.title";
	public static final String GUI_TEXT_WARP_BOOK_BIND_TOOLTIP = rl("gui.text").toLanguageKey() + ".bind_msg.tooltip";
	public static final String GUI_TEXT_DO_WARP = rl("gui.text.do_warp").toLanguageKey();
	public static final String GUI_TEXT_WARP_BOOK_TOOLTIP = rl("gui.tooltip_warp_book").toLanguageKey();

	public static final String MESSAGE_ERROR_NO_PAGES = rl("message.error.no_pages").toLanguageKey();
	public static final String MESSAGE_ERROR_INVALID_POSITION = rl("message.error.invalid_position").toLanguageKey();
	public static final String MESSAGE_ERROR_NOTFOUND = rl("message.error.notfound").toLanguageKey();
	public static final String MESSAGE_ERROR_PLAYER_NOTFOUND = rl("message.error.player_notfound").toLanguageKey();

	public static final String SAVED_DATA_FILE_NAME = rl("saved_data").toDebugFileName();
	public static final String SAVED_DATA_INFO_LIST = "info_list";
	public static final String SAVED_DATA_UUID = "uuid";
	public static final String SAVED_DATA_POSITION = "pos";

	@Contract("_ -> new")
	public static @NotNull ResourceLocation rl(@NotNull String str)
	{
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, str);
	}
}
