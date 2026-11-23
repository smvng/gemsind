package com.somevanguy.gemsind.content.base;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;

public class BaseItemHandler extends ItemStackHandler {
    public final BlockEntity blockEntity;

    public BaseItemHandler(BlockEntity blockEntity, int slotCount) {
        super(slotCount);
        this.blockEntity = blockEntity;
    }

    @Override
    protected void onContentsChanged(int slot) {
        blockEntity.setChanged();
        if (blockEntity.getLevel() != null) {
            if (!blockEntity.getLevel().isClientSide()) {
                blockEntity.getLevel().sendBlockUpdated(blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
            }
        }
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack newStack, boolean simulate) {
        if (!isItemValid(slot, newStack)) {
            return newStack;
        }
//        var oldStack = getStackInSlot(slot);
//        if (!oldStack.is(newStack.getItem()) && !oldStack.isEmpty()) {
//            return newStack;
//        }
//        if (oldStack.getCount() + newStack.getCount() > getSlotLimit(slot)) {
//            return newStack;
//        }

        return super.insertItem(slot, newStack, simulate);
    }
}
