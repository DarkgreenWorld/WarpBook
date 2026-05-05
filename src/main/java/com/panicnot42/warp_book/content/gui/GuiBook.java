/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, Darkgreen_World
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.gui;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.core.IDeclareWarp;
import com.panicnot42.warp_book.content.item.WarpBookItem;
import com.panicnot42.warp_book.content.network.NetworkEngine;
import com.panicnot42.warp_book.content.network.packet.C2SWarpPacket;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class GuiBook extends Screen
{
	private final Player player;
	private List<ItemStack> items;
	private static final ResourceLocation INV_BG = Database.rl("textures/gui/book.png");
	private int xSize, ySize, page, pageCount;
	private NextPageButton next, prev;
	private ArrayList<WarpButton> warps;
	private ArrayList<ButtonPos> pos;
	private static final int warpsPerPage = 10;
	
	private record ButtonPos(int id, Component name)
	{

	}
	
	private final Supplier<ItemStack> bookBearer;
	
	public GuiBook(Player entityPlayer, InteractionHand hand)
	{
		super(Component.empty());
		this.player = entityPlayer;
		this.bookBearer = () -> entityPlayer.getItemInHand(hand);
	}
	
	protected ItemStack procureBook() {
		return bookBearer.get();
	}
	
	@Override
	public void init()
	{
		children().clear();
		xSize = 146;
		ySize = 180;
		page = 0;
		ItemStack heldItem = procureBook();

		items = WarpBookItem.getContent(heldItem).stream().collect(Collectors.toList());
		if (items.isEmpty())
		{
			player.sendSystemMessage(Component.translatable(Database.MESSAGE_ERROR_NO_PAGES));
			this.onClose();
			return;
		}
		pos = new ArrayList<>();
		for (int i = 0; i < items.size(); i++)
		{
			ItemStack stack = items.get(i);
			if (stack.getItem() instanceof IDeclareWarp declareWarp)
			{
				if(declareWarp.hasValidData(stack))
				{
					pos.add(new ButtonPos(i, declareWarp.getName(Item.TooltipContext.of(player.level()), stack)));
				} else {
					pos.add(new ButtonPos(i, Component.literal("...")));
				}
			}
		}
		
		int x = width / 2 - 48;
		int y = 12 + (height / 2) - (ySize / 2);
		int n = Math.min(warpsPerPage, pos.size());
		warps = new ArrayList<>();
		for (int i = 0; i < n; i++)
		{
			WarpButton but = new WarpButton(i, x, y + (14 * i), 96, 12, button ->
			{
				int index = pos.get(((WarpButton)button).getIndex() + (page*warpsPerPage)).id();

				C2SWarpPacket packet = new C2SWarpPacket(player.getUUID(), index);
				NetworkEngine.sendToServer(packet);
				GuiBook.this.onClose();
			});
			addRenderableWidget(but);
			warps.add(but);
		}
		y = (height / 2) + (ySize / 2);
		
		// Add back and forward buttons
		pageCount = (pos.size() - 1) / (warpsPerPage);
		if (pageCount != 0)
		{
			addRenderableWidget(prev = new NextPageButton(x, y, false, button ->
			{
				page--;
				updateButtonStat();
			}));
			addRenderableWidget(next = new NextPageButton(x + 64, y, true, button ->
			{
				page++;
				updateButtonStat();
			}));
		}
		
		updateButtonStat();
	}

	private void updateButtonStat()
	{
		page = Mth.clamp(page, 0, pageCount);
		if (prev != null)
		{
			prev.visible = page != 0;
		}
		if (next != null)
		{
			next.visible = pageCount > page;
		}
		int r = pos.size() - page * warpsPerPage;
		int n = Math.min(r, warpsPerPage);
		
		//Hide all of the warp buttons
		for(WarpButton wb : warps)
		{
			wb.visible = false;
		}
		
		for (int i = 0; i < n; i++)
		{
			int j = page * warpsPerPage + i;
			WarpButton warp = warps.get(i);
			warp.setMessage(pos.get(j).name);
			warp.visible = true;
		}
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
	{
		super.render(guiGraphics, mouseX, mouseY, partialTick);

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		guiGraphics.blit(INV_BG, (width - xSize) / 2, (height - ySize) / 2, 20, 1, xSize, ySize);
		guiGraphics.drawCenteredString(font, Component.translatable(Database.GUI_TEXT_DO_WARP), width / 2, (height / 2) - ySize / 2 - 12, 0xFFFFFF);
	}

    @OnlyIn(Dist.CLIENT)
    static class NextPageButton extends Button
	{
		private final boolean isForward;

		public NextPageButton(int x, int y, boolean isForwardIn, Button.OnPress onPress)
		{
			super(x, y, 23, 13, Component.empty(), onPress, Button.DEFAULT_NARRATION);
			this.isForward = isForwardIn;
		}

		/**
		 * Draws this button to the screen.
		 */
		@Override
		protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
		{
			if (this.visible)
			{
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				int i = 0;
				int j = 192;

				if (isHovered())
					i += 23;


				if (!this.isForward)
					j += 13;

				guiGraphics.blit(INV_BG, this.getX(), this.getY(), i, j, 23, 13);
			}
		}
	}

	@Override
	public boolean isPauseScreen()
	{
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		KeyMapping inventoryKey = Minecraft.getInstance().options.keyInventory;
	    if (inventoryKey.matches(keyCode, scanCode))
	    {
	        this.onClose();
	        return true;
	    }
		
		boolean result = super.keyPressed(keyCode, scanCode, modifiers);

		switch (keyCode)
		{
			case 203 ->
			{
				page--;
				updateButtonStat();
			}
			case 205 ->
			{
				page++;
				updateButtonStat();
			}
		}

		return result;
	}
}
