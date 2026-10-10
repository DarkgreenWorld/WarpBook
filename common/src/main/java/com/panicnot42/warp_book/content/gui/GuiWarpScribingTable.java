/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2016-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.gui;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.gui.inventory.MenuWarpScribingTable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GuiWarpScribingTable extends AbstractContainerScreen<MenuWarpScribingTable>
{
	private static final ResourceLocation BACKGROUND = Database.rl("textures/gui/warp_scribing_table.png");

	public GuiWarpScribingTable(MenuWarpScribingTable menu, Inventory inventory, Component title)
	{
		super(menu, inventory, title);
		imageHeight = 180;
		inventoryLabelY = imageHeight - 94;
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
	{
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY)
	{
		guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}
}
