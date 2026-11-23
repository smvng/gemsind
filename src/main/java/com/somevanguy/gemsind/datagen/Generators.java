package com.somevanguy.gemsind.datagen;

import com.somevanguy.gemsind.GemstoneIndustryMod;
import com.somevanguy.gemsind.datagen.loot_tables.DataBlockLootTableProvider;
import com.somevanguy.gemsind.datagen.loot_tables.DataGenericLootTableProvider;
import com.somevanguy.gemsind.datagen.models.DataBlockStateProvider;
import com.somevanguy.gemsind.datagen.models.DataItemModelProvider;
import com.somevanguy.gemsind.datagen.tags.DataBiomeTagProvider;
import com.somevanguy.gemsind.datagen.tags.DataBlockTagProvider;
import com.somevanguy.gemsind.datagen.tags.DataItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = GemstoneIndustryMod.MODID)
public class Generators {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator dataGenerator = event.getGenerator();
        PackOutput output = dataGenerator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();

        dataGenerator.addProvider(event.includeServer(), new LootTableProvider(output, Collections.emptySet(),
                List.of(
                        new LootTableProvider.SubProviderEntry(DataBlockLootTableProvider::new, LootContextParamSets.BLOCK),
                        new LootTableProvider.SubProviderEntry(DataGenericLootTableProvider::new, LootContextParamSets.EMPTY)
                ), provider));

        BlockTagsProvider blockTagsProvider = new DataBlockTagProvider(output, provider, helper);
        dataGenerator.addProvider(event.includeServer(), blockTagsProvider);
        dataGenerator.addProvider(event.includeServer(), new DataItemTagProvider(output, provider, blockTagsProvider.contentsGetter(), helper));
        dataGenerator.addProvider(event.includeServer(), new DataBiomeTagProvider(output, provider, helper));
        dataGenerator.addProvider(event.includeServer(), new DataRecipeProvider(output, provider));

        dataGenerator.addProvider(event.includeClient(), new DataItemModelProvider(output, helper));
        dataGenerator.addProvider(event.includeClient(), new DataBlockStateProvider(output, helper));
        dataGenerator.addProvider(event.includeClient(), new DataSoundDefinitionsProvider(output, helper));
    }

}
