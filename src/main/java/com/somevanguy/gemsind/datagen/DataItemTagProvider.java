package com.somevanguy.gemsind.datagen;

import com.somevanguy.gemsind.GemstoneIndustryMod;
import com.somevanguy.gemsind.content.AllItems;
import com.somevanguy.gemsind.content.items.*;
import com.somevanguy.gemsind.content.AllTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class DataItemTagProvider extends ItemTagsProvider {
    public DataItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, GemstoneIndustryMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        HashMap<String, Integer> elementCounts = new HashMap<>();
        for (DeferredHolder<Item, GemItem> gemstoneHolder : AllItems.GEMSTONES) {
            tag(AllTags.Items.GEMSTONE).add(gemstoneHolder.get());
            for (String element : GemItem.prominentElements) {
                if (gemstoneHolder.get().hasElement(element)) {
                    elementCounts.put(element, elementCounts.getOrDefault(element, 0) + 1);
                    tag(AllTags.Items.getElementTag(element)).add(gemstoneHolder.get());
                }
            }
            if (gemstoneHolder.get().isFlux()) {
                tag(AllTags.Items.FLUX).add(gemstoneHolder.get());
            }
        }
        for (String element : GemItem.prominentElements) {
            if (elementCounts.getOrDefault(element, 0) < 3) {
                GemstoneIndustryMod.LOGGER.warn("Element \"{}\" has less than 3 gems with it ({} total)", element, elementCounts.getOrDefault(element, 0));
            }
        }
        for (DeferredHolder<Item, GeodeItem> geodeHolder : AllItems.GEODES) {
            tag(AllTags.Items.GEODE).add(geodeHolder.getKey());
        }
        for (DeferredHolder<Item, AcidPackItem> alloyHolder : AllItems.ACIDS) {
            tag(AllTags.Items.ACID).add(alloyHolder.getKey());
        }
        for (DeferredHolder<Item, AlloyItem> alloyHolder : AllItems.ALLOYS) {
            tag(AllTags.Items.ALLOY).add(alloyHolder.getKey());
        }

//        tag(AllTags.Items.getElementTag("Fe")).add(Items.IRON_INGOT);
//        tag(AllTags.Items.getElementTag("Fe")).add(Items.IRON_BLOCK);
//
//        tag(AllTags.Items.getElementTag("Cr")).add(Items.REDSTONE);
//        tag(AllTags.Items.getElementTag("Cr")).add(Items.REDSTONE_BLOCK);
//
//        tag(AllTags.Items.getElementTag("Au")).add(Items.GOLD_INGOT);
//        tag(AllTags.Items.getElementTag("Au")).add(Items.GOLD_BLOCK);
//
//        tag(AllTags.Items.getElementTag("C")).add(Items.DIAMOND);
//        tag(AllTags.Items.getElementTag("C")).add(Items.DIAMOND_BLOCK);
//
//        tag(AllTags.Items.getElementTag("Si")).add(Items.EMERALD);
//        tag(AllTags.Items.getElementTag("O")).add(Items.EMERALD);
//        tag(AllTags.Items.getElementTag("Si")).add(Items.EMERALD_BLOCK);
//        tag(AllTags.Items.getElementTag("O")).add(Items.EMERALD_BLOCK);
    }
}
