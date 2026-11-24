package com.somevanguy.gemsind.content.acid_purifier;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.somevanguy.gemsind.content.items.AcidPackItem;
import com.somevanguy.gemsind.helpers.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

import java.util.ArrayList;

public class AcidPurifierRenderer implements BlockEntityRenderer<AcidPurifierEntity> {
    public static final float PIXEL = (float) 1 / 16;
    public static final float ITEM_SIZE = 0.4f;

    public final BlockRenderDispatcher blockRenderer;
    public final ItemRenderer itemRenderer;

    public AcidPurifierRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderer = context.getBlockRenderDispatcher();
        itemRenderer = context.getItemRenderer();
        var extensions = IClientFluidTypeExtensions.of(Fluids.WATER);
        var texture = extensions.getStillTexture();
        //var texture = ResourceLocation.withDefaultNamespace("block/jigsaw_side");
        sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
    }

    public final TextureAtlasSprite sprite;

    @Override
    public void render(AcidPurifierEntity acidPurifierEntity, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        var level = acidPurifierEntity.getLevel();
        if (level != null && acidPurifierEntity.getItemHandler() != null) {
            renderItems(level, acidPurifierEntity.getBlockPos(), multiBufferSource, poseStack, RenderHelper.getContainedDebug());//acidPurifierEntity.getContained());

            if (acidPurifierEntity.acidPackContained != null && acidPurifierEntity.hasAcid) {
                if (multiBufferSource instanceof MultiBufferSource.BufferSource bufferSource) {
                    bufferSource.endBatch();
                }
                VertexConsumer consumer = multiBufferSource.getBuffer(RenderType.itemEntityTranslucentCull(sprite.atlasLocation()));

                float progress = (float) acidPurifierEntity.acidLeft / AcidPurifierEntity.MAX_ACID_LEFT;
                float red = RenderHelper.inQuintEase(progress, (float) AcidPackItem.DULL_R / 255, (float) acidPurifierEntity.acidPackContained.r / 255);
                float green = RenderHelper.inQuintEase(progress, (float) AcidPackItem.DULL_G / 255, (float) acidPurifierEntity.acidPackContained.g / 255);
                float blue = RenderHelper.inQuintEase(progress, (float) AcidPackItem.DULL_B / 255, (float) acidPurifierEntity.acidPackContained.b / 255);

                poseStack.pushPose();
                RenderHelper.renderBox(poseStack, consumer,
                        PIXEL, PIXEL, PIXEL, PIXEL * 15, 14 * PIXEL, PIXEL * 15,
                        sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1(),
                        LightTexture.FULL_BRIGHT,
                        1f, red, green, blue);
                poseStack.popPose();
            }
        }
    }

    public void renderItems(Level level, BlockPos pos, MultiBufferSource multiBufferSource, PoseStack poseStack, ArrayList<ItemStack> containedStacks) {
        if (containedStacks.size() <= 0) return;
        ItemStack stack;
        stack = containedStacks.get(0);
        poseStack.pushPose();
        poseStack.translate(0.4f, 0.08f, 0.32f);
        poseStack.mulPose(RenderHelper.fromDegreesXZY(90f, 45f, 0f));
        poseStack.scale(ITEM_SIZE, ITEM_SIZE, 0.5f);
        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED,
                RenderHelper.getLightLevel(level, pos),
                OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, level, 1);
        poseStack.popPose();

        if (containedStacks.size() <= 1) return;
        stack = containedStacks.get(1);
        poseStack.pushPose();
        poseStack.translate(0.7f, 0.08f, 0.69f);
        poseStack.mulPose(RenderHelper.fromDegreesXZY(90f, 10f, 0f));
        poseStack.scale(ITEM_SIZE, ITEM_SIZE, 0.5f);
        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED,
                RenderHelper.getLightLevel(level, pos),
                OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, level, 1);
        poseStack.popPose();
    }
}
