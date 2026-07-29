/**
 * @author DarkgreenWorld, Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc
 * Created at: 2026.07.29
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
/*package com.panicnot42.warp_book.util;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.gui.GuiBook;
import com.panicnot42.warp_book.content.gui.GuiWaypointName;
import com.panicnot42.warp_book.content.item.UnboundWarpPageItem;
import com.panicnot42.warp_book.content.item.WarpBookItem;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraft.world.item.ItemStack;

@EventBusSubscriber(modid = Database.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.FORGE)
public class ClientHandler
{
	public ClientHandler() {}
	
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
	
	@SubscribeEvent
	public static void setWaypointName(PlayerInteractEvent.RightClickItem event)
	{
		ItemStack stack = event.getItemStack();
		Player player = event.getEntity();
		InteractionHand usedHand = event.getHand();
		
        if (stack.getItem() instanceof UnboundWarpPageItem && !player.isCrouching()) 
            if (event.getLevel().isClientSide())
            	Minecraft.getInstance().setScreen(new GuiWaypointName(player, usedHand));
	}
}
*/