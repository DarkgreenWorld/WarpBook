/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, Darkgreen_World
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.gui;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.network.NetworkEngine;
import com.panicnot42.warp_book.content.network.packet.C2SCoordsNamePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class GuiWaypointName extends Screen
{
	private EditBox waypointName;
	private Button doneButton;

	private final Player player;
	private final InteractionHand hand;

	public GuiWaypointName(Player player, InteractionHand hand)
	{
        super(Component.empty());
		this.player = player;
		this.hand = hand;
    }

	@Override
	public void init()
	{
		children().clear();
		addRenderableWidget(doneButton = Button.builder(Component.translatable(Database.GUI_BUTTON_DONE_TITLE), doneButton->
						{
							if (doneButton.isActive())
							{
								C2SCoordsNamePacket packet = new C2SCoordsNamePacket(waypointName.getValue(), player.getGameProfile().getId(), hand.ordinal());
								NetworkEngine.sendToServer(packet);
								GuiWaypointName.this.onClose();
							}
						}).
						pos(width / 2 - 100, height / 4 + 96 + 12).
						size(200, 20).
						build()
				);
		addRenderableWidget(Button.builder(Component.translatable(Database.GUI_BUTTON_CANCEL_TITLE), cancelButton ->
                                GuiWaypointName.this.onClose()).
						pos(width / 2 - 100, height / 4 + 96 + 12 + 22).
						size(200, 20).
						build()
				);
		waypointName = new EditBox(font, width / 2 - 150, 60, 300, 20, Component.empty());
		waypointName.setMaxLength(128);
		waypointName.setFocused(true);
		waypointName.setValue("");
		waypointName.setTextShadow(false);
		this.setFocused(waypointName);
		addRenderableWidget(waypointName);
		doneButton.active = !waypointName.getValue().trim().isEmpty();

	}

	@Override
	public void tick()
	{
		doneButton.active = !waypointName.getValue().trim().isEmpty();
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
	{
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.drawCenteredString(font, Component.translatable(Database.GUI_TEXT_BIND_PAGE), width / 2, 20, 16777215);
		guiGraphics.drawString(font, Component.translatable(Database.GUI_TEXT_NAME_WAYPOINT), width / 2 - 150, 47, 10526880);
	}
}
