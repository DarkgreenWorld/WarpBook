/**
 * @author MelonVRneu, ArcAnc, DarkgreenWorld, Panicnot42, FerreusVeritas
 * Created at: 07.08.2024
 * Copyright (c) 2024-2026
 * <p>
 * This code is licensed under "GPL-v3.0-only"
 * Details can be found in the license file in the root folder of this project
 */
package com.panicnot42.warp_book.content.savedData;

import com.panicnot42.warp_book.Database;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DeathSavedData extends SavedData
{
    public final Object2ObjectLinkedOpenHashMap<UUID, GlobalPos> POSITIONS = new Object2ObjectLinkedOpenHashMap<>();

    public DeathSavedData()
    {
        setDirty();
    }

    public static @NotNull DeathSavedData getInstance()
    {
       MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
       if (server == null)
           return new DeathSavedData();

       ServerLevel level = server.getLevel(Level.OVERWORLD);
       if (level == null)
           return new DeathSavedData();
       return level.getDataStorage().computeIfAbsent(new Factory<>(
               DeathSavedData::new,
               (compoundTag, provider) -> DeathSavedData.load(compoundTag)), Database.SAVED_DATA_FILE_NAME);
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries)
    {
        ListTag listTag = new ListTag();

        POSITIONS.forEach((uuid, globalPos) ->
        {
            CompoundTag compoundTag = new CompoundTag();
            GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, globalPos).ifSuccess(savedTag -> compoundTag.put(Database.SAVED_DATA_POSITION, savedTag));
            UUIDUtil.CODEC.encodeStart(NbtOps.INSTANCE, uuid).ifSuccess(savedTag -> compoundTag.put(Database.SAVED_DATA_UUID, savedTag));
            listTag.add(compoundTag);
        });

        tag.put(Database.SAVED_DATA_INFO_LIST, listTag);
        return tag;
    }

    public static @NotNull DeathSavedData load(@NotNull CompoundTag tag)
    {
        DeathSavedData data = new DeathSavedData();

        ListTag listTag = tag.getList(Database.SAVED_DATA_INFO_LIST, 10);
        listTag.forEach(dynTag ->
        {
            if (dynTag instanceof CompoundTag compoundTag)
            {
                GlobalPos pos = GlobalPos.CODEC.parse(NbtOps.INSTANCE, compoundTag.get(Database.SAVED_DATA_POSITION)).
                        result().
                        orElse(null);
                UUID uuid = UUIDUtil.CODEC.parse(NbtOps.INSTANCE, compoundTag.get(Database.SAVED_DATA_UUID)).
                        result().
                        orElse(null);

                if (pos != null && uuid != null)
                    data.POSITIONS.putIfAbsent(uuid, pos);
            }
        });
        data.setDirty();
        return data;
    }

    public void removeDeath(@NotNull UUID uuid)
    {
        if (POSITIONS.remove(uuid) != POSITIONS.defaultReturnValue())
        {
            POSITIONS.trim();
            setDirty();
        }
    }
}
