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
import com.panicnot42.warp_book.client.ClientHooks;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public record S2CPacketEffect(boolean enter, int x, int y, int z) implements IPacket
{

	public static final PacketType<S2CPacketEffect> TYPE = PacketType.create(Database.rl("packet_effect"), S2CPacketEffect :: new);

	public S2CPacketEffect(FriendlyByteBuf buf)
	{
		this(buf.readBoolean(), buf.readInt(), buf.readInt(), buf.readInt());
	}

	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeBoolean(enter);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}

	@Override
	public void handle(@NotNull Player player)
	{
		ClientHooks.playWarpEffect(player, enter, x, y, z);
	}

	@Override
	public @NotNull PacketType<?> getType()
	{
		return TYPE;
	}
}
