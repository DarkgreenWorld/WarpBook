/**
 * @author MelonVRneu, ArcAnc, DarkgreenWorld, Panicnot42, FerreusVeritas
 * Created at: 07.08.2024
 * Copyright (c) 2024-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.network.packet;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.WarpBook;
import com.panicnot42.warp_book.content.core.IDeclareWarp;
import com.panicnot42.warp_book.content.item.WarpBookItem;

import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraftforge.event.network.CustomPayloadEvent;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public record C2SWarpPacket(UUID uuid, int index, boolean isMainHand) implements IPacket 
{
    public static final CustomPacketPayload.Type<C2SWarpPacket> TYPE = new CustomPacketPayload.Type<>(Database.rl("packet_warp"));
    
    public static final StreamCodec<FriendlyByteBuf, C2SWarpPacket> STREAM_CODEC = StreamCodec.composite
    (
    		UUIDUtil.STREAM_CODEC, C2SWarpPacket::uuid,
    		ByteBufCodecs.INT, C2SWarpPacket::index,
    		ByteBufCodecs.BOOL,C2SWarpPacket::isMainHand,
    		C2SWarpPacket::new
    );

    @Override
    public void handle(CustomPayloadEvent.Context ctx) 
    {
    	ServerPlayer player = ctx.getSender();
        if (player.getServer() == null) return;
        
        ctx.enqueueWork(() ->
        {
        	Player targetPlayer = player.getServer().getPlayerList().getPlayer(uuid);
        	if (targetPlayer == null) return;
        	
        	InteractionHand usedHand = isMainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        	ItemStack stack = targetPlayer.getItemInHand(usedHand);
        	if (stack.getItem() instanceof WarpBookItem) 
        	{
        		ItemContainerContents item = WarpBookItem.getContent(stack);
        		List<ItemStack> items = item.stream().toList();
        		if (index >= 0 && index < items.size()) 
        		{
        			ItemStack page = items.get(index);
        			if (page.getItem() instanceof IDeclareWarp warp) 
        			{
        				GlobalPos pos = warp.getWaypoint(targetPlayer, page);
        				if (pos != null) WarpBook.warpDrive.processWarp(targetPlayer, pos);
        			}
        		}
        	}
        });
    }

    @Override
    public @NotNull Type<C2SWarpPacket> type() 
    {
        return TYPE;
    }
}