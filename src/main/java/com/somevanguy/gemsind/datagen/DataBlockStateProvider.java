package com.somevanguy.gemsind.datagen;

import com.somevanguy.gemsind.GemstoneIndustryMod;
import com.somevanguy.gemsind.content.AllBlocks;
import com.somevanguy.gemsind.content.alloying_furnace.AlloyingFurnaceBlock;
import com.somevanguy.gemsind.content.centrifuge.CentrifugeBlock;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class DataBlockStateProvider extends BlockStateProvider {
    public DataBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, GemstoneIndustryMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        ModelFile counterModel = models().cubeTop("counter",
                modLoc("block/counter_side"),
                modLoc("block/counter_top"));
        simpleBlockWithItem(AllBlocks.COUNTER.get(), counterModel);

        ModelFile centrifugeOffModel = models().cubeTop("centrifuge_off",
                modLoc("block/centrifuge_side"),
                modLoc("block/centrifuge_top"));
        ModelFile centrifugeOnModel = models().cubeTop("centrifuge_on",
                modLoc("block/centrifuge_side"),
                modLoc("block/centrifuge_top_spin"));
        simpleBlockItem(AllBlocks.CENTRIFUGE.get(), centrifugeOffModel);
        getVariantBuilder(AllBlocks.CENTRIFUGE.get()).forAllStates(blockState -> {
            boolean spinning = blockState.getValue(CentrifugeBlock.SPINNING);
            return ConfiguredModel.builder().modelFile(spinning ? centrifugeOnModel : centrifugeOffModel).build();
        });

        ModelFile alloyingFurnaceOffModel = models().cube("alloying_furnace_off",
                modLoc("block/alloying_furnace_side"),
                modLoc("block/alloying_furnace_side"),
                modLoc("block/alloying_furnace_front"),
                modLoc("block/alloying_furnace_side"),
                modLoc("block/alloying_furnace_side"),
                modLoc("block/alloying_furnace_side"))
                .texture("particle", "block/alloying_furnace_front");
        ModelFile alloyingFurnaceOnModel = models().cube("alloying_furnace_on",
                modLoc("block/alloying_furnace_side"),
                modLoc("block/alloying_furnace_side"),
                modLoc("block/alloying_furnace_front_on"),
                modLoc("block/alloying_furnace_side"),
                modLoc("block/alloying_furnace_side"),
                modLoc("block/alloying_furnace_side"))
                .texture("particle", "block/alloying_furnace_front_on");
        simpleBlockItem(AllBlocks.ALLOYING_FURNACE.get(), alloyingFurnaceOffModel);
        getVariantBuilder(AllBlocks.ALLOYING_FURNACE.get()).forAllStates(blockState -> {
            boolean active = blockState.getValue(AlloyingFurnaceBlock.ACTIVE);
            // north = 0
            // east = 90
            // south = 180
            // west = 270

            Direction facing = blockState.getValue(AlloyingFurnaceBlock.FACING);
            return ConfiguredModel.builder().rotationY(
                    switch (facing) {
                        case Direction.NORTH -> 0;
                        case Direction.EAST -> 90;
                        case Direction.SOUTH -> 180;
                        case Direction.WEST -> 270;
                        default -> 0;
                    }
            ).modelFile(active ? alloyingFurnaceOnModel : alloyingFurnaceOffModel).build();
        });

        ModelFile geodeContainingBlockModel = models().cubeAll("geode_containing_block",
                mcLoc("block/debug"));
        simpleBlockWithItem(AllBlocks.GEODE_CONTAINING_BLOCK.get(), geodeContainingBlockModel);
    }
}
