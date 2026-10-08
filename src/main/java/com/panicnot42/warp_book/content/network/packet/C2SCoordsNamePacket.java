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
import com.panicnot42.warp_book.registration.Registration;
import com.panicnot42.warp_book.util.WarpUtils;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record C2SCoordsNamePacket(String name, UUID playerId, int hand) implements IPacket
{
	public static final PacketType<C2SCoordsNamePacket> TYPE = PacketType.create(Database.rl("packet_coords_name"), C2SCoordsNamePacket :: new);

	public C2SCoordsNamePacket(FriendlyByteBuf buf)
	{
		this(buf.readUtf(), buf.readUUID(), buf.readInt());
	}

	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeUtf(name);
		buf.writeUUID(playerId);
		buf.writeInt(hand);
	}

	@Override
	public void handle(@NotNull Player player)
	{
		Player targetPlayer = player.level().getPlayerByUUID(playerId);
		if (targetPlayer == null) targetPlayer = player;

		InteractionHand usedHand = InteractionHand.values()[hand];
		ItemStack stack = targetPlayer.getItemInHand(usedHand);
		if (stack.isEmpty())
			return;

		stack.shrink(1);
		ItemStack newPage = WarpUtils.bindItemStackToLocation(new ItemStack(Registration.ItemRegistry.WARP_PAGE_ITEM_LOCATION.get()), name, targetPlayer);
		if (!targetPlayer.addItem(newPage))
		{
			ItemEntity item = new ItemEntity(targetPlayer.level(), targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ(), newPage);
			targetPlayer.level().addFreshEntity(item);
		}
	}

	@Override
	public @NotNull PacketType<?> getType()
	{
		return TYPE;
	}
}
