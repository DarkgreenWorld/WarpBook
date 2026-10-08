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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

// Everything that touches client-only classes. Callers on the common side only pass vanilla types,
// so a dedicated server never loads Minecraft or any Screen.
public final class ClientHooks
{
	public static void openWarpBookGui(Player player, InteractionHand usedHand)
	{
		Minecraft.getInstance().setScreen(new GuiBook(player, usedHand));
	}

	public static void openWaypointNameGui(Player player, InteractionHand usedHand)
	{
		Minecraft.getInstance().setScreen(new GuiWaypointName(player, usedHand));
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
