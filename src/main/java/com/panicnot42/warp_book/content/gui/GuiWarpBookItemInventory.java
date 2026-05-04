/**
 * @author Panicnot42, ArcAnc, Darkgreen_World
 * Copyright (c) 2016-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.gui;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.WarpBook;
import com.panicnot42.warp_book.content.gui.inventory.container.ContainerWarpBook;
import com.panicnot42.warp_book.content.gui.inventory.MenuWarpBook;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GuiWarpBookItemInventory extends AbstractContainerScreen<MenuWarpBook>
{
	private static final ResourceLocation iconLocation = Database.rl("textures/gui/warpinv.png");
	private static final ResourceLocation deathBox = Database.rl("textures/gui/deathbox.png");
	
	private final ContainerWarpBook inventory;
	
	public GuiWarpBookItemInventory(MenuWarpBook warpBookContainerItem, Inventory player, Component title)
	{
		super(warpBookContainerItem, player, Component.empty());
		inventory = warpBookContainerItem.inventory;
		imageWidth = 194;
		imageHeight = 222;
	}

	@Override
	protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY)
	{
		Component s = inventory.getName();
		guiGraphics.drawString(font, s, (imageWidth - 18) / 2 - font.width(s) / 2, 6, 4210752,false);
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
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		int k = (width - imageWidth) / 2;
		int l = (height - imageHeight) / 2;
		guiGraphics.blit(iconLocation, k, l, 0, 0, imageWidth, imageHeight);
		if (WarpBook.deathPagesEnabled)
		{
			guiGraphics.blit(deathBox, k, l, 0, 0, imageWidth, imageHeight);
		}
	}
}
