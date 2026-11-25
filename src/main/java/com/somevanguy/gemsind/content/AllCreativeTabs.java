package com.somevanguy.gemsind.content;

import com.somevanguy.gemsind.GemstoneIndustryMod;
import com.somevanguy.gemsind.content.items.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class AllCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GemstoneIndustryMod.MODID);

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }

    public static final Supplier<CreativeModeTab> GEMSIND = CREATIVE_TABS.register("gemsind",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(AllBlocks.COUNTER.get()))
                    .title(Component.translatable("tab.gemsind.gemsind"))
                    .displayItems((itemDisplayParameters, output) -> {
                        // blocks
                        output.accept(AllBlocks.COUNTER.asItem());
                        output.accept(AllBlocks.ACID_PURIFIER.asItem());
                        output.accept(AllBlocks.DESALINATOR.asItem());
                        output.accept(AllBlocks.CENTRIFUGE.asItem());
                        output.accept(AllBlocks.TUMBLER.asItem());
                        output.accept(AllBlocks.ALLOYING_FURNACE.asItem());

                        // tools (geode containing block / counter)
                        output.accept(AllItems.IRON_HAMMER.asItem());
                        output.accept(AllItems.IRON_PICK.asItem());
                        output.accept(AllItems.GOLDEN_HAMMER.asItem());
                        output.accept(AllItems.GOLDEN_PICK.asItem());
                        output.accept(AllItems.DIAMOND_HAMMER.asItem());
                        output.accept(AllItems.DIAMOND_PICK.asItem());
                        output.accept(AllItems.REINFORCED_HAMMER.asItem());
                        output.accept(AllItems.REINFORCED_PICK.asItem());

                        //acid purifier
                        output.accept(AllItems.EMPTY_ACID_PACK.asItem());
                        for (DeferredHolder<Item, AcidPackItem> acidPack : AllItems.ACIDS) {
                            output.accept(acidPack.get());
                        }

                        // desalinator
                        output.accept(AllItems.CHLORINATING_SALT.asItem());

                        // alloying furnace
                        output.accept(AllItems.ANTHRACITE.asItem());

                        // peak
                        output.accept(AllItems.WOODEN_HAMMER.asItem());
                        output.accept(AllItems.WOODEN_PICK.asItem());
                    })
                    .build());

    public static final Supplier<CreativeModeTab> GEODES_AND_VALUABLES = CREATIVE_TABS.register("geodes_and_valuables",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(AllItems.RIVER_GEODE.asItem()))
                    .title(Component.translatable("tab.gemsind.geodes_and_valuables"))
                    .displayItems((itemDisplayParameters, output) -> {
                        for (DeferredHolder<Item, GeodeItem> geode : AllItems.GEODES) {
                            output.accept(geode.get());
                        }
                        for (DeferredHolder<Item, AlloyItem> alloy : AllItems.ALLOYS) {
                            output.accept(alloy.get());
                        }
                        for (DeferredHolder<Item, GemItem> gem : AllItems.GEMSTONES) {
                            output.accept(gem.get());
                        }
                    })
                    .build());
}
