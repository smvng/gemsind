package com.somevanguy.gemsind.content.acid_purifier;

import com.somevanguy.gemsind.content.base.BaseItemHandler;
import com.somevanguy.gemsind.content.AllDataComponents;
import com.somevanguy.gemsind.content.items.AcidPackItem;
import com.somevanguy.gemsind.content.items.GemItem;
import com.somevanguy.gemsind.content.AllTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class AcidPurifierItemHandler extends BaseItemHandler {
    public static final int ACID_SLOT = 0;

    public static boolean isGemSlot(int i) {
        return i > 0 && i <= 9;
    }

    public AcidPurifierItemHandler(BlockEntity blockEntity, int slotCount) {
        super(blockEntity, slotCount);
    }

//    public static int acidValue(ItemStack stack) {
//        return stack.getOrDefault(AllDataComponents.ACID_GAIN.get(), 0);
//    }

    public static boolean isAcidPack(ItemStack stack) {
        return stack.getItem() instanceof AcidPackItem;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        boolean is_gem = stack.is(AllTags.Items.GEMSTONE);
        boolean is_acid = isAcidPack(stack);
        Boolean used = stack.getOrDefault(AllDataComponents.CENTRIFUGE_USED, false);
        return (slot != ACID_SLOT && is_gem && !is_acid && !used) || (slot == ACID_SLOT && is_acid);
    }

    @Override
    public int getSlotLimit(int slot) {
        if (slot != 0) {
            return 1;
        }
        return super.getSlotLimit(slot);
    }

    // for hoppers
    record Automation(AcidPurifierItemHandler mainHandler) implements IItemHandler {
        @Override
        public int getSlots() {
            return mainHandler.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int i) {
            return mainHandler.getStackInSlot(i);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack itemStack, boolean b) {
            if (slot != ACID_SLOT) {
                if (mainHandler.blockEntity instanceof AcidPurifierEntity acidPurifierBlockEntity && itemStack.getItem() instanceof GemItem gemItem) {
                    if (acidPurifierBlockEntity.acidPackContained != null) {
                        if (acidPurifierBlockEntity.acidPackContained.acidStrength > gemItem.maxAcidTolerance) {
                            return itemStack; // do not insert gems that will be destroyed
                        }
                    }
                }
            }
            return mainHandler.insertItem(slot, itemStack, b);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack stack = getStackInSlot(slot);
            Boolean used = stack.getOrDefault(AllDataComponents.ACID_PURIFIER_USED, false);
            if (slot != ACID_SLOT && !used) {
                // do not extract if its not been used
                return ItemStack.EMPTY;
            }
            if (slot == ACID_SLOT && isAcidPack(stack)) {
                return ItemStack.EMPTY;
            }
            return mainHandler.extractItem(slot, amount, simulate);
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
