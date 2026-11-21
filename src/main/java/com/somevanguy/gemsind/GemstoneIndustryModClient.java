package com.somevanguy.gemsind;

import com.somevanguy.gemsind.content.AllBlockEntities;
import com.somevanguy.gemsind.content.AllBlocks;
import com.somevanguy.gemsind.content.alloying_furnace.AlloyingFurnaceScreen;
import com.somevanguy.gemsind.content.counter.CounterRenderer;
import com.somevanguy.gemsind.content.geode_containing_block.GeodeContainingBlockBakedModel;
import com.somevanguy.gemsind.content.geode_containing_block.GeodeContainingBlockEntity;
import com.somevanguy.gemsind.content.items.AcidPackItem;
import com.somevanguy.gemsind.content.AllItems;
import com.somevanguy.gemsind.content.AllMenus;
import com.somevanguy.gemsind.content.acid_purifier.AcidPurifierScreen;
import com.somevanguy.gemsind.content.centrifuge.CentrifugeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.registries.DeferredItem;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = GemstoneIndustryMod.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = GemstoneIndustryMod.MODID, value = Dist.CLIENT)
public class GemstoneIndustryModClient {
    public GemstoneIndustryModClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        GemstoneIndustryMod.LOGGER.info("HELLO FROM CLIENT SETUP");
        GemstoneIndustryMod.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    public static void onRegisterMenuScreen(RegisterMenuScreensEvent event) {
        event.register(AllMenus.ACID_PURIFIER_MENU.get(), AcidPurifierScreen::new);
        event.register(AllMenus.CENTRIFUGE_MENU.get(), CentrifugeScreen::new);
        //event.register(AllMenus.TUMBLER_MENU.get(), TumblerScreen::new); // TODO: ADD TUMBLER SCREEN REG
        event.register(AllMenus.ALLOYING_FURNACE_MENU.get(), AlloyingFurnaceScreen::new);
    }

    @SubscribeEvent
    public static void onEntityRenderersEvent(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(AllBlockEntities.COUNTER_ENTITY.get(), CounterRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterItemColorHandlers(RegisterColorHandlersEvent.Item event) {
        for (DeferredItem<AcidPackItem> acidPackItem : AllItems.ACIDS) {
            event.register(((itemStack, tintIndex) -> {
                if (tintIndex != 0) {
                    return 0xffffffff;
                }

                int rgb = acidPackItem.get().RGB;

                return rgb | 0xff000000;
            }), acidPackItem.get());
        }
    }

    public static final ModelResourceLocation GEODE_CONTAINING_BLOCK_OVERLAY =
            ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(GemstoneIndustryMod.MODID, "block/geode_containing_block_overlay"));

    @SubscribeEvent
    public static void onRegisterAdditional(ModelEvent.RegisterAdditional event) {
        event.register(GEODE_CONTAINING_BLOCK_OVERLAY);
    }

    @SubscribeEvent
    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        var models = event.getModels();
        var overlayModel = models.get(GEODE_CONTAINING_BLOCK_OVERLAY);
        for (BlockState state : AllBlocks.GEODE_CONTAINING_BLOCK.get().getStateDefinition().getPossibleStates()) {
            ModelResourceLocation location = BlockModelShaper.stateToModelLocation(state);
            BakedModel model = models.get(location);
            if (model != null && overlayModel != null) {
                models.put(location, new GeodeContainingBlockBakedModel(model, overlayModel));
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
        event.register(((state, level, pos, tintIndex) -> {
            if (level != null && pos != null && level.getBlockEntity(pos) instanceof GeodeContainingBlockEntity blockEntity) {
                BlockState replacementState = blockEntity.getReplacementState();
                if (replacementState != null) {
                    return event.getBlockColors().getColor(replacementState, level, pos, tintIndex);
                }
            }
            return -1;
        }), AllBlocks.GEODE_CONTAINING_BLOCK.get());
    }
}
