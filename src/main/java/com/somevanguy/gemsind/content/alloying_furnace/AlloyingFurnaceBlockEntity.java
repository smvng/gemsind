package com.somevanguy.gemsind.content.alloying_furnace;

import com.somevanguy.gemsind.content.*;
import com.somevanguy.gemsind.content.base.BaseTransmutatorBlockEntity;
import com.somevanguy.gemsind.content.items.AlloyItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;

public class AlloyingFurnaceBlockEntity extends BaseTransmutatorBlockEntity {
    public int fuelLeft = 0;
    public int maxFuelLeft = 1;
    public int progress = 0;
    public int maxProgress = 0;
    public int tickSoundCooldown = 0;
    public boolean isWorking = false;
    protected final ContainerData data;

    public AlloyingFurnaceBlockEntity(BlockPos pos, BlockState blockState) {
        super(AllBlockEntities.ALLOYING_FURNACE_ENTITY.get(), pos, blockState);
        setItemHandler(new AlloyingFurnaceItemHandler(this, 10));
        setAutomationItemHandler(new AlloyingFurnaceItemHandler.Automation((AlloyingFurnaceItemHandler) getItemHandler()));
        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    case 2 -> fuelLeft;
                    case 3 -> maxFuelLeft;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0: progress = value;
                    case 1: maxProgress = value;
                    case 2: fuelLeft = value;
                    case 3: maxFuelLeft = value;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (fuelLeft > 0) {
            fuelLeft -= 1;
            if (!isWorking) {
                isWorking = true;
                level.setBlockAndUpdate(pos, state.setValue(AlloyingFurnaceBlock.ACTIVE, true));
            }
            tickSoundCooldown -= 1;
            if (tickSoundCooldown <= 0) {
                tickSoundCooldown = 70;
                level.playSound(null, getBlockPos(), AllSoundEvents.ALLOYING_FURNACE_AMBIENT.get(), SoundSource.BLOCKS);
            }
            tryCraft(level, pos, state);
        } else {
            if (!tryRegainFuel(level, pos, state)) {
                drainProgress();
                if (isWorking) {
                    isWorking = false;
                    level.setBlockAndUpdate(pos, state.setValue(AlloyingFurnaceBlock.ACTIVE, false));
                    level.playSound(null, getBlockPos(), AllSoundEvents.ALLOYING_FURNACE_FINISH.get(), SoundSource.BLOCKS);
                }
            }
        }
    }

    private void drainProgress() {
        if (progress > 0) {
            progress = Mth.clamp(progress-5, 0, maxProgress);
        }
    }

    public int getFluxCount() {
        int fluxCount = 0;
        for (int i = 6; AlloyingFurnaceItemHandler.isFluxSlot(i); i++) {
            if (itemHandler.getStackInSlot(i).is(AllTags.Items.FLUX)) {
                fluxCount += 1;
            }
        }
        return fluxCount;
    }

    private boolean canCraft(Level level) {
        Optional<RecipeHolder<AlloyingRecipe>> currentRecipe = getRecipe(level);
        if (currentRecipe.isEmpty()) {
            return false;
        }
        if (currentRecipe.get().value().minFluxCount() > getFluxCount()) {
            return false;
        }
        return itemHandler.getStackInSlot(AlloyingFurnaceItemHandler.ALLOY_SLOT).isEmpty();
    }

    private Optional<RecipeHolder<AlloyingRecipe>> getRecipe(Level level) {
        var serverLevel = (ServerLevel) level;
        NonNullList<ItemStack> providedItems = NonNullList.create();
        for (int i = 2; AlloyingFurnaceItemHandler.isElementSlot(i); i++) {
            var stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                providedItems.add(stack);
            }
        }
        return serverLevel.getRecipeManager().getRecipeFor(AllRecipes.ALLOYING_TYPE.get(),
                new AlloyingRecipeInput(providedItems, getFluxCount()), level);
    }

    public void tryCraft(Level level, BlockPos pos, BlockState state) {
        if (canCraft(level)) {
            var recipe = getRecipe(level);
            if (recipe.isPresent()) {
                maxProgress = recipe.get().value().craftingTime();
                progress += 1;
                if (progress >= maxProgress) {
                    progress = 0;
                    maxProgress = 0;
                    ItemStack result = recipe.get().value().result().copy();
                    var elementsAndFluxes = new ArrayList<ItemStack>();
                    for (int i = 2; AlloyingFurnaceItemHandler.isElementSlot(i); i++) {
                        var stack = itemHandler.getStackInSlot(i).copy();
                        if (!stack.isEmpty()) {
                            elementsAndFluxes.add(stack);
                            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                        }
                    }
                    for (int i = 6; AlloyingFurnaceItemHandler.isFluxSlot(i); i++) {
                        var stack = itemHandler.getStackInSlot(i).copy();
                        if (!stack.isEmpty()) {
                            elementsAndFluxes.add(stack);
                            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                        }
                    }
                    int bonusValue = AlloyItem.getBonusValue(result, elementsAndFluxes);
                    result.set(AllDataComponents.GEM_BONUS_VALUE, bonusValue);
                    itemHandler.setStackInSlot(AlloyingFurnaceItemHandler.ALLOY_SLOT, result);
                }
            }
        } else {
            drainProgress();
        }
    }

    public boolean tryRegainFuel(Level level, BlockPos pos, BlockState state) {
        var fuelStack = itemHandler.getStackInSlot(AlloyingFurnaceItemHandler.FUEL_SLOT);
        if (fuelStack.getBurnTime(null) > 0) {
            if (canCraft(level)) {
                var burnTime = (int)(fuelStack.getBurnTime(null) * 0.05);
                maxFuelLeft = burnTime;
                fuelLeft = burnTime;
                var returnStack = fuelStack.getCraftingRemainingItem().copy();
                fuelStack.shrink(1);
                if (fuelStack.isEmpty() && !returnStack.isEmpty()) {
                    itemHandler.setStackInSlot(AlloyingFurnaceItemHandler.FUEL_SLOT, returnStack);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("progress", progress);
        tag.putInt("maxProgress", maxProgress);
        tag.putInt("fuelLeft", fuelLeft);
        tag.putInt("maxFuelLeft", maxFuelLeft);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        progress = tag.getInt("progress");
        maxProgress = tag.getInt("maxProgress");
        fuelLeft = tag.getInt("fuelLeft");
        maxFuelLeft = tag.getInt("maxFuelLeft");
    }

    @Override
    public Component getDisplayName() {
        return getDisplayName("block.gemsind.alloying_furnace");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new AlloyingFurnaceMenu(i, inventory, this, this.data);
    }
}
