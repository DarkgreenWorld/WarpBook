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
import com.panicnot42.warp_book.content.item.WarpBookItem;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.jetbrains.annotations.NotNull;

// Takes a warp book off a vanilla lectern, mirroring the take button of the vanilla lectern screen
public record C2STakeBookPacket(BlockPos pos) implements IPacket
{
	public static final PacketType<C2STakeBookPacket> TYPE = PacketType.create(Database.rl("packet_take_book"), C2STakeBookPacket :: new);

	public C2STakeBookPacket(FriendlyByteBuf buf)
	{
		this(buf.readBlockPos());
	}

	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeBlockPos(pos);
	}

	@Override
	public void handle(@NotNull Player player)
	{
		Level level = player.level();
		if (!player.mayBuild() || !pos.closerToCenterThan(player.position(), 8.0) || !(level.getBlockEntity(pos) instanceof LecternBlockEntity lectern) || !(lectern.getBook().getItem() instanceof WarpBookItem))
			return;

		ItemStack book = lectern.getBook();
		lectern.setBook(ItemStack.EMPTY);
		LecternBlock.resetBookState(player, level, pos, level.getBlockState(pos), false);
		if (!player.getInventory().add(book))
			player.drop(book, false);
	}

	@Override
	public @NotNull PacketType<?> getType()
	{
		return TYPE;
	}
}
