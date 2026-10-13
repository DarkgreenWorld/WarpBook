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
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LecternBlockEntity.class)
public abstract class MixinLecternBlockEntity
{
    @Shadow
    public abstract ItemStack getBook();

    @Redirect(method = {"setBook(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)V", "load"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/WrittenBookItem;getPageCount(Lnet/minecraft/world/item/ItemStack;)I"))
    private int warp_book$countWarpBookPages(ItemStack stack)
    {
        return stack.getItem() instanceof WarpBookItem ? WarpBookItem.getListPageCount(stack) : WrittenBookItem.getPageCount(stack);
    }

    @Inject(method = "hasBook", at = @At("HEAD"), cancellable = true)
    private void warp_book$hasWarpBook(CallbackInfoReturnable<Boolean> cir)
    {
        if (getBook().getItem() instanceof WarpBookItem)
            cir.setReturnValue(true);
    }
}