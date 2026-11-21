package com.somevanguy.gemsind.content.acid_purifier;

import com.somevanguy.gemsind.content.AllBlockEntities;
import com.somevanguy.gemsind.content.AllSoundEvents;
import com.somevanguy.gemsind.content.base.BaseTransmutatorBlockEntity;
import com.somevanguy.gemsind.content.items.AcidPackItem;
import com.somevanguy.gemsind.content.AllDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

public class AcidPurifierEntity extends BaseTransmutatorBlockEntity {
    public Map<Integer, Integer> progresses = new HashMap<>();
    public static final int ticksUntilDone = 35 * 20;
    public @Nullable AcidPackItem acidContained = null;
    public int acidLeft = 0;
    public final int maxAcidLeft = (int) (6.0 * 20);
    public int tickSoundCooldown = 0;
    public boolean isWorking = false;
    protected final ContainerData data;
    public static final int dataSlotCount = 5;

    public OptionalInt getAcidStrength() {
        if (acidContained != null) {
            return OptionalInt.of(acidContained.acidStrength);
        }
        return OptionalInt.empty();
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
                    case 1 -> maxAcidLeft;
                    case 2 -> acidContained != null ? acidContained.r : 0;
                    case 3 -> acidContained != null ? acidContained.g : 0;
                    case 4 -> acidContained != null ? acidContained.b : 0;
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
                return dataSlotCount;
            }
        };
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (acidLeft > 0) {
            if (tryProgressContents(level, pos, state)) {
                acidLeft -= 1;
                if (!isWorking) {
                    isWorking = true;
                    level.setBlockAndUpdate(pos, state.setValue(AcidPurifierBlock.HAS_ACID, true));
                }
                tickSoundCooldown -= 1;
                if (tickSoundCooldown <= 0) {
                    tickSoundCooldown = 60;
                    level.playSound(null, getBlockPos(), AllSoundEvents.ACID_PURIFIER_AMBIENT.get(), SoundSource.BLOCKS, 0.2f, 0.2f);
                }
            }
        }
        if (acidLeft < maxAcidLeft) {
            tryRegainAcid(pos, state);
        }
        if (acidLeft <= 0) {
            if (isWorking) {
                isWorking = false;
                level.setBlockAndUpdate(pos, state.setValue(AcidPurifierBlock.HAS_ACID, false));
                //level.playSound(null, getBlockPos(), SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 2.5f, 0.15f);
            }
        }
    }

    public boolean tryProgressContents(Level level, BlockPos pos, BlockState state) {
        boolean anyWorkable = false;
        if (itemHandler == null) {
            return false;
        }
        for (int i = 1; i < (itemHandler.getSlots()); i++) {
            var stack = itemHandler.getStackInSlot(i);
            if (stack.isEmpty()) {
                progresses.put(i, 0);
            } else {
                var used = stack.getOrDefault(AllDataComponents.ACID_USED, false);
                if (used) {
                    continue;
                }
                var progress = progresses.getOrDefault(i, 0);
                if (progress >= ticksUntilDone) {
                    float purity = stack.getOrDefault(AllDataComponents.GEM_PURITY, 0f);
                    if (purity < 1f) {
                        purity = Math.min(1f, purity + 0.45f);
                    }
                    stack.set(AllDataComponents.ACID_USED, true);
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
        var acidStack = itemHandler != null ? itemHandler.getStackInSlot(AcidPurifierItemHandler.ACID_SLOT) : ItemStack.EMPTY;
        var acidGain = AcidPurifierItemHandler.acidValue(acidStack);
        if (acidGain > 0) {
            if (acidLeft <= 0 || acidStack.is(acidContained)) {
                acidLeft = Mth.clamp(acidLeft + acidGain, 0, maxAcidLeft);
                var returnStack = acidStack.getCraftingRemainingItem();
                acidStack.shrink(1);
                if (acidStack.isEmpty() && !returnStack.isEmpty()) {
                    itemHandler.setStackInSlot(AcidPurifierItemHandler.ACID_SLOT, returnStack);
                }
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
        tag.putString("acidType",
                acidContained != null ? BuiltInRegistries.ITEM.getKey(acidContained).toString() : "minecraft:air"
        );
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        acidLeft = tag.getInt("acidLeft");
        var containedItem = BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(tag.getString("acidType")));
        if (containedItem.isPresent()) {
            if (containedItem.get() instanceof AcidPackItem acidPackItem) {
                acidContained = acidPackItem;
            }
        }
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new AcidPurifierMenu(i, inventory, this, this.data);
    }
}
