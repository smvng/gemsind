package com.somevanguy.gemsind.content.base;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseTransmutatorEntity extends BaseItemHandlerEntity implements MenuProvider {
    public BaseTransmutatorEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public Component getDisplayName(String key) {
        return Component.translatable(key);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {

    }
}
