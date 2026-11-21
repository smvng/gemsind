package com.somevanguy.gemsind.content.counter;

import com.mojang.serialization.MapCodec;
import com.somevanguy.gemsind.content.AllBlockEntities;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CounterBlock extends BaseEntityBlock {
    public static final MapCodec<CounterBlock> CODEC = simpleCodec(CounterBlock::new);

    public CounterBlock(Properties properties) {
        super(properties);
    }

    public CounterBlock() {
        this(BlockBehaviour.Properties.of()
                .strength(1.5f, 6f)
                .sound(SoundType.STONE)
                .mapColor(MapColor.STONE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops());
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CounterEntity(blockPos, blockState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("block.gemsind.counter.tooltip"));
        } else {
            tooltipComponents.add(Component.translatable("item.gemsind.gem.holdshifttoexpand"));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof CounterEntity blockEntity) {
            return blockEntity.trySwapItem(stack, InteractionHand.MAIN_HAND, player);
        }
        return ItemInteractionResult.FAIL;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }

        return createTickerHelper(blockEntityType, AllBlockEntities.COUNTER_ENTITY.get(),
                ((level1, blockPos, blockState, blockEntity) -> blockEntity.tick(level1, blockPos, blockState)));
    }

    //    @Override
//    protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
//        if (level.getBlockEntity(pos) instanceof CounterEntity blockEntity) {
//            if (!level.isClientSide()) {
//                blockEntity.onAttack((ServerPlayer) player);
//            }
//        }
//        super.attack(state, level, pos, player);
//    }

    public void onHammerHit(BlockPos pos, ItemStack hammer, ServerLevel level, ServerPlayer player) {
        if (level.getBlockEntity(pos) instanceof CounterEntity blockEntity) {
            if (!level.isClientSide()) {
                blockEntity.tryCrack(hammer, player);
            }
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof CounterEntity counterBlockEntity) {
                counterBlockEntity.dropContents();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
