package com.somevanguy.gemsind.content.counter;

import com.somevanguy.gemsind.content.AllBlockEntities;
import com.somevanguy.gemsind.content.base.BaseItemHandlerBlockEntity;
import com.somevanguy.gemsind.content.AllDataComponents;
import com.somevanguy.gemsind.content.items.GeodeItem;
import com.somevanguy.gemsind.content.items.HammerItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CounterBlockEntity extends BaseItemHandlerBlockEntity {
    public CounterBlockEntity(BlockPos pos, BlockState blockState) {
        super(AllBlockEntities.COUNTER_ENTITY.get(), pos, blockState);
        setItemHandler(new CounterItemHandler(this)); // for the player
        setAutomationItemHandler(new CounterItemHandler.Automation(getItemHandler()));
    }

    public int cooldown = 0;

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (cooldown > 0) {
            cooldown -= 1;
        }
    }

    public ItemInteractionResult trySwapItem(ItemStack newStack, InteractionHand hand, Player player) {
        if (itemHandler != null) {
            if (!level.isClientSide()) {
                var newStackCopy = newStack.copy();
                var oldCount = newStackCopy.getCount();
                newStackCopy.setCount(1);
                ItemStack oldStack = itemHandler.getStackInSlot(0).copy();
                if (newStackCopy.isEmpty() && oldStack.isEmpty()) {
                    return ItemInteractionResult.FAIL;
                }
                if (newStackCopy.getItem() instanceof HammerItem && oldStack.getItem() instanceof GeodeItem) {
                    return ItemInteractionResult.FAIL; // prevent accidentally swapping between them
                }
                itemHandler.setStackInSlot(0, newStackCopy);
                if (oldCount == 1) {
                    player.setItemInHand(hand, oldStack);
                } else {
                    var returning = newStack.copy();
                    returning.setCount(oldCount-1);
                    player.setItemInHand(hand, returning);
                    if (!player.addItem(oldStack)) {
                        ItemEntity itemEntity = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), oldStack);
                    }
                }
                level.playSound(null, getBlockPos(), SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1f, 0.8f);
            }
            return ItemInteractionResult.CONSUME;
        }
        return ItemInteractionResult.FAIL;
    }

    public void tryCrack(ItemStack usedHammer, @Nullable ServerPlayer player) {
        if (cooldown > 0) {
            return;
        }
        if (itemHandler != null && level != null) {
            cooldown = 8;
            ItemStack storedGeode = itemHandler.getStackInSlot(0);
            if (!storedGeode.isEmpty()) {
                if (usedHammer.getItem() instanceof HammerItem hammerItem && storedGeode.getItem() instanceof GeodeItem geodeItem) {
                    if (!hammerItem.usable) {
                        usedHammer.hurtAndBreak(Integer.MAX_VALUE, (ServerLevel) level, null, item -> {
                            level.playSound(null, getBlockPos(), SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.BLOCKS, 2f, 1.5f);
                        });
                        return;
                    }
                    if (hammerItem.getStrength() >= geodeItem.toughness) {
                        var progress = storedGeode.getOrDefault(AllDataComponents.GEODE_CRACK_PROGRESS, 0f);
                        progress += hammerItem.getProgressGainAgainst(geodeItem.toughness);
                        if (progress >= 1f) {
                            ItemStack drop = geodeItem.getDrop(storedGeode, (ServerLevel) level, player);
                            if (drop.isEmpty()) {
                                return;
                            }
                            itemHandler.setStackInSlot(0, drop);
                        } else {
                            storedGeode.set(AllDataComponents.GEODE_CRACK_PROGRESS, progress);
                        }
                        player.getCooldowns().addCooldown(hammerItem, hammerItem.useCooldown);
                        usedHammer.hurtAndBreak(1, (ServerLevel) level, player, item -> {});
                        level.playSound(null, getBlockPos(), SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 0.9f, 1.5f);
                        RandomSource random = level.getRandom();
                        Vec3 particleOrigin = getBlockPos().getCenter().add(0,0.9f,0);
                        ((ServerLevel) level).sendParticles(ParticleTypes.CLOUD, particleOrigin.x, particleOrigin.y, particleOrigin.z, 5, 0.35, 0,0.35, 0.0);
                    } else {
                        player.sendSystemMessage(Component.translatable("message.gemsind.hammer_not_strong_enough").withStyle(ChatFormatting.RED), true);
                    }
                }
            }
        }
    }
}
