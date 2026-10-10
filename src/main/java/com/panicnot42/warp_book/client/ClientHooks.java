/**
 * @author DarkgreenWorld, Claude Opus 5.5
 * Created at: 2026.10.10
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.client;

import com.panicnot42.warp_book.content.gui.GuiBook;
import com.panicnot42.warp_book.content.network.NetworkEngine;
import com.panicnot42.warp_book.content.network.packet.C2SLecternBookPacket;
import com.panicnot42.warp_book.registration.Registration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Everything that touches client only classes, so a dedicated server never loads Minecraft or any Screen
public final class ClientHooks
{
	private static boolean warpingVision;
	// Which vanilla lecterns hold a warp book, and when each one was last asked about
	private static final Set<BlockPos> warpBookLecterns = new HashSet<>();
	private static final Map<BlockPos, Long> lecternQueries = new HashMap<>();
	private static @Nullable Level lecternLevel;

	public static void openLecternGui(ItemStack book, BlockPos lecternPos, int page)
	{
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player != null)
			minecraft.setScreen(new GuiBook(minecraft.player, book, lecternPos, page));
	}

	// Called every client tick, shows the inverted view vanilla uses when spectating an enderman
	public static void tickWarpingVision()
	{
		Minecraft minecraft = Minecraft.getInstance();
		boolean warping = minecraft.player != null && minecraft.player.hasEffect(Registration.MobEffectRegistry.WARPING.get());
		// GameRenderer.loadEffect is private, an enderman handed to the public checkEntityPostEffect loads the same shader
		if (warping != warpingVision)
			minecraft.gameRenderer.checkEntityPostEffect(warping ? new EnderMan(EntityType.ENDERMAN, minecraft.level) : minecraft.getCameraEntity());
		warpingVision = warping;
	}

	// An empty lectern is forgotten so its next book is asked about at once, otherwise asks again every five seconds
	public static boolean isWarpBookOnLectern(BlockPos pos, boolean hasBook)
	{
		Level level = Minecraft.getInstance().level;
		if (level != lecternLevel)
		{
			warpBookLecterns.clear();
			lecternQueries.clear();
			lecternLevel = level;
		}
		if (!hasBook)
		{
			warpBookLecterns.remove(pos);
			lecternQueries.remove(pos);
			return false;
		}
		Long asked = lecternQueries.get(pos);
		if (level != null && (asked == null || level.getGameTime() - asked > 100))
		{
			lecternQueries.put(pos.immutable(), level.getGameTime());
			NetworkEngine.sendToServer(new C2SLecternBookPacket(pos.immutable()));
		}
		return warpBookLecterns.contains(pos);
	}

	public static void setLecternBook(BlockPos pos, boolean warpBook)
	{
		if (warpBook)
			warpBookLecterns.add(pos);
		else
			warpBookLecterns.remove(pos);
	}
}
