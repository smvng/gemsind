package com.somevanguy.gemsind.datagen.models;

import com.somevanguy.gemsind.GemstoneIndustryMod;
import com.somevanguy.gemsind.content.items.AcidPackItem;
import com.somevanguy.gemsind.content.AllItems;
import com.somevanguy.gemsind.content.items.AlloyItem;
import com.somevanguy.gemsind.content.items.GemItem;
import com.somevanguy.gemsind.content.items.GeodeItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

public class DataItemModelProvider extends ItemModelProvider {
    public DataItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, GemstoneIndustryMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItemWithTexture(AllItems.CHLORINATING_SALT.getId(), modLoc("item/chlorinating_salt"));
        basicItemWithTexture(AllItems.ANTHRACITE.getId(), modLoc("item/anthracite"));

        basicItemWithTexture(AllItems.EMPTY_ACID_PACK.getId(), modLoc("item/acid_pack"));
        for (DeferredItem<AcidPackItem> acidPackItem : AllItems.ACIDS) {
            withExistingParent(acidPackItem.getId().getPath(), mcLoc("item/generated"))
                    .texture("layer0", modLoc("item/acid_pack_overlay"))
                    .texture("layer1", modLoc("item/acid_pack"));
        }

        for (DeferredItem<GeodeItem> geodeHolder : AllItems.GEODES) {
            var geodeName = geodeHolder.getId().getPath();
            withExistingParent(geodeHolder.getId().getPath(), modLoc("item/base_geode"))
                    .texture("main", modLoc("item/"+geodeName+"/"+geodeName));
            for (DeferredItem<?> holder : geodeHolder.get().getDatagenLootDrops()) {
                if (holder.get() instanceof GemItem) {
                    var gemName = holder.getId().getPath();
                    basicItemWithTexture(holder.getId(), modLoc("item/river_geode/halite"));//modLoc("item/"+geodeName+"/"+gemName));
                }
            }
        }
        for (DeferredItem<AlloyItem> alloyHolder : AllItems.ALLOYS) {
            var name = alloyHolder.getId().getPath();
            basicItemWithTexture(alloyHolder.getId(), modLoc("item/alloys/"+name));
        }
    }

    private void basicItemWithTexture(ResourceLocation item, ResourceLocation texture) {
        try {
            withExistingParent(item.getPath(), mcLoc("item/generated")).texture("layer0", texture);
        } catch (Exception e) {
            GemstoneIndustryMod.LOGGER.warn("failed to create item model for {}", item.getPath());
        }
    }
}
