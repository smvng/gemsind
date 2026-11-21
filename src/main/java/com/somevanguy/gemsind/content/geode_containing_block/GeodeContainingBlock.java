package com.somevanguy.gemsind.content.geode_containing_block;

import com.mojang.serialization.MapCodec;
import com.somevanguy.gemsind.content.AllSoundEvents;
import com.somevanguy.gemsind.content.items.GeodeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class GeodeContainingBlock extends BaseEntityBlock {
    public static final MapCodec<GeodeContainingBlock> CODEC = simpleCodec(GeodeContainingBlock::new);
    public static final IntegerProperty CRACK_STAGE = IntegerProperty.create("crack_stage", 0, 4);

    public GeodeContainingBlock(Properties properties) {
        super(properties.noLootTable().noOcclusion());
    }

    public GeodeContainingBlock() {
        this(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(3f, 4f));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(CRACK_STAGE, 0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CRACK_STAGE);
    }

    public void replaceWith(ServerLevel serverLevel, BlockPos pos, BlockState state, GeodeItem geodeDrop) {
        serverLevel.setBlock(pos,
                this.defaultBlockState(),
                Block.UPDATE_ALL);
        if (serverLevel.getBlockEntity(pos) instanceof GeodeContainingBlockEntity blockEntity) {
            blockEntity.setReplacementState(state);
            blockEntity.setGeodeDrop(geodeDrop);
        }
    }

    public void tryCrack(ServerLevel level, BlockPos pos, BlockState state) {
        int nextStage = state.getValue(CRACK_STAGE) + 1;
        if (nextStage >= 4) {
            if (level.getBlockEntity(pos) instanceof GeodeContainingBlockEntity blockEntity) {
                ItemEntity dropEntity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), blockEntity.getGeodeDrop().getDefaultInstance());
                level.addFreshEntity(dropEntity);
                level.playSound(null, pos, AllSoundEvents.GEODE_CONTAINING_BLOCK_BREAK.get(), SoundSource.BLOCKS);
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            }
            return;
        }
        level.playSound(null, pos, AllSoundEvents.GEODE_CONTAINING_BLOCK_CRACK.get(), SoundSource.BLOCKS);
        level.setBlock(pos, state.setValue(CRACK_STAGE, nextStage), Block.UPDATE_ALL);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new GeodeContainingBlockEntity(blockPos, blockState);
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    @Override
    protected boolean isOcclusionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof GeodeContainingBlockEntity blockEntity) {
            if (blockEntity.getReplacementState() != null) {
                return blockEntity.getReplacementState().canOcclude();
            }
        }
        return false;
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
//        if (level.getBlockEntity(pos) instanceof GeodeContainingBlockEntity blockEntity) {
//            if (blockEntity.getReplacementState() != null) {
//                return blockEntity.getReplacementState().getOcclusionShape(level, pos);
//            }
//        }
        return Shapes.empty();
    }

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {}

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) level;
            if (level.getBlockEntity(pos) instanceof GeodeContainingBlockEntity blockEntity) {
                if (blockEntity.getReplacementState() != null) {
                    BlockParticleOption particleData = new BlockParticleOption(ParticleTypes.BLOCK, blockEntity.getReplacementState());
                    serverLevel.playSound(null, pos, AllSoundEvents.GEODE_CONTAINING_BLOCK_BREAK.get(), SoundSource.BLOCKS);
                    serverLevel.sendParticles(particleData, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 30, 0.3, 0.3, 0.3, 0.15);
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
