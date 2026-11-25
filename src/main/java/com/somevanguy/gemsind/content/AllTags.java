package com.somevanguy.gemsind.content;

import com.somevanguy.gemsind.GemstoneIndustryMod;
import com.somevanguy.gemsind.content.items.GemItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

import java.util.*;

public class AllTags {
    public static TagKey<Block> createBlockTag(String name) {
        return BlockTags.create(ResourceLocation.fromNamespaceAndPath(GemstoneIndustryMod.MODID, name));
    }

    public static TagKey<Item> createItemTag(String name) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath(GemstoneIndustryMod.MODID, name));
    }

    public static TagKey<Biome> createBiomeTag(String name) {
        return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(GemstoneIndustryMod.MODID, name));
    }

    public static class Blocks {

    }

    public static class Items {
        public static Map<String, TagKey<Item>> elementTags = new HashMap<>();

        public static TagKey<Item> getElementTag(String element) {
            return elementTags.get(GemItem.safeElementName(element));
        }

        public static TagKey<Item> createElementTag(String element) {
            element = GemItem.safeElementName(element);
            var tag = createItemTag("has_element/"+element);
            elementTags.put(element, tag);
            return tag;
        }

        public static void createProminentTags() {
            for (String element : GemItem.PROMINENT_ELEMENTS) {
                createElementTag(element);
            }
        }

        public static final TagKey<Item> GEMSTONE = createItemTag("gemstone");
        public static final TagKey<Item> GEODE = createItemTag("geode");
        public static final TagKey<Item> FLUX = createItemTag("flux");
        public static final TagKey<Item> ACID = createItemTag("contains_acid");
        public static final TagKey<Item> ALLOY = createItemTag("alloy");
    }

    public static class PickTests {
        public static final TagKey<Block> CONTAINS_RIVER_GEODE = createBlockTag("contains_river_geode");
        public static final TagKey<Biome> SPAWNS_RIVER_GEODE = createBiomeTag("spawns_river_geode");

        public static final TagKey<Block> CONTAINS_GRANITE_GEODE = createBlockTag("contains_granite_geode");

        public static final TagKey<Block> CONTAINS_NETHERRACK_GEODE = createBlockTag("contains_netherrack_geode");

        public static final TagKey<Block> CONTAINS_SLATED_GEODE = createBlockTag("contains_slated_geode");

        public static final TagKey<Block> CONTAINS_MOUNTAIN_GEODE = createBlockTag("contains_mountain_geode");
        public static final TagKey<Biome> SPAWNS_MOUNTAIN_GEODE = createBiomeTag("spawns_mountain_geode");

        public static final TagKey<Block> CONTAINS_LAVA_GEODE = createBlockTag("contains_lava_geode");
        public static final TagKey<Biome> SPAWNS_LAVA_GEODE = createBiomeTag("spawns_lava_geode");

        public static final TagKey<Block> CONTAINS_UNEARTHLY_GEODE = createBlockTag("contains_unearthly_geode");
    }
}
