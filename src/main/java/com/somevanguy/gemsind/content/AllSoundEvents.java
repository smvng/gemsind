package com.somevanguy.gemsind.content;

import com.somevanguy.gemsind.GemstoneIndustryMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AllSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, GemstoneIndustryMod.MODID);

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }

    public record SoundOriginsValue(Supplier<SoundEvent> soundEventSupplier, List<ResourceLocation> locations) {};

    public static final HashMap<String, SoundOriginsValue> SoundOrigins = new HashMap<>();

    private static Supplier<SoundEvent> registerSoundEvent(String name, List<String> sounds) {
        var loc = ResourceLocation.fromNamespaceAndPath(GemstoneIndustryMod.MODID, name);
        var soundEvent = SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(loc));
        var soundOriginsValue = new SoundOriginsValue(() -> soundEvent.get(), sounds.stream().map((soundName) ->
                ResourceLocation.fromNamespaceAndPath(GemstoneIndustryMod.MODID, soundName)).collect(Collectors.toList()));
        SoundOrigins.put(name, soundOriginsValue);
        return soundEvent;
    }

    public static final Supplier<SoundEvent> ACID_PURIFIER_AMBIENT = registerSoundEvent("acid_purifier_ambient", List.of(
            "acid_purifier_ambient"
    ));
    public static final Supplier<SoundEvent> CENTRIFUGE_AMBIENT = registerSoundEvent("centrifuge_ambient", List.of(
            "centrifuge_ambient_1",
            "centrifuge_ambient_2"
    ));
    public static final Supplier<SoundEvent> CENTRIFUGE_FINISH = registerSoundEvent("centrifuge_finish", List.of(
            "deep_fizz"
    ));
    public static final Supplier<SoundEvent> ALLOYING_FURNACE_AMBIENT = registerSoundEvent("alloying_furnace_ambient", List.of(
            "crackling_ambient_1",
            "crackling_ambient_2",
            "crackling_ambient_3",
            "crackling_ambient_4"
    ));
    public static final Supplier<SoundEvent> ALLOYING_FURNACE_FINISH = registerSoundEvent("alloying_furnace_finish", List.of(
            "deep_fizz"
    ));

    public static final Supplier<SoundEvent> GEODE_CONTAINING_BLOCK_CRACK = registerSoundEvent("geode_containing_block_crack", List.of(
            "centrifuge_ambient_1" // TODO:CHANGE
    ));
    public static final Supplier<SoundEvent> GEODE_CONTAINING_BLOCK_BREAK = registerSoundEvent("geode_containing_block_break", List.of(
            "centrifuge_ambient_1"
    ));
}
