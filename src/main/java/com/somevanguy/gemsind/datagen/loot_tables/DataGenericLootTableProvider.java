package com.somevanguy.gemsind.datagen.loot_tables;

import com.somevanguy.gemsind.content.AllItems;
import com.somevanguy.gemsind.content.items.GeodeItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.BiConsumer;

public class DataGenericLootTableProvider implements LootTableSubProvider {
    private final HolderLookup.Provider provider;

    public DataGenericLootTableProvider(HolderLookup.Provider provider1) {
        this.provider = provider1;
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> writer) {
        for (DeferredItem<GeodeItem> geodeItem : AllItems.GEODES) {
            String lootTableName = geodeItem.get().defaultLootTableName;
            ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(lootTableName));

            LootPool.Builder poolBuilder = LootPool.lootPool().setRolls(ConstantValue.exactly(1));
            for (DeferredItem<?> drop : geodeItem.get().getDatagenLootDrops()) {
                poolBuilder.add(LootItem.lootTableItem(drop.get()));
            }

            writer.accept(key, LootTable.lootTable().withPool(poolBuilder));
        }
    }
}
