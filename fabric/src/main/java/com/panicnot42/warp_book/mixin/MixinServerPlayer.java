/**
 * @author DarkgreenWorld, Claude Opus 5.5
 * Created at: 2026.10.07
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.mixin;

import com.panicnot42.warp_book.util.EventHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Fabric's death events fire too late or too early, the start of die is where the inventory is still intact
@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer
{
	@Inject(method = "die", at = @At("HEAD"))
	private void warp_book$onDeath(DamageSource damageSource, CallbackInfo ci)
	{
		EventHandler.onDeath((ServerPlayer) (Object) this);
	}
}
