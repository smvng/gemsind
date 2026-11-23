package com.somevanguy.gemsind.datagen.models;

import com.somevanguy.gemsind.GemstoneIndustryMod;
import com.somevanguy.gemsind.content.items.AcidPackItem;
import com.somevanguy.gemsind.content.AllItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

public class DataItemModelProvider extends ItemModelProvider {
    public DataItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, GemstoneIndustryMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(AllItems.EMPTY_ACID_PACK.getId().getPath(), mcLoc("item/generated"))
            .texture("layer0", modLoc("item/acid_pack"));
        for (DeferredItem<AcidPackItem> acidPackItem : AllItems.ACIDS) {
            withExistingParent(acidPackItem.getId().getPath(), mcLoc("item/generated"))
                    .texture("layer0", modLoc("item/acid_pack_overlay"))
                    .texture("layer1", modLoc("item/acid_pack"));
        }
//        for (DeferredHolder<Item, ? extends Item> gemstoneHolder : AllItems.GEMSTONES) {
//            basicItem(gemstoneHolder.get());
//        }
//        for (DeferredHolder<Item, ? extends Item> geodeHolder : AllItems.GEODES) {
//            basicItem(geodeHolder.get());
//        }
    }
}
