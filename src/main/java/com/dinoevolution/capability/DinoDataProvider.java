package com.dinoevolution.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class DinoDataProvider implements ICapabilitySerializable<CompoundTag> {
    private final DinoData data = new DinoData();
    private final LazyOptional<DinoData> optional = LazyOptional.of(() -> data);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return cap == DinoCapabilities.DINO_DATA ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() { return data.save(); }

    @Override
    public void deserializeNBT(CompoundTag nbt) { data.load(nbt); }
}
