package com.somevanguy.gemsind.content.alloying_furnace;

import com.somevanguy.gemsind.content.AllTags;
import com.somevanguy.gemsind.content.base.BaseItemHandler;
import com.somevanguy.gemsind.content.items.AnthraciteItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;

public class AlloyingFurnaceItemHandler extends BaseItemHandler {
    public static final int FUEL_SLOT = 0;
    public static final int ALLOY_SLOT = 1;

    public static boolean isElementSlot(int i) {
        return i > 1 && i <= 5;
    }

    public static boolean isFluxSlot(int i) {
        return i > 5 && i <= 9;
    }

    public static int getBurnValue(ItemStack stack) {
        return stack.getItem() instanceof AnthraciteItem fuelItem ? fuelItem.ALLOYING_BURN_TIME : 0;
    }

    public AlloyingFurnaceItemHandler(BlockEntity blockEntity, int slotCount) {
        super(blockEntity, slotCount);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (slot == ALLOY_SLOT && stack.is(AllTags.Items.ALLOY)) {
            return true;
        }
        if (slot == FUEL_SLOT && getBurnValue(stack) > 0) {
            return true;
        }
        if (isFluxSlot(slot) && stack.is(AllTags.Items.FLUX)) {
            return true;
        }
        if (isElementSlot(slot) && stack.is(AllTags.Items.GEMSTONE)) {
            return true;
        }
        return false;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack newStack, boolean simulate) {
        if (slot == ALLOY_SLOT) {
            return newStack;
        }
        return super.insertItem(slot, newStack, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        if (slot != FUEL_SLOT && slot != ALLOY_SLOT) {
            return 1;
        }
        return super.getSlotLimit(slot);
    }

    public record Automation(AlloyingFurnaceItemHandler mainHandler) implements IItemHandler {
        @Override
        public int getSlots() {
            return mainHandler.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int i) {
            return mainHandler.getStackInSlot(i);
        }

        @Override
        public ItemStack insertItem(int i, ItemStack itemStack, boolean b) {
            return mainHandler.insertItem(i, itemStack, b);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean b) {
            if (slot == FUEL_SLOT && getBurnValue(mainHandler.getStackInSlot(slot)) > 0) {
                return ItemStack.EMPTY;
            }
            if (isElementSlot(slot)) {
                return ItemStack.EMPTY;
            }
            if (isFluxSlot(slot)) {
                return ItemStack.EMPTY;
            }
            return mainHandler.extractItem(slot, amount, b);
        }

        @Override
        public int getSlotLimit(int i) {
            return mainHandler.getSlotLimit(i);
        }

        @Override
        public boolean isItemValid(int i, ItemStack itemStack) {
            return mainHandler.isItemValid(i, itemStack);
        }
    }
}
