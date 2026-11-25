package com.somevanguy.gemsind.content.items;

import com.somevanguy.gemsind.content.geode_containing_block.GeodeContainingBlock;
import com.somevanguy.gemsind.content.geode_containing_block.GeodeContainingBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;

import java.util.List;

public class HammerItem extends Item {
    public HammerItem(int strength, Properties properties) {
        this(true, strength, properties);
    }

    public final boolean usable;
    private final int strength;
    public final int useCooldown = 22;

    public int getStrength() {
        return strength; // TODO:recalculate strength with enchants
    }

    public float getProgressGainAgainst(int geodeToughness) {
        return 0.4f;
    }

    public HammerItem(boolean usable, int strength, Properties properties) {
        super(properties.stacksTo(1));
        this.usable = usable;
        this.strength = strength;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide()) {
            ServerLevel level = (ServerLevel) context.getLevel();
            ServerPlayer player = (ServerPlayer) context.getPlayer();
            BlockPos pos = context.getClickedPos();
            var offHandStack = player.getItemInHand(InteractionHand.OFF_HAND);
            if (offHandStack.getItem() instanceof PickItem pickItem) {
                if (level.getBlockEntity(pos) instanceof GeodeContainingBlockEntity blockEntity) {
                    player.getCooldowns().addCooldown(this, useCooldown);
                    var drop = blockEntity.getGeodeDrop();
                    if (drop != null && drop.toughness <= this.strength && drop.toughness <= pickItem.power) {
                        GeodeContainingBlock geodeContainingBlock = (GeodeContainingBlock) blockEntity.getBlockState().getBlock();
                        geodeContainingBlock.tryCrack(level, pos, blockEntity.getBlockState());
                        return InteractionResult.SUCCESS;
                    } else if (drop != null && drop.toughness > this.strength) {
                        player.sendSystemMessage(Component.translatable("message.gemsind.hammer_not_strong_enough").withStyle(ChatFormatting.DARK_RED), true);
                        return InteractionResult.FAIL;
                    } else {
                        player.sendSystemMessage(Component.translatable("message.gemsind.pick_not_strong_enough").withStyle(ChatFormatting.DARK_RED), true);
                        return InteractionResult.FAIL;
                    }
                }
                var success = pickItem.trySpawnGeode(level, context.getClickedFace().getOpposite(), pos);
                if (success) {
                    player.sendSystemMessage(Component.translatable("message.gemsind.pick_found").withStyle(ChatFormatting.GREEN), true);
                    return InteractionResult.SUCCESS;
                }
                player.sendSystemMessage(Component.translatable("message.gemsind.pick_not_found").withStyle(ChatFormatting.DARK_RED), true);
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (usable) {
            if (Screen.hasShiftDown()) {
                tooltipComponents.add(Component.translatable("item.gemsind.hammer.tooltip").withStyle(ChatFormatting.GRAY));
            } else {
                tooltipComponents.add(Component.translatable("item.gemsind.gem.holdshifttoexpand").withStyle(ChatFormatting.GRAY));
            }
        } else {
            if (Screen.hasShiftDown()) {
                tooltipComponents.add(Component.translatable("item.gemsind.hammer.not_usable_tooltip").withStyle(ChatFormatting.GRAY));
            } else {
                tooltipComponents.add(Component.translatable("item.gemsind.gem.holdshifttoexpand").withStyle(ChatFormatting.GRAY));
            }
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
