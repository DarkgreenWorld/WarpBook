/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, Darkgreen_World
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class WarpButton extends Button
{
    private final int index;
    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");

    public WarpButton(int index, int x, int y, int widthIn, int heightIn, Button.OnPress onPress)
    {
        super(x, y, widthIn, heightIn, Component.empty(), onPress, Button.DEFAULT_NARRATION);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        if (this.visible)
        {
            Minecraft minecraft = Minecraft.getInstance();
            
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

            // 参数含义：纹理, x, y, u, v, 宽度, 高度, 纹理内宽度(200), 纹理内高度(20), 边框大小(2)
            int i = this.getTextureY();
            guiGraphics.blitWithBorder(WIDGETS_LOCATION, this.getX(), this.getY(), 0, i, this.width, this.height, 200, 20, 2);

            int textColor = 0xFF646451;
            if (!this.active) {
                textColor = 0xFF58584D;
            } else if (this.isHoveredOrFocused()) {
                textColor = 0xFF79793C;
            }

            if (this.getMessage().getString().equals("...")) {
                textColor = 0xFF4E4E0E;
            }

            int finalColor = textColor | Mth.ceil(this.alpha * 255.0F) << 24;
            this.renderString(guiGraphics, minecraft.font, finalColor);
            
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    // 计算标准按钮在 widgets.png 中的 V 坐标
    private int getTextureY() {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (this.isHoveredOrFocused()) {
            i = 2;
        }
        return 46 + i * 20;
    }
}