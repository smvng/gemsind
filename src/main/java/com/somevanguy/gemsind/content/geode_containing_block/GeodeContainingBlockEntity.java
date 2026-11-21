package com.somevanguy.gemsind.content.geode_containing_block;

import com.somevanguy.gemsind.GemstoneIndustryMod;
import com.somevanguy.gemsind.content.AllBlockEntities;
import com.somevanguy.gemsind.content.AllItems;
import com.somevanguy.gemsind.content.items.GeodeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GeodeContainingBlockEntity extends BlockEntity implements Nameable {
    public static final ModelProperty<BlockState> REPLACEMENT_STATE = new ModelProperty<>();
    private BlockState replacementState = Blocks.JIGSAW.defaultBlockState();
    public GeodeItem geodeDrop = AllItems.RIVER_GEODE.get();

    public GeodeContainingBlockEntity(BlockPos pos, BlockState blockState) {
        super(AllBlockEntities.GEODE_CONTAINING_BLOCK_ENTITY.get(), pos, blockState);
    }

    public BlockState getReplacementState() {
        return replacementState;
    }

    public void setReplacementState(BlockState replacementState) {
        this.replacementState = replacementState;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public GeodeItem getGeodeDrop() {
        return geodeDrop;
    }

    public void setGeodeDrop(GeodeItem geodeDrop) {
        this.geodeDrop = geodeDrop;
    }

    @Override
    public ModelData getModelData() {
        if (replacementState == null) {
            return ModelData.EMPTY;
        }
        return ModelData.builder().with(REPLACEMENT_STATE, replacementState).build();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
        requestModelDataUpdate();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public @NotNull Component getName() {
        return replacementState.getBlock().getName();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        BlockState.CODEC.encodeStart(NbtOps.INSTANCE, replacementState)
                .resultOrPartial(tagError -> {})
                .ifPresent(stateTag -> tag.put("replacement", stateTag));
        tag.putString("geodeDrop", BuiltInRegistries.ITEM.getKey(geodeDrop).toString());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("replacement")) {
            var replacementTag = tag.get("replacement");
            BlockState.CODEC.parse(NbtOps.INSTANCE, replacementTag)
                    .resultOrPartial(tagError -> {})
                    .ifPresent(this::setReplacementState);
        }
        if (tag.contains("geodeDrop")) {
            var geodeDropTagPath = tag.getString("geodeDrop");
            var geodeDrop = BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(geodeDropTagPath));
            geodeDrop.ifPresent(item -> {
                if (item instanceof GeodeItem geodeItem) {
                    setGeodeDrop(geodeItem);
                }
            });
        }
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}
