package com.somevanguy.gemsind.content.acid_purifier;

import com.mojang.blaze3d.systems.RenderSystem;
import com.somevanguy.gemsind.GemstoneIndustryMod;
import com.somevanguy.gemsind.content.base.BaseTransmutatorScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AcidPurifierScreen extends BaseTransmutatorScreen<AcidPurifierMenu> {
    public static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(GemstoneIndustryMod.MODID, "textures/gui/acid_purifier.png");
    public static final ResourceLocation ACID_OVERLAY = ResourceLocation.fromNamespaceAndPath(GemstoneIndustryMod.MODID, "textures/gui/acid_overlay.png");

    public AcidPurifierScreen(AcidPurifierMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(BACKGROUND, x, y, 0, 0, imageWidth, imageHeight, 256, 256);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(menu.getRed(), menu.getGreen(), menu.getBlue(), 1f);
        guiGraphics.blit(ACID_OVERLAY, x + 76, y + 11, 0, 0, 81, 63, 81, 63);
        RenderSystem.setShaderColor(1f,1f, 1f, 1f);
        RenderSystem.disableBlend();
//        if (menu.isProgressing()) {
//
//        }
    }
}
