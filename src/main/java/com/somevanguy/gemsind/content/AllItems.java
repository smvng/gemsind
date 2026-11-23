package com.somevanguy.gemsind.content;

import com.somevanguy.gemsind.GemstoneIndustryMod;
import com.somevanguy.gemsind.content.items.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class AllItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GemstoneIndustryMod.MODID);

    public static <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> itemsupplier) {
        return ITEMS.register(name, itemsupplier);
    }

    public static ArrayList<DeferredItem<GemItem>> GEMSTONES = new ArrayList<>();
    public static ArrayList<DeferredItem<GeodeItem>> GEODES = new ArrayList<>();
    public static ArrayList<DeferredItem<AcidPackItem>> ACIDS = new ArrayList<>();
    public static ArrayList<DeferredItem<AlloyItem>> ALLOYS = new ArrayList<>();

    public static DeferredItem<GemItem> registerBasicGem(String name, String formula, int acidTolerance, Rarity rarity, GemItem.GainInfo info) {
        var r = registerItem(name, () -> new GemItem(name, new Item.Properties().rarity(rarity), formula, acidTolerance, info));
        GEMSTONES.add(r);
        return r;
    }

    public static DeferredItem<GeodeItem> registerBasicGeode(String name, int toughness, Rarity rarity, List<DeferredItem<?>> drops) {
        var r = registerItem(name, () -> new GeodeItem(new Item.Properties().rarity(rarity), name, toughness, drops));
        GEODES.add(r);
        return r;
    }

    public static DeferredItem<AcidPackItem> registerBasicAcid(String name, int acidStrength, int red, int green, int blue) {
        var r = registerItem(name, () -> new AcidPackItem(new Item.Properties().craftRemainder(EMPTY_ACID_PACK.get()).stacksTo(8), acidStrength, red, green, blue));
        ACIDS.add(r);
        return r;
    }

    public static DeferredItem<AlloyItem> registerBasicAlloy(String name, Rarity rarity, AlloyItem.Crafting craftData, GemItem.GainInfo info) {
        var r = registerItem(name, () -> new AlloyItem(name, new Item.Properties().rarity(rarity), craftData, info));
        ALLOYS.add(r);
        return r;
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static final DeferredItem<Item> ASH = AllItems.registerItem("ash", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CHLORINATING_SALT = AllItems.registerItem("chlorinating_salt", () -> new Item(new Item.Properties())); // TODO: USE FOR IMBUEMENTS LATER
    public static final DeferredItem<AnthraciteItem> ANTHRACITE = AllItems.registerItem("anthracite", AnthraciteItem::new);

    public static final DeferredItem<HammerItem> WOODEN_HAMMER = AllItems.registerItem("wooden_hammer", () -> new HammerItem(false, 0, new Item.Properties().durability(2)));
    public static final DeferredItem<HammerItem> IRON_HAMMER = AllItems.registerItem("iron_hammer", () -> new HammerItem(1, new Item.Properties().durability(250)));
    public static final DeferredItem<HammerItem> GOLDEN_HAMMER = AllItems.registerItem("golden_hammer", () -> new HammerItem(4, new Item.Properties().durability(60)));
    public static final DeferredItem<HammerItem> DIAMOND_HAMMER = AllItems.registerItem("diamond_hammer", () -> new HammerItem(2, new Item.Properties().durability(1250)));
    public static final DeferredItem<HammerItem> REINFORCED_HAMMER = AllItems.registerItem("reinforced_hammer", () -> new HammerItem(3, new Item.Properties().durability(2750).fireResistant()));

    public static final DeferredItem<PickItem> WOODEN_PICK = AllItems.registerItem("wooden_pick", () -> new PickItem(false, 0, 1, new Item.Properties().durability(2)));
    public static final DeferredItem<PickItem> IRON_PICK = AllItems.registerItem("iron_pick", () -> new PickItem(1, 1, new Item.Properties().durability(250)));
    public static final DeferredItem<PickItem> GOLDEN_PICK = AllItems.registerItem("golden_pick", () -> new PickItem(4, 3, new Item.Properties().durability(60)));
    public static final DeferredItem<PickItem> DIAMOND_PICK = AllItems.registerItem("diamond_pick", () -> new PickItem(2, 2, new Item.Properties().durability(1250)));
    public static final DeferredItem<PickItem> REINFORCED_PICK = AllItems.registerItem("reinforced_pick", () -> new PickItem(3, 3, new Item.Properties().durability(2750)));

    // acids
    public static final DeferredItem<Item> EMPTY_ACID_PACK = AllItems.registerItem("empty_acid_pack", () -> new Item(new Item.Properties()));
    public static final DeferredItem<AcidPackItem> ACETIC_ACID_PACK = AllItems.registerBasicAcid("acetic_acid_pack",
            1, 110, 35, 40);
    public static final DeferredItem<AcidPackItem> OXALIC_ACID_PACK = AllItems.registerBasicAcid("oxalic_acid_pack",
            2, 25, 215, 110);
    public static final DeferredItem<AcidPackItem> HYDROCHLORIC_ACID_PACK = AllItems.registerBasicAcid("hydrochloric_acid_pack",
            3, 110, 40, 180);

    // alloys
    public static final DeferredItem<AlloyItem> TEST_ALLOY = AllItems.registerBasicAlloy("test_alloy", Rarity.COMMON,
            new AlloyItem.Crafting(1, 3*20, "C", "Os"), new GemItem.GainInfo(false, 10000, 2.4f, 0.1f));
    public static final DeferredItem<AlloyItem> STEEL = AllItems.registerBasicAlloy("steel", Rarity.COMMON,
            new AlloyItem.Crafting(1, 30*20, "Fe", "C"), new GemItem.GainInfo(false, 350, 1.5f, 1f));
    public static final DeferredItem<AlloyItem> STAINLESS_STEEL = AllItems.registerBasicAlloy("stainless_steel", Rarity.COMMON,
            new AlloyItem.Crafting(2, 60*20, "Fe", "C", "Cr"), new GemItem.GainInfo(false, 1250, 1.25f, 1.25f));
    public static final DeferredItem<AlloyItem> COBALT_CHROME = AllItems.registerBasicAlloy("cobalt_chrome", Rarity.COMMON,
            new AlloyItem.Crafting(1, (int) (1.5*60*20), "Co", "Cr"), new GemItem.GainInfo(false, 1750, 1.9f, 0.6f));
    public static final DeferredItem<AlloyItem> ELECTRUM = AllItems.registerBasicAlloy("electrum", Rarity.UNCOMMON,
            new AlloyItem.Crafting(2, 2*60*20, "Au", "Ag"), new GemItem.GainInfo(false, 2000, 1.4f, 1.1f));
    public static final DeferredItem<AlloyItem> ENDHITHYTE = AllItems.registerBasicAlloy("endhithyte", Rarity.EPIC,
            new AlloyItem.Crafting(4, 8*60*20, "??", "As", "Fe", "Ti"), new GemItem.GainInfo(false, 5000, 0.8f, 1.7f));

    // river geode [Na, Fe, Cl] (in clay in a river biome)
    public static final DeferredItem<GemItem> GOETHITE = AllItems.registerBasicGem("goethite", "FeO(OH)", 3, Rarity.COMMON,
            new GemItem.GainInfo(30, 0.7f, 0.3f));
    public static final DeferredItem<GemItem> HALITE = AllItems.registerBasicGem("halite", "NaCl", 2, Rarity.COMMON,
            new GemItem.GainInfo(12, 0.85f, 0.15f));

    public static final DeferredItem<GeodeItem> RIVER_GEODE = AllItems.registerBasicGeode("river_geode", 1, Rarity.COMMON,
            List.of(
                    HALITE,
                    GOETHITE
            ));

    // granite geode [Cu, C, Cr] (in granite at y>30)
    public static final DeferredItem<GemItem> MALACHITE = AllItems.registerBasicGem("malachite", "Cu2CO3(OH)2", 1, Rarity.COMMON,
            new GemItem.GainInfo(56, 0.4f, 0.6f));
    public static final DeferredItem<GemItem> PEPSYTE = AllItems.registerBasicGem("pepsyte", "Cr2O3", 3, Rarity.COMMON,
            new GemItem.GainInfo(89, 0.2f, 0.8f));

    public static final DeferredItem<GeodeItem> GRANITE_GEODE = AllItems.registerBasicGeode("granite_geode", 1, Rarity.COMMON,
            List.of(
                    MALACHITE,
                    PEPSYTE
            ));

    // netherrack geode [S, As, Cr] (in the nether at y< -30)
    public static final DeferredItem<GemItem> TENNATITE = AllItems.registerBasicGem("tennatite", "(Cu,Fe)12As4S13", 2, Rarity.COMMON,
            new GemItem.GainInfo(1050, 0.1f, 0.9f));
    public static final DeferredItem<GemItem> ENARGITE = AllItems.registerBasicGem("enargite", "Cu3AsS4", 1, Rarity.COMMON,
            new GemItem.GainInfo(1200, 0.2f, 0.8f));
    public static final DeferredItem<GemItem> UVAROVITE = AllItems.registerBasicGem("uvarovite", "Ca3Cr2(SiO4)3", 1, Rarity.COMMON,
            new GemItem.GainInfo(1450, 0.85f, 0.15f));

    public static final DeferredItem<GeodeItem> NETHERRACK_GEODE = AllItems.registerBasicGeode("netherrack_geode", 2, Rarity.COMMON,
            List.of(
                    TENNATITE,
                    ENARGITE,
                    UVAROVITE
            ));

    // mountain geode [Ba, Se, Ag] (in stone in a mountain biome at y>200)
    public static final DeferredItem<GemItem> PYRITE = AllItems.registerBasicGem("pyrite", "FeS2", 2, Rarity.COMMON,
            new GemItem.GainInfo(255, 0.45f, 0.55f));
    public static final DeferredItem<GemItem> BARITE = AllItems.registerBasicGem("barite", "BaSO4", 2, Rarity.COMMON,
            new GemItem.GainInfo(325, 0.15f, 0.85f));
    public static final DeferredItem<GemItem> FERROSELITE = AllItems.registerBasicGem("ferroselite", "FeSe2", 3, Rarity.COMMON,
            new GemItem.GainInfo(380, 0.1f, 0.9f));
    public static final DeferredItem<GemItem> MAGNETITE = AllItems.registerBasicGem("magnetite", "Fe3O4", 3, Rarity.COMMON,
            new GemItem.GainInfo(420, 0.2f, 0.8f));

    public static final DeferredItem<GeodeItem> SLATED_GEODE = AllItems.registerBasicGeode("slated", 2, Rarity.COMMON,
            List.of(
                    PYRITE,
                    BARITE,
                    FERROSELITE,
                    MAGNETITE
            ));

    // slated geode [Si, Ti, Au] (in deepslate at y< -30)
    public static final DeferredItem<GemItem> MILKY_QUARTZ = AllItems.registerBasicGem("milky_quartz", "SiO2", 2, Rarity.RARE,
            new GemItem.GainInfo(1050, 0.1f, 0.9f));
    public static final DeferredItem<GemItem> ILMENITE = AllItems.registerBasicGem("ilmenite", "FeTiO3", 2, Rarity.RARE,
            new GemItem.GainInfo(1400, 0.2f, 0.8f));
    public static final DeferredItem<GemItem> CALAVERITE = AllItems.registerBasicGem("calaverite", "AuTe2", 1, Rarity.RARE,
            new GemItem.GainInfo(1900, 0.9f, 0.1f));
    public static final DeferredItem<GemItem> KOSMOCHLOR = AllItems.registerBasicGem("kosmochlor", "NaCrSi2O6", 1, Rarity.RARE,
            new GemItem.GainInfo(2250, 0.55f, 0.45f));

    public static final DeferredItem<GeodeItem> MOUNTAIN_GEODE = AllItems.registerBasicGeode("mountain_geode", 3, Rarity.RARE,
            List.of(
                    ILMENITE,
                    MILKY_QUARTZ,
                    CALAVERITE,
                    KOSMOCHLOR
            ));

    // lava geode [Al, Pb, Pt] (under the lava pools in nether)
    public static final DeferredItem<GemItem> CROCOITE = AllItems.registerBasicGem("crocoite", "PbCr04", 1, Rarity.RARE,
            new GemItem.GainInfo(5300, 0.1f, 0.9f));
    public static final DeferredItem<GemItem> KYANITE = AllItems.registerBasicGem("kyanite", "Al2SiO5", 3, Rarity.RARE,
            new GemItem.GainInfo(5700, 0.5f, 0.5f));
    public static final DeferredItem<GemItem> CLAUSTHALITE = AllItems.registerBasicGem("clausthalite", "PbSe", 3, Rarity.RARE,
            new GemItem.GainInfo(6100, 0.7f, 0.3f));
    public static final DeferredItem<GemItem> SAPPHIRE = AllItems.registerBasicGem("sapphire", "Al2O3", 1, Rarity.RARE,
            new GemItem.GainInfo(6600, 0.2f, 0.8f));

    public static final DeferredItem<GeodeItem> LAVA_GEODE = AllItems.registerBasicGeode("lava_geode", 4, Rarity.RARE,
            List.of(
                    CROCOITE,
                    KYANITE,
                    CLAUSTHALITE,
                    SAPPHIRE
            ));

    // unearthly geode [P, Os] (under the end islands)
    public static final DeferredItem<GemItem> COSMOLITE = AllItems.registerBasicGem("cosmolite", "??Os3", 2, Rarity.RARE,
            new GemItem.GainInfo(12200, 0.6f, 0.4f));
    public static final DeferredItem<GemItem> FLOALYTE = AllItems.registerBasicGem("floalyte", "??P5", 2, Rarity.RARE,
            new GemItem.GainInfo(13900, 0.9f, 0.1f));
    public static final DeferredItem<GemItem> UNTYTE = AllItems.registerBasicGem("untyte", "??H2Cu(Cr2O7)2", 3, Rarity.RARE,
            new GemItem.GainInfo(16150, 0.7f, 0.3f));
    public static final DeferredItem<GemItem> STAROTHALITE = AllItems.registerBasicGem("starothalite", "??(Os8As4)2", 1, Rarity.RARE,
            new GemItem.GainInfo(17250, 0.1f, 0.9f));

    public static final DeferredItem<GeodeItem> UNEARTHLY_GEODE = AllItems.registerBasicGeode("unearthly_geode", 5, Rarity.RARE,
            List.of(
                    COSMOLITE,
                    FLOALYTE,
                    UNTYTE,
                    STAROTHALITE
            ));
}
