package com.somevanguy.gemsind.content.centrifuge;

import com.somevanguy.gemsind.content.AllBlockEntities;
import com.somevanguy.gemsind.content.AllSoundEvents;
import com.somevanguy.gemsind.content.base.BaseTransmutatorEntity;
import com.somevanguy.gemsind.content.AllDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CentrifugeEntity extends BaseTransmutatorEntity {
    public Map<Integer, Integer> progresses = new HashMap<>();
    public static final int TICKS_UNTIL_DONE = 25 * 20;
    public int fuelLeft = 0;
    public int maxFuelLeft = 1;
    public int tickSoundCooldown = 0;
    public boolean isWorking = false;
    protected final ContainerData data;

    public CentrifugeEntity(BlockPos pos, BlockState blockState) {
        super(AllBlockEntities.CENTRIFUGE_ENTITY.get(), pos, blockState);
        setItemHandler(new CentrifugeItemHandler(this, 10)); // for the player
        setAutomationItemHandler(new CentrifugeItemHandler.Automation((CentrifugeItemHandler) getItemHandler())); // for hoppers/automation mods
        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> fuelLeft;
                    case 1 -> maxFuelLeft;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0: fuelLeft = value;
                    case 1: maxFuelLeft = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (fuelLeft > 0) {
            fuelLeft -= 1;
            if (!isWorking) {
                isWorking = true;
                level.setBlockAndUpdate(pos, state.setValue(CentrifugeBlock.SPINNING, true));
            }
            tickSoundCooldown -= 1;
            if (tickSoundCooldown <= 0) {
                tickSoundCooldown = 10;
                level.playSound(null, getBlockPos(), AllSoundEvents.CENTRIFUGE_AMBIENT.get(), SoundSource.BLOCKS);
            }
            tryProgressContents(level, pos, state);
            setChanged();
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
        } else {
            if (!tryRegainFuel(pos, state)) {
                if (isWorking) {
                    isWorking = false;
                    level.setBlockAndUpdate(pos, state.setValue(CentrifugeBlock.SPINNING, false));
                    level.playSound(null, getBlockPos(), AllSoundEvents.CENTRIFUGE_FINISH.get(), SoundSource.BLOCKS);
                }
            }
        }
    }

    public void tryProgressContents(Level level, BlockPos pos, BlockState state) {
        for (int i = 1; i < itemHandler.getSlots(); i++) {
            var stack = itemHandler.getStackInSlot(i);
            if (stack.isEmpty()) {
                progresses.put(i, 0);
            } else {
                var used = stack.getOrDefault(AllDataComponents.CENTRIFUGE_USED, false);
                if (used) {
                    continue;
                }
                var progress = progresses.getOrDefault(i, 0);
                if (progress >= TICKS_UNTIL_DONE) {
                    var purity = stack.getOrDefault(AllDataComponents.PURITY, 0f);
                    if (purity < 1f) {
                        purity = Math.min(1f, purity + 0.45f);
                    }
                    stack.set(AllDataComponents.CENTRIFUGE_USED, true);
                    stack.set(AllDataComponents.PURITY, purity);
                    progresses.put(i, 0);
                    continue;
                }
                progresses.put(i, progress + 1);
            }
        }
    }

    public boolean tryRegainFuel(BlockPos pos, BlockState state) {
        var fuelStack = itemHandler.getStackInSlot(CentrifugeItemHandler.FUEL_SLOT);
        if (fuelStack.getBurnTime(null) > 0) {
            var anyWorkable = false;
            for (int i = 1; i < itemHandler.getSlots(); i++) {
                // accepts false or null
                var stack = itemHandler.getStackInSlot(i);
                if (!stack.isEmpty() && !stack.getOrDefault(AllDataComponents.CENTRIFUGE_USED, false)) {
                    anyWorkable = true;
                    break;
                }
            }
            if (anyWorkable) {
                var burnTime = (int)(fuelStack.getBurnTime(null) * 0.4);
                maxFuelLeft = burnTime;
                fuelLeft = burnTime;
                var returnStack = fuelStack.getCraftingRemainingItem();
                fuelStack.shrink(1);
                if (fuelStack.isEmpty() && !returnStack.isEmpty()) {
                    itemHandler.setStackInSlot(CentrifugeItemHandler.FUEL_SLOT, returnStack);
                }
                setChanged();
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("fuelLeft", fuelLeft);
        tag.putInt("maxFuelLeft", maxFuelLeft);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        fuelLeft = tag.getInt("fuelLeft");
        maxFuelLeft = tag.getInt("maxFuelLeft");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new CentrifugeMenu(i, inventory, this, this.data);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return super.getDisplayName("block.gemsind.centrifuge");
    }
}
