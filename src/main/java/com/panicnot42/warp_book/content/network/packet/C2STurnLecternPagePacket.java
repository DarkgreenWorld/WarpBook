/**
 * @author DarkgreenWorld, Claude Opus 5.5
 * Created at: 2026.10.10
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.network.packet;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.block.WarpLecternBlockEntity;
import com.panicnot42.warp_book.content.item.WarpBookItem;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.jetbrains.annotations.NotNull;

// Sent when a player turns the page of a warp lectern's warp list
public record C2STurnLecternPagePacket(BlockPos pos, int page) implements IPacket
{
	public static final PacketType<C2STurnLecternPagePacket> TYPE = PacketType.create(Database.rl("packet_turn_lectern_page"), C2STurnLecternPagePacket :: new);

	public C2STurnLecternPagePacket(FriendlyByteBuf buf)
	{
		this(buf.readBlockPos(), buf.readVarInt());
	}

	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeBlockPos(pos);
		buf.writeVarInt(page);
	}

	@Override
	public void handle(@NotNull Player player)
	{
		if (!pos.closerToCenterThan(player.position(), 8.0))
			return;
		BlockEntity blockEntity = player.level().getBlockEntity(pos);
		if (blockEntity instanceof WarpLecternBlockEntity warpLectern)
			warpLectern.setPage(page);
		else if (blockEntity instanceof LecternBlockEntity lectern && lectern.getBook().getItem() instanceof WarpBookItem)
			lectern.createMenu(0, player.getInventory(), player).setData(0, page);
	}

	@Override
	public @NotNull PacketType<?> getType()
	{
		return TYPE;
	}
}
