package com.somevanguy.gemsind.datagen.loot_tables;

import com.somevanguy.gemsind.content.AllBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class DataBlockLootTableProvider extends BlockLootSubProvider {
    public DataBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    public void generate() {
        dropSelf(AllBlocks.COUNTER.get());
        dropSelf(AllBlocks.ACID_PURIFIER.get());
        dropSelf(AllBlocks.CENTRIFUGE.get());
        dropSelf(AllBlocks.TUMBLER.get());
        dropSelf(AllBlocks.ALLOYING_FURNACE.get());
    }

    @Override
    public Iterable<Block> getKnownBlocks() {
        return AllBlocks.BLOCKS.getEntries().stream().map(holder -> (Block)holder.get())::iterator;
    }
}
