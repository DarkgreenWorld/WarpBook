/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.core.IDeclareWarp;
import com.panicnot42.warp_book.content.item.WarpBookItem;
import com.panicnot42.warp_book.content.network.NetworkEngine;
import com.panicnot42.warp_book.content.network.packet.C2STakeBookPacket;
import com.panicnot42.warp_book.content.network.packet.C2STurnLecternPagePacket;
import com.panicnot42.warp_book.content.network.packet.C2SWarpPacket;
import com.panicnot42.warp_book.registration.Registration;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class GuiBook extends Screen
{
	private final Player player;
	private List<ItemStack> items;
	private static final ResourceLocation INV_BG = Database.rl("textures/gui/book.png");
	private static final ResourceLocation WARP_LECTERN_BG = Database.rl("textures/gui/warp_lectern_book.png");
	private int xSize, ySize, page, pageCount;
	private NextPageButton next, prev;
	private ArrayList<WarpButton> warps;
	private ArrayList<ButtonPos> pos;
	private static final int warpsPerPage = Database.WARPS_PER_PAGE;
	private static final int bookTop = 14;
	private static InteractionHand hand;

	private record ButtonPos(int id, Component name)
	{

	}

	private final Supplier<ItemStack> bookBearer;
	private final @Nullable BlockPos lecternPos;
	private final ResourceLocation background;
	private final String heading;
	private final String noPagesMessage;
	// The page the warp lectern was last told about
	private int lecternPage;

	public GuiBook(Player entityPlayer, InteractionHand hand)
	{
		super(Component.empty());
		this.player = entityPlayer;
		this.hand = hand;
		this.bookBearer = () -> entityPlayer.getItemInHand(hand);
		this.lecternPos = null;
		this.background = INV_BG;
		this.heading = Database.GUI_TEXT_DO_WARP;
		this.noPagesMessage = Database.MESSAGE_ERROR_NO_PAGES;
	}

	public GuiBook(Player entityPlayer, ItemStack book, BlockPos lecternPos, int page)
	{
		super(Component.empty());
		this.player = entityPlayer;
		hand = InteractionHand.MAIN_HAND;
		this.bookBearer = () -> book;
		this.lecternPos = lecternPos;
		boolean warpLectern = entityPlayer.level().getBlockState(lecternPos).is(Registration.BlockRegistry.WARP_LECTERN.get());
		this.background = warpLectern ? WARP_LECTERN_BG : INV_BG;
		this.heading = warpLectern ? Database.GUI_TEXT_WARP_LECTERN_DO_WARP : Database.GUI_TEXT_DO_WARP;
		this.noPagesMessage = warpLectern ? Database.MESSAGE_ERROR_WARP_LECTERN_NO_PAGES : Database.MESSAGE_ERROR_NO_PAGES;
		this.page = page;
		this.lecternPage = page;
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
		ItemStack heldItem = procureBook();

		items = WarpBookItem.getContent(heldItem).stream().collect(Collectors.toList());
		pos = new ArrayList<>();
		for (int i = 0; i < items.size(); i++)
		{
			ItemStack stack = items.get(i);
			if (stack.getItem() instanceof IDeclareWarp declareWarp)
			{
				if(declareWarp.hasValidData(stack))
				{
					pos.add(new ButtonPos(i, declareWarp.getSubName(stack)));
				} else {
					pos.add(new ButtonPos(i, Component.literal("...")));
				}
			}
		}

		if (pos.isEmpty())
		{
			player.sendSystemMessage(Component.translatable(noPagesMessage));
			this.onClose();
			return;
		}

		// Laid out like the vanilla lectern screen
		boolean takeBook = lecternPos != null && player.mayBuild() && player.level().getBlockState(lecternPos).is(Blocks.LECTERN);
		addRenderableWidget(Button.builder(Component.translatable(Database.GUI_BUTTON_DONE_TITLE), button -> GuiBook.this.onClose()).
				bounds(width / 2 - 100, bookTop + ySize + 2, takeBook ? 98 : 200, 20).
				build());
		if (takeBook)
			addRenderableWidget(Button.builder(Component.translatable(Database.GUI_TEXT_TAKE_BOOK), button ->
					{
						NetworkEngine.sendToServer(new C2STakeBookPacket(lecternPos));
						GuiBook.this.onClose();
					}).
					bounds(width / 2 + 2, bookTop + ySize + 2, 98, 20).
					build());

		int x = width / 2 - 48;
		// Where a vanilla book starts its text, under the page number
		int y = bookTop + 27;
		int n = Math.min(warpsPerPage, pos.size());
		warps = new ArrayList<>();
		for (int i = 0; i < n; i++)
		{
			WarpButton but = new WarpButton(i, x, y + (12 * i), 96, 12, button ->
			{
				int index = pos.get(((WarpButton)button).getIndex() + (page*warpsPerPage)).id();

				C2SWarpPacket packet = new C2SWarpPacket(player.getUUID(), index, this.hand == InteractionHand.MAIN_HAND, Optional.ofNullable(lecternPos));
				NetworkEngine.sendToServer(packet);
				GuiBook.this.onClose();
			});
			addRenderableWidget(but);
			warps.add(but);
		}
		// The same spots on the book as the page buttons of a vanilla book
		int left = (width - xSize) / 2;
		y = bookTop + 156;

		// Add back and forward buttons
		pageCount = (pos.size() - 1) / (warpsPerPage);
		if (pageCount != 0)
		{
			addWidget(prev = new NextPageButton(left + 23, y, false, background, lecternPos == null, button ->
			{
				page--;
				updateButtonStat();
			}));
			addWidget(next = new NextPageButton(left + 96, y, true, background, lecternPos == null, button ->
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

		if (lecternPos != null && page != lecternPage)
		{
			lecternPage = page;
			NetworkEngine.sendToServer(new C2STurnLecternPagePacket(lecternPos, page));
		}
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
	{
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTick);

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		guiGraphics.blit(background, (width - xSize) / 2, bookTop, 20, 1, xSize, ySize);
		if (prev != null)
		{
			prev.render(guiGraphics, mouseX, mouseY, partialTick);
			next.render(guiGraphics, mouseX, mouseY, partialTick);
		}
		// The same spot and look as the page number of a vanilla book
		Component pageNumber = Component.translatable(Database.GUI_TEXT_PAGE_INDICATOR, page + 1, pageCount + 1);
		guiGraphics.drawString(font, pageNumber, (width - xSize) / 2 + 128 - font.width(pageNumber), bookTop + 15, 0, false);
		guiGraphics.drawCenteredString(font, Component.translatable(heading), width / 2, bookTop - 12, 0xFFFFFF);
	}

	@OnlyIn(Dist.CLIENT)
	static class NextPageButton extends Button
	{
		private final boolean isForward;
		private final ResourceLocation texture;
		private final boolean playTurnSound;

		public NextPageButton(int x, int y, boolean isForwardIn, ResourceLocation texture, boolean playTurnSound, Button.OnPress onPress)
		{
			super(x, y, 23, 13, Component.empty(), onPress, Button.DEFAULT_NARRATION);
			this.isForward = isForwardIn;
			this.texture = texture;
			this.playTurnSound = playTurnSound;
		}

		@Override
		public void playDownSound(@NotNull SoundManager soundManager)
		{
			if (playTurnSound)
				soundManager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
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

				guiGraphics.blit(texture, this.getX(), this.getY(), i, j, 23, 13);
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

		switch (keyCode)
		{
			case InputConstants.KEY_LEFT, InputConstants.KEY_PAGEUP ->
			{
				page--;
				updateButtonStat();
				return true;
			}
			case InputConstants.KEY_RIGHT, InputConstants.KEY_PAGEDOWN ->
			{
				page++;
				updateButtonStat();
				return true;
			}
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}
}