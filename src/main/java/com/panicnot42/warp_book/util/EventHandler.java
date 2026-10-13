/**
 * @author ArcAnc, MelonVRneu, DarkgreenWorld, Panicnot42, FerreusVeritas
 * Created at: 07.08.2024
 * Copyright (c) 2024-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.util;

import com.panicnot42.warp_book.Database;
import com.panicnot42.warp_book.content.block.WarpLecternBlockEntity;
import com.panicnot42.warp_book.content.gui.inventory.MenuWarpBook;
import com.panicnot42.warp_book.content.item.WarpBookItem;
import com.panicnot42.warp_book.content.network.NetworkEngine;
import com.panicnot42.warp_book.content.network.packet.S2COpenLecternPacket;
import com.panicnot42.warp_book.content.savedData.DeathSavedData;
import com.panicnot42.warp_book.registration.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// Called by the event listeners in each loader's WarpBook class
public class EventHandler
{
    public static void onDeath(@NotNull final Player player)
    {
        if (!Database.deathPagesEnabled)
            return;

        Level level = player.level();
        if(!level.isClientSide)
        {
            outer:
            for (NonNullList<ItemStack> list : List.of(player.getInventory().items, player.getInventory().offhand))
            {
                for (int q = 0; q < list.size(); q++)
                {
                    ItemStack item = list.get(q);
                    if (item.is(Registration.ItemRegistry.WARP_BOOK.get()) && WarpBookItem.getRespawnsLeft(item) > 0)
                    {

                    	WarpBookItem.decrRespawnsLeft(item);
                    }
                    else if(item.is(Registration.ItemRegistry.WARP_PAGE_ITEM_DEATHLY.get()))
                    {
                    	item.shrink(1);
                    }
                    else continue;

                    DeathSavedData data = DeathSavedData.getInstance(player.getServer());
                    GlobalPos pos = GlobalPos.of(player.level().dimension(),player.blockPosition());
                    data.POSITIONS.put(player.getUUID(),pos);
                    data.setDirty();

                    break outer;
                }
            }
        }
    }

    // Returns true when the click was handled and the vanilla interaction must not run
    public static boolean onUseBlock(@NotNull Player player, @NotNull Level level, @NotNull InteractionHand hand, @NotNull BlockPos pos)
    {
        if (hand != InteractionHand.MAIN_HAND)
            return false;
        // The client cannot see what lies on a vanilla lectern, so it only claims clicks on a warp lectern
        if (level.isClientSide())
            return level.getBlockState(pos).is(Registration.BlockRegistry.WARP_LECTERN.get());

        ItemStack book = WarpLecternBlockEntity.findBook(level, pos);
        if (book.isEmpty() || player.isSpectator() || !(player instanceof ServerPlayer serverPlayer))
            return false;

        BlockEntity lectern = level.getBlockEntity(pos);
        boolean warpLectern = lectern instanceof WarpLecternBlockEntity;
        Component title = warpLectern ? ((WarpLecternBlockEntity) lectern).getName() : book.getHoverName();
        if (!player.isSecondaryUseActive())
            NetworkEngine.sendToPlayer(serverPlayer, new S2COpenLecternPacket(pos, book, warpLectern ? ((WarpLecternBlockEntity) lectern).getPage() : ((LecternBlockEntity) lectern).getPage()));
        // Like vanilla, sneaking with an item in hand on a vanilla lectern uses the item instead
        else if (warpLectern || player.getMainHandItem().isEmpty() && player.getOffhandItem().isEmpty())
            MenuWarpBook.open(serverPlayer, title, book, pos, warpLectern);
        else
            return false;
        return true;
    }

    public static void onPlayerRespawn(@NotNull final Player player, boolean endConquered)
    {
        if (Database.deathPagesEnabled && !endConquered)
        {
            if (!player.level().isClientSide())
            {
                DeathSavedData data = DeathSavedData.getInstance(player.getServer());
                GlobalPos death = data.POSITIONS.get(player.getUUID());
                if(death != null)
                {
                	ItemStack page = new ItemStack(Registration.ItemRegistry.WARP_PAGE_ITEM_LOCATION.get(), 1);
                	WarpUtils.bindItemStackToLocation(page, "Death...", death);
                	if (!player.addItem(page))
                	{
                		EntityType.ITEM.spawn((ServerLevel) player.level(), (CompoundTag) null, itemEntity -> itemEntity.setItem(page), player.getOnPos(), MobSpawnType.EVENT, false, false);
                	}
                	data.removeDeath(player.getGameProfile().getId());
                }
            }
        }
    }
}
