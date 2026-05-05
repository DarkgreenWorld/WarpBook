/**
 * @author MelonVRneu, ArcAnc, Darkgreen_World, Panicnot42, FerreusVeritas
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
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record C2SCoordsNamePacket(String name, UUID playerId, int hand) implements IPacket
{
	public static final CustomPacketPayload.Type<C2SCoordsNamePacket> TYPE = new CustomPacketPayload.Type<>(Database.rl("packet_coords_name"));
	public static final StreamCodec<FriendlyByteBuf, C2SCoordsNamePacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8,
			C2SCoordsNamePacket :: name,
			UUIDUtil.STREAM_CODEC,
			C2SCoordsNamePacket :: playerId,
			ByteBufCodecs.INT,
			C2SCoordsNamePacket::hand,
			C2SCoordsNamePacket::new
	);

	@Override
	public void handle(@NotNull IPayloadContext ctx)
	{
		ServerPlayer player = (ServerPlayer) ctx.player();
		if (player == null)
			return;
		ctx.enqueueWork(() ->
		{
			Player targetPlayer = player.level().getPlayerByUUID(playerId);
			InteractionHand usedHand = InteractionHand.values()[hand];
			ItemStack stack = targetPlayer.getItemInHand(usedHand);
			stack.shrink(1);
			ItemStack newPage = WarpUtils.bindItemStackToLocation(new ItemStack(Registration.ItemRegistry.WARP_PAGE_ITEM_LOCATION.get()), name, targetPlayer);
			if (!targetPlayer.addItem(newPage))
			{
				ItemEntity item = new ItemEntity(targetPlayer.level(), targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ(), newPage);
				targetPlayer.level().addFreshEntity(item);
			}
		});
	}

	@Override
	public @NotNull Type<C2SCoordsNamePacket> type()
	{
		return TYPE;
	}
}
