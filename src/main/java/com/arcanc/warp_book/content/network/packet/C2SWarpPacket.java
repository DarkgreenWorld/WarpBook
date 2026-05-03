package com.arcanc.warp_book.content.network.packet;

import com.arcanc.warp_book.Database;
import com.arcanc.warp_book.WarpBook;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record C2SWarpPacket(UUID uuid, GlobalPos pos) implements IPacket
{

	public static final CustomPacketPayload.Type<C2SWarpPacket> TYPE = new CustomPacketPayload.Type<>(Database.rl("packet_warp"));
	public static final StreamCodec<ByteBuf, C2SWarpPacket> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC,
			C2SWarpPacket::uuid,
			GlobalPos.STREAM_CODEC,
			C2SWarpPacket :: pos,
			C2SWarpPacket :: new
	);

	@Override
	public void handle(@NotNull IPayloadContext ctx)
	{
		ServerPlayer player = (ServerPlayer) ctx.player();
		if (player == null)
			return;
		ctx.enqueueWork(() ->
		{
			Player teleportedPlayer = player.getServer().getPlayerList().getPlayer(uuid);
			if (teleportedPlayer != null)
			{
				if(pos != null)
				{
					WarpBook.warpDrive.processWarp(teleportedPlayer, pos);
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
