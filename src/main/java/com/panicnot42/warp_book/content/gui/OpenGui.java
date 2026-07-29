/**
 * @author DarkgreenWorld, Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc
 * Created at: 2026.07.29
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
/*
package com.panicnot42.warp_book.content.gui;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.item.WarpBookItem;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.world.item.ItemStack;

@EventBusSubscriber(modid = Database.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class OpenGui
{
	@SubscribeEvent
    public static void openGui(PlayerInteractEvent.RightClickItem event)
    {
		ItemStack stack = event.getItemStack();
		Player player = event.getEntity();
		InteractionHand usedHand = event.getHand();
		
        if (stack.getItem() instanceof WarpBookItem && !player.isCrouching()) 
            if (event.getLevel().isClientSide())
            	Minecraft.getInstance().setScreen(new GuiBook(player, usedHand));
    }
}
*/
