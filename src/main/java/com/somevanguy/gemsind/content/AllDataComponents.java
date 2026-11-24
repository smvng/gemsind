package com.somevanguy.gemsind.content;

import com.mojang.serialization.Codec;
import com.somevanguy.gemsind.GemstoneIndustryMod;
import com.somevanguy.gemsind.content.items.AcidPackItem;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class AllDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, GemstoneIndustryMod.MODID);

    public static void register(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
    }

    public static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> registerDataComponent(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return  DATA_COMPONENTS.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static DeferredHolder<DataComponentType<?>, DataComponentType<Float>> registerProgressComponent(String processname) {
        return registerDataComponent(processname+"_progress", builder -> builder.persistent(Codec.floatRange(0,1)));
    }

//    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> GEM_CACHED_VALUE = registerDataComponent("gem_cached_value",
//                builder -> builder.persistent(Codec.intRange(0,Integer.MAX_VALUE)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> GEM_PURITY = registerDataComponent("gem_purity",
                builder -> builder.persistent(Codec.floatRange(0,1)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> GEM_POLISH = registerDataComponent("gem_polish",
                builder -> builder.persistent(Codec.floatRange(0,1)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> GEM_IMBUEMENTS = registerDataComponent("gem_imbuements",
                builder -> builder.persistent(Codec.intRange(0,255))); // super late game material for exponential gem worth increase TODO:ADD
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> GEM_BONUS_VALUE = registerDataComponent("gem_bonus_value",
            builder -> builder.persistent(Codec.intRange(0,Integer.MAX_VALUE))); // when an alloy is forged with a gem that is worth more than the alloy TODO:aDD

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> GEODE_LOOT_TABLE = registerDataComponent("geode_loot_table",
                builder -> builder.persistent(Codec.STRING));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> GEODE_CRACK_PROGRESS = registerDataComponent("geode_crack_progress",
            builder -> builder.persistent(Codec.floatRange(0,1)));

//    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ACID_GAIN = registerDataComponent("acid_gain",
//            builder -> builder.persistent(Codec.intRange(0,Integer.MAX_VALUE)));
//    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> TUMBLER_GAIN = registerDataComponent("tumbler_gain",
//            builder -> builder.persistent(Codec.intRange(0,Integer.MAX_VALUE)));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ACIDITY = registerDataComponent("acidity",
            builder -> builder.persistent(Codec.intRange(0, AcidPackItem.MAX_ACID_STRENGTH)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> CENTRIFUGE_USED = registerDataComponent("centrifuge_used",
            builder -> builder.persistent(Codec.BOOL));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> TUMBLER_USED = registerDataComponent("tumbler_used",
            builder -> builder.persistent(Codec.BOOL));
}
