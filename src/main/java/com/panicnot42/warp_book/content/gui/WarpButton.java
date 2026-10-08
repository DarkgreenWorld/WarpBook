/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public class WarpButton extends Button
{
    private final int index;

    public WarpButton(int index, int x, int y, int widthIn, int heightIn, Button.OnPress onPress)
    {
    	super(x, y, widthIn, heightIn, Component.empty(), onPress, Button.DEFAULT_NARRATION);
        this.index = index;
    }

    public int getIndex() {
        return index;
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
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
            int textureY = textureY();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            RenderSystem.enableDepthTest();

            int halfWidth = this.width / 2;
            int halfHeight = this.height / 2;

            //Upper Left
            guiGraphics.blitNineSliced(WIDGETS_LOCATION, this.getX(), this.getY(), halfWidth, halfHeight, 20, 4, 200, 20, 0, textureY);
            //Lower Left
            guiGraphics.blitNineSliced(WIDGETS_LOCATION, this.getX(), this.getY() + halfHeight, halfWidth, halfHeight, 20, 4, 200, 20, 0, textureY);
            //Upper Right
            guiGraphics.blitNineSliced(WIDGETS_LOCATION, this.getX() + halfWidth, this.getY(), halfWidth, halfHeight, 20, 4, 200, 20, 0, textureY);
            //Lower Right
            guiGraphics.blitNineSliced(WIDGETS_LOCATION, this.getX() + halfWidth, this.getY() + halfHeight, halfWidth, halfHeight, 20, 4, 200, 20, 0, textureY);

            int textColor = 0xFF646451;

            if (!this.active)
                textColor = 0xFF58584D;
            else if (this.isHovered)
                textColor = 0xFF79793C;

            if(this.getMessage().getString().equals("...") )
                textColor = 0xFF4E4E0E;

            this.renderString(guiGraphics, Minecraft.getInstance().font, textColor);
        }
    }

    // 1.20.1 has no button sprites yet: buttons come from rows of widgets.png (disabled, normal, highlighted)
    private int textureY()
    {
        int row = 1;
        if (!this.active)
            row = 0;
        else if (this.isHoveredOrFocused())
            row = 2;
        return 46 + row * 20;
    }
}