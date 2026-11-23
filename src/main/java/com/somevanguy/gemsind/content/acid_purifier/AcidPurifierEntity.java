package com.somevanguy.gemsind.content.acid_purifier;

import com.somevanguy.gemsind.content.AllBlockEntities;
import com.somevanguy.gemsind.content.AllItems;
import com.somevanguy.gemsind.content.AllSoundEvents;
import com.somevanguy.gemsind.content.base.BaseTransmutatorEntity;
import com.somevanguy.gemsind.content.items.AcidPackItem;
import com.somevanguy.gemsind.content.AllDataComponents;
import com.somevanguy.gemsind.content.items.GemItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("unused")
public class AcidPurifierEntity extends BaseTransmutatorEntity {
    public Map<Integer, Integer> progresses = new HashMap<>();
    public static final int TICKS_UNTIL_DONE = 35 * 20;
    public @Nullable AcidPackItem acidPackContained = null;
    public int acidLeft = 0;
    public static final int MAX_ACID_LEFT = 120 * 20;
    public int tickSoundCooldown = 0;
    //public boolean isWorking = false;
    protected final ContainerData data;
    public static final int DATA_SLOT_COUNT = 5;

    public List<ItemStack> getContained() {
        List<ItemStack> list = List.of();
        for (int i = 1; i < itemHandler.getSlots(); i++) {
            var stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                list.add(stack);
            }
        }
        return list;
    }

    public AcidPurifierEntity(BlockPos pos, BlockState blockState) {
        super(AllBlockEntities.ACID_PURIFIER_ENTITY.get(), pos, blockState);
        setItemHandler(new AcidPurifierItemHandler(this, 10)); // for the player
        setAutomationItemHandler(new AcidPurifierItemHandler.Automation((AcidPurifierItemHandler) getItemHandler())); // for hoppers/automation mods
        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> acidLeft;
                    case 1 -> MAX_ACID_LEFT;
                    case 2 -> acidPackContained != null ? acidPackContained.r : 0;
                    case 3 -> acidPackContained != null ? acidPackContained.g : 0;
                    case 4 -> acidPackContained != null ? acidPackContained.b : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                if (i == 0) {
                    acidLeft = value;
                }
            }

            @Override
            public int getCount() {
                return DATA_SLOT_COUNT;
            }
        };
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (acidLeft > 0) {
            if (tryProgressContents(level, pos, state)) {
                acidLeft -= 1;
                setChanged();
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
//                if (!isWorking) {
//                    isWorking = true;
//                    level.setBlockAndUpdate(pos, state.setValue(AcidPurifierBlock.HAS_ACID, true));
//                }
                tickSoundCooldown -= 1;
                if (tickSoundCooldown <= 0) {
                    tickSoundCooldown = 80;
                    level.playSound(null, getBlockPos(), AllSoundEvents.ACID_PURIFIER_AMBIENT.get(), SoundSource.BLOCKS, 0.2f, 0.2f);
                }
            }
        }
        tryRegainAcid(pos, state);
//        if (acidLeft <= 0) {
//            if (isWorking) {
//                isWorking = false;
//                level.setBlockAndUpdate(pos, state.setValue(AcidPurifierBlock.HAS_ACID, false));
//                //level.playSound(null, getBlockPos(), SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 2.5f, 0.15f);
//            }
//        }
    }

    public boolean tryProgressContents(Level level, BlockPos pos, BlockState state) {
        boolean anyWorkable = false;
        if (itemHandler == null) {
            return false;
        }
        if (acidPackContained == null) {
            return false;
        }
        for (int i = 1; i < (itemHandler.getSlots()); i++) {
            var stack = itemHandler.getStackInSlot(i);
            if (stack.isEmpty()) {
                progresses.put(i, 0);
            } else if (stack.getItem() instanceof GemItem gemItem) {
                if (acidPackContained != null) {
                    if (gemItem.maxAcidTolerance < acidPackContained.acidStrength) {
                        if (level.getRandom().nextFloat() < (float) 1 / 200) {
                            var ashStack = AllItems.ASH.toStack();
                            ashStack.set(AllDataComponents.ACID_PURIFIER_USED, true);
                            itemHandler.setStackInSlot(i, ashStack);
                        }
                        continue;
                    }
                }
                var used = stack.getOrDefault(AllDataComponents.ACID_PURIFIER_USED, false);
                if (used) {
                    continue;
                }
                var progress = progresses.getOrDefault(i, 0);
                if (progress >= TICKS_UNTIL_DONE) {
                    float purity = stack.getOrDefault(AllDataComponents.GEM_PURITY, 0f);
                    if (purity < 1f) {
                        purity = Math.min(1f, purity + 0.55f * acidPackContained.acidStrength);
                    }
                    stack.set(AllDataComponents.ACID_PURIFIER_USED, true);
                    stack.set(AllDataComponents.GEM_PURITY, purity);
                    progresses.put(i, 0);
                    continue;
                }
                progresses.put(i, progress + 1);
                anyWorkable = true;
            }
        }
        return anyWorkable;
    }

    public void tryRegainAcid(BlockPos pos, BlockState state) {
        ItemStack acidStack = itemHandler != null ? itemHandler.getStackInSlot(AcidPurifierItemHandler.ACID_SLOT) : ItemStack.EMPTY;
        if (acidStack.getItem() instanceof AcidPackItem acidPackItem) {
            if (acidLeft <= 0 || !acidStack.is(acidPackContained)) {
                acidLeft = MAX_ACID_LEFT;
                acidPackContained = acidPackItem;
                var returnStack = acidStack.getCraftingRemainingItem();
                acidStack.shrink(1);
                if (acidStack.isEmpty() && !returnStack.isEmpty()) {
                    itemHandler.setStackInSlot(AcidPurifierItemHandler.ACID_SLOT, returnStack);
                }
                setChanged();
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public @NotNull Component getDisplayName() {
        return super.getDisplayName("block.gemsind.acid_purifier");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("acidLeft", acidLeft);
        tag.putString("acidPack",
                acidPackContained != null ? BuiltInRegistries.ITEM.getKey(acidPackContained).toString() : "minecraft:air"
        );
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        acidLeft = tag.getInt("acidLeft");
        var containedItem = BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(tag.getString("acidPack")));
        if (containedItem.isPresent()) {
            if (containedItem.get() instanceof AcidPackItem acidPackItem) {
                acidPackContained = acidPackItem;
            } else {
                acidPackContained = null;
            }
        }
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new AcidPurifierMenu(i, inventory, this, this.data);
    }
}
