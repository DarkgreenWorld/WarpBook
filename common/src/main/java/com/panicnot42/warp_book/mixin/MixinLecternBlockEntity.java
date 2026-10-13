/**
 * @author DarkgreenWorld, Claude Opus 5.5
 * Created at: 2026.10.13
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.mixin;

import com.panicnot42.warp_book.content.item.WarpBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LecternBlockEntity.class)
public abstract class MixinLecternBlockEntity
{
    @Shadow
    public abstract ItemStack getBook();

    @Inject(method = "getPageCount", at = @At("HEAD"), cancellable = true)
    private static void warp_book$countWarpBookPages(ItemStack stack, CallbackInfoReturnable<Integer> cir)
    {
        if (stack.getItem() instanceof WarpBookItem)
            cir.setReturnValue(WarpBookItem.getListPageCount(stack));
    }

    @Inject(method = "hasBook", at = @At("HEAD"), cancellable = true)
    private void warp_book$hasWarpBook(CallbackInfoReturnable<Boolean> cir)
    {
        if (getBook().getItem() instanceof WarpBookItem)
            cir.setReturnValue(true);
    }
}