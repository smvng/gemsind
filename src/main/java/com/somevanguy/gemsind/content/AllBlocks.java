package com.somevanguy.gemsind.content;

import com.somevanguy.gemsind.GemstoneIndustryMod;
import com.somevanguy.gemsind.content.alloying_furnace.AlloyingFurnaceBlock;
import com.somevanguy.gemsind.content.counter.CounterBlock;
import com.somevanguy.gemsind.content.acid_purifier.AcidPurifierBlock;
import com.somevanguy.gemsind.content.centrifuge.CentrifugeBlock;
import com.somevanguy.gemsind.content.geode_containing_block.GeodeContainingBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class AllBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GemstoneIndustryMod.MODID);


    public static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        return registerBlockWithItemProps(name, new Item.Properties(), block);
    }

    public static <T extends Block> DeferredBlock<T> registerBlockWithItemProps(String name, Item.Properties properties, Supplier<T> block) {
        var finalblock = BLOCKS.register(name, block);
        registerBlockItem(name, properties, finalblock);
        return finalblock;
    }

    public static <T extends Block> void registerBlockItem(String name, Item.Properties properties, DeferredBlock<T> block) {
        GemstoneIndustryMod.LOGGER.info(name);
        AllItems.ITEMS.register(name, () -> new BlockItem(block.get(), properties));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    public static final DeferredBlock<Block> COUNTER = registerBlock("counter", CounterBlock::new);

    public static final DeferredBlock<AcidPurifierBlock> ACID_PURIFIER = registerBlock("acid_purifier", AcidPurifierBlock::new);
    public static final DeferredBlock<CentrifugeBlock> CENTRIFUGE = registerBlock("centrifuge", CentrifugeBlock::new);
    public static final DeferredBlock<Block> TUMBLER = registerBlock("tumbler", () -> new Block(BlockBehaviour.Properties.of()));
    public static final DeferredBlock<AlloyingFurnaceBlock> ALLOYING_FURNACE = registerBlock("alloying_furnace", AlloyingFurnaceBlock::new);

    public static final DeferredBlock<GeodeContainingBlock> GEODE_CONTAINING_BLOCK = registerBlock("geode_containing_block", GeodeContainingBlock::new);
}