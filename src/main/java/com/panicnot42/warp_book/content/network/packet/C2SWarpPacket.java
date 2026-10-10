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
import com.panicnot42.warp_book.content.block.WarpLecternBlockEntity;
import com.panicnot42.warp_book.content.core.IDeclareWarp;
import com.panicnot42.warp_book.content.item.WarpBookItem;

import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

// The lectern is set when the warp list was opened from a lectern instead of a held book
public record C2SWarpPacket(UUID uuid, int index, boolean isMainHand, Optional<BlockPos> lectern) implements IPacket
{

	public static final PacketType<C2SWarpPacket> TYPE = PacketType.create(Database.rl("packet_warp"), C2SWarpPacket :: new);

	public C2SWarpPacket(FriendlyByteBuf buf)
	{
		this(buf.readUUID(), buf.readInt(), buf.readBoolean(), buf.readOptional(FriendlyByteBuf::readBlockPos));
	}

	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeUUID(uuid);
		buf.writeInt(index);
		buf.writeBoolean(isMainHand);
		buf.writeOptional(lectern, FriendlyByteBuf::writeBlockPos);
	}

	@Override
	public void handle(@NotNull Player player)
	{
		Player targetPlayer = player.getServer().getPlayerList().getPlayer(uuid);
		if (targetPlayer == null) return;

		InteractionHand usedHand = isMainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		ItemStack stack = lectern.map(pos -> pos.closerToCenterThan(targetPlayer.position(), 8.0) ? WarpLecternBlockEntity.findBook(targetPlayer.level(), pos) : ItemStack.EMPTY).
				orElseGet(() -> targetPlayer.getItemInHand(usedHand));

		if (stack.getItem() instanceof WarpBookItem)
		{
			NonNullList<ItemStack> pages = WarpBookItem.getContent(stack);

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
	public @NotNull PacketType<?> getType()
	{
		return TYPE;
	}
}
