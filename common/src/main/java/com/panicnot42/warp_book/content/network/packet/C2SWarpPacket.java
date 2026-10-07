/**
 * @author ArcAnc, MelonVRneu, DarkgreenWorld, Panicnot42, FerreusVeritas
 * Created at: 07.08.2024
 * Copyright (c) 2024-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.network.packet;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.core.IDeclareWarp;
import com.panicnot42.warp_book.content.item.WarpBookItem;

import net.minecraft.core.GlobalPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record C2SWarpPacket(UUID uuid, int index, boolean isMainHand) implements IPacket
{

	public static final CustomPacketPayload.Type<C2SWarpPacket> TYPE = new CustomPacketPayload.Type<>(Database.rl("packet_warp"));
	public static final StreamCodec<FriendlyByteBuf, C2SWarpPacket> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC,
			C2SWarpPacket::uuid,
			ByteBufCodecs.INT,
			C2SWarpPacket :: index,
			ByteBufCodecs.BOOL,
			C2SWarpPacket::isMainHand,
			C2SWarpPacket :: new
	);

	@Override
	public void handle(@NotNull Player player)
	{
		Player targetPlayer = player.getServer().getPlayerList().getPlayer(uuid);
		if (targetPlayer == null) return;

		InteractionHand usedHand = isMainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		ItemStack stack = targetPlayer.getItemInHand(usedHand);

		if (stack.getItem() instanceof WarpBookItem)
		{
			// getSlots() / getStackInSlot() are NeoForge additions, copyInto() is vanilla
			NonNullList<ItemStack> pages = NonNullList.withSize(54, ItemStack.EMPTY);
			WarpBookItem.getContent(stack).copyInto(pages);

			if(index >= 0 && index < pages.size())
			{
				ItemStack page = pages.get(index);
				if(page.getItem() instanceof IDeclareWarp warp)
				{
					GlobalPos pos = warp.getWaypoint(targetPlayer, page);
					if(pos != null) Database.warpDrive.processWarp(targetPlayer, pos);
				}
			}
		}
	}

	@Override
	public @NotNull Type<C2SWarpPacket> type()
	{
		return TYPE;
	}
}
