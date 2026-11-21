package com.somevanguy.gemsind.content;

import com.somevanguy.gemsind.GemstoneIndustryMod;
import com.somevanguy.gemsind.content.alloying_furnace.AlloyingFurnaceEntity;
import com.somevanguy.gemsind.content.centrifuge.CentrifugeEntity;
import com.somevanguy.gemsind.content.counter.CounterEntity;
import com.somevanguy.gemsind.content.acid_purifier.AcidPurifierEntity;
import com.somevanguy.gemsind.content.geode_containing_block.GeodeContainingBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class AllBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, GemstoneIndustryMod.MODID);

    public static <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String name, DeferredBlock<?> blockHolder, BlockEntityType.BlockEntitySupplier<T> entitySupplier) {
        return BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of(entitySupplier, blockHolder.get()).build(null));
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

    public static final Supplier<BlockEntityType<CounterEntity>> COUNTER_ENTITY =
            registerBlockEntity("counter_entity", AllBlocks.COUNTER, CounterEntity::new);

    public static final Supplier<BlockEntityType<AcidPurifierEntity>> ACID_PURIFIER_ENTITY =
            registerBlockEntity("acid_purifier_entity", AllBlocks.ACID_PURIFIER, AcidPurifierEntity::new);
    public static final Supplier<BlockEntityType<CentrifugeEntity>> CENTRIFUGE_ENTITY =
            registerBlockEntity("centrifuge_entity", AllBlocks.CENTRIFUGE, CentrifugeEntity::new);
//    public static final Supplier<BlockEntityType<TumblerBlockEntity>> TUMBLER_ENTITY =
//            registerBlockEntity("tumbler_entity", AllBlocks.TUMBLER, TumblerBlockEntity::new);
    public static final Supplier<BlockEntityType<AlloyingFurnaceEntity>> ALLOYING_FURNACE_ENTITY =
            registerBlockEntity("alloying_furnace_entity", AllBlocks.ALLOYING_FURNACE, AlloyingFurnaceEntity::new);

    public static final Supplier<BlockEntityType<GeodeContainingBlockEntity>> GEODE_CONTAINING_BLOCK_ENTITY =
            registerBlockEntity("geode_containing_block_entity", AllBlocks.GEODE_CONTAINING_BLOCK, GeodeContainingBlockEntity::new);

}
