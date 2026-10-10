/**
 * @author Panicnot42, FerreusVeritas, MelonVRneu, ArcAnc, DarkgreenWorld
 * Copyright (c) 2014-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.client;

import com.panicnot42.warp_book.content.gui.GuiBook;
import com.panicnot42.warp_book.content.gui.GuiWaypointName;
import com.panicnot42.warp_book.registration.Registration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import com.panicnot42.warp_book.content.network.NetworkEngine;
import com.panicnot42.warp_book.content.network.packet.C2SLecternBookPacket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Everything that touches client only classes, so a dedicated server never loads Minecraft or any Screen
public final class ClientHooks
{
	private static boolean warpingVision;
	private static final Set<BlockPos> warpBookLecterns = new HashSet<>();
	private static final Map<BlockPos, Long> lecternQueries = new HashMap<>();
	private static @Nullable Level lecternLevel;

	// Declared as Player so callers never reference the client only LocalPlayer
	public static @Nullable Player getClientPlayer()
	{
		return Minecraft.getInstance().player;
	}

	public static void openWarpBookGui(Player player, InteractionHand usedHand)
	{
		Minecraft.getInstance().setScreen(new GuiBook(player, usedHand));
	}

	public static void openLecternGui(Player player, ItemStack book, BlockPos lecternPos, int page)
	{
		Minecraft.getInstance().setScreen(new GuiBook(player, book, lecternPos, page));
	}

	public static void openWaypointNameGui(Player player, InteractionHand usedHand)
	{
		Minecraft.getInstance().setScreen(new GuiWaypointName(player, usedHand));
	}

	// Called every client tick by each loader, shows the inverted view vanilla uses when spectating an enderman
	public static void tickWarpingVision()
	{
		Minecraft minecraft = Minecraft.getInstance();
		boolean warping = minecraft.player != null && minecraft.player.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(Registration.MobEffectRegistry.WARPING.get()));
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

	public static void playWarpEffect(Player player, boolean enter, int x, int y, int z)
	{
		Level level = player.level();
		RandomSource rand = level.random;
		int particles = (2 - Minecraft.getInstance().options.particles().get().getId()) * 50;
		if (enter)
		{
			for (int i = 0; i < (5 * particles); ++i)
			{
				player.level().addParticle(ParticleTypes.LARGE_SMOKE,
						x,
						y + (rand.nextDouble() * 2),
						z,
						(rand.nextDouble() / 10) - 0.05D,
						0D,
						(rand.nextDouble() / 10) - 0.05D);
			}
			player.level().playSound(player, player.getX(), player.getY(), player.getZ(), Registration.SoundRegistry.ARRIVE.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
		}
		else
		{
			for (int i = 0; i < particles; ++i)
			{
				player.level().addParticle(ParticleTypes.PORTAL,
						x + 0.5D,
						y + (rand.nextDouble() * 2),
						z + 0.5D,
						rand.nextDouble() - 0.5D,
						rand.nextDouble() - 0.5D,
						rand.nextDouble() - 0.5D);
			}
			player.level().playSound(player, player.getX(), player.getY(), player.getZ(), Registration.SoundRegistry.DEPART.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
		}
	}
}
