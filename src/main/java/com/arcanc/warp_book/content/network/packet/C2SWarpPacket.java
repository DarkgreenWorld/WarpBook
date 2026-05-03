package com.arcanc.warp_book.content.network.packet;

import com.arcanc.warp_book.Database;
import com.arcanc.warp_book.WarpBook;
import com.arcanc.warp_book.content.core.IDeclareWarp;
import com.arcanc.warp_book.content.item.WarpBookItem;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public record C2SWarpPacket(UUID uuid, int index) implements IPacket
{

	public static final CustomPacketPayload.Type<C2SWarpPacket> TYPE = new CustomPacketPayload.Type<>(Database.rl("packet_warp"));
	public static final StreamCodec<ByteBuf, C2SWarpPacket> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC,
			C2SWarpPacket::uuid,
			ByteBufCodecs.INT,
			C2SWarpPacket :: index,
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
			Player targetPlayer = player.getServer().getPlayerList().getPlayer(uuid);
			
			ItemStack stack = targetPlayer.getMainHandItem();
			
			if (targetPlayer != null && stack.getItem() instanceof WarpBookItem)
			{
				ItemContainerContents item = WarpBookItem.getContent(stack);
				
				if(index >= 0 && index < item.getSlots())
				{
					ItemStack page = item.getStackInSlot(index);
					if(page.getItem() instanceof IDeclareWarp warp)
					{
						if(warp.hasValidData(page))
						{
							GlobalPos pos = warp.getWaypoint(targetPlayer, page);
							if(pos != null) WarpBook.warpDrive.processWarp(targetPlayer, pos);
						}
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
