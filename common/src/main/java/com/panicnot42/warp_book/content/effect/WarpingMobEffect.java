/**
 * @author DarkgreenWorld, Claude Opus 5.5
 * Created at: 2026.10.10
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.effect;

import com.panicnot42.warp_book.content.core.WarpColors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

// The inverted view is client side, see ClientHooks.tickWarpingVision
public class WarpingMobEffect extends MobEffect
{
	public WarpingMobEffect()
	{
		super(MobEffectCategory.HARMFUL, WarpColors.POTION.getColor());
	}

	// The view distortion is vanilla nausea, hidden and lasting as long as this effect
	@Override
	public void onEffectStarted(@NotNull LivingEntity entity, int amplifier)
	{
		MobEffectInstance warping = entity.getEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(this));
		if (!entity.level().isClientSide() && warping != null)
			entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, warping.getDuration(), 0, true, false, false));
	}

	// Same rhythm as poison
	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier)
	{
		int interval = 50 >> amplifier;
		return interval == 0 || duration % interval == 0;
	}

	@Override
	public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier)
	{
		if (!(entity.level() instanceof ServerLevel level))
			return true;
		if (entity.getHealth() > 1.0F)
			entity.hurt(entity.damageSources().magic(), 1.0F);
		if (entity.getRandom().nextInt(2) == 0)
			teleportRandomly(entity, level);
		return true;
	}

	// Chorus fruit with twice the range
	private static void teleportRandomly(LivingEntity entity, ServerLevel level)
	{
		for (int i = 0; i < 16; i++)
		{
			double x = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * 32.0;
			double y = Mth.clamp(entity.getY() + (entity.getRandom().nextInt(32) - 16), level.getMinBuildHeight(), level.getMinBuildHeight() + level.getLogicalHeight() - 1);
			double z = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * 32.0;
			if (entity.isPassenger())
				entity.stopRiding();
			Vec3 from = entity.position();
			if (entity.randomTeleport(x, y, z, true))
			{
				level.gameEvent(GameEvent.TELEPORT, from, GameEvent.Context.of(entity));
				level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS);
				entity.resetFallDistance();
				return;
			}
		}
	}
}
