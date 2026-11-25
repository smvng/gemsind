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
import java.util.List;

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
        if (!Minecraft.useFancyGraphics()) {
            return;
        }
        var level = acidPurifierEntity.getLevel();
        if (level != null && acidPurifierEntity.getItemHandler() != null) {
            renderItems(level, acidPurifierEntity.getBlockPos(), multiBufferSource, poseStack,
                    acidPurifierEntity.progressBobbing(), acidPurifierEntity.getContained());

            if (multiBufferSource instanceof MultiBufferSource.BufferSource bufferSource) {
                bufferSource.endBatch();
            }
            VertexConsumer consumer = multiBufferSource.getBuffer(RenderType.itemEntityTranslucentCull(sprite.atlasLocation()));

            float red = (float) AcidPackItem.DULL_R / 255;
            float green = (float) AcidPackItem.DULL_G / 255;
            float blue = (float) AcidPackItem.DULL_B / 255;
            if (acidPurifierEntity.acidPackContained != null) { //&& acidPurifierEntity.hasAcid) {
                var progress = (float) acidPurifierEntity.acidLeft / AcidPurifierEntity.MAX_ACID_LEFT;
                red = RenderHelper.inQuintEase(progress, red, (float) acidPurifierEntity.acidPackContained.r / 255);
                green = RenderHelper.inQuintEase(progress, green, (float) acidPurifierEntity.acidPackContained.g / 255);
                blue = RenderHelper.inQuintEase(progress, blue, (float) acidPurifierEntity.acidPackContained.b / 255);
            }

            poseStack.pushPose();
            RenderHelper.renderBox(poseStack, consumer,
                    PIXEL, PIXEL, PIXEL, PIXEL * 15, 14 * PIXEL, PIXEL * 15,
                    sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1(),
                    LightTexture.FULL_BRIGHT,
                    1f, red, green, blue);
            poseStack.popPose();
        }
    }

    public float getBobbingHeight(int i, float t) {
        return (float) (Math.cos(2f * Math.PI * (i/9f - t)) + 0.5f) * 0.05f;
    }

    public record ItemRenderData(float x, float y, float z, float rotX, float rotY, float rotZ) {}

    public static final List<ItemRenderData> ITEM_RENDER_DATA = List.of(
            new ItemRenderData(0.28f, 0.5f, 0.7f, 10f, 100f, 0f),
            new ItemRenderData(0.73f, 0.35f, 0.8f, -15f, 20f, 0f),
            new ItemRenderData(0.65f, 0.6f, 0.65f, -5f, 10f, 30f),
            new ItemRenderData(0.65f, 0.6f, 0.65f, -5f, 10f, 30f),
            new ItemRenderData(0.15f, 0.4f, 0.68f, -25f, 95f, 10f),
            new ItemRenderData(0.35f, 0.6f, 0.4f, 20f, 0f, 60f),
            new ItemRenderData(0.7f, 0.5f, 0.45f, 10f, 5f, -5f),
            new ItemRenderData(0.3f, 0.35f, 0.25f, -20f, 5f, -5f),
            new ItemRenderData(0.7f, 0.15f, 0.3f, 80f, 10f, -10f),
            new ItemRenderData(0.7f, 0.55f, 0.2f, 0f, 20f, -15f)
    );

    public void renderItems(Level level, BlockPos pos, MultiBufferSource multiBufferSource, PoseStack poseStack, float bobbingProgress, ArrayList<ItemStack> containedStacks) {
        for (int i = 0; i < containedStacks.size(); i++) {
            ItemStack stack = containedStacks.get(i);
            poseStack.pushPose();
            var data = ITEM_RENDER_DATA.get(i);
            poseStack.translate(data.x, data.y + getBobbingHeight(i, bobbingProgress), data.z);
            poseStack.mulPose(RenderHelper.fromDegreesXZY(data.rotX, data.rotZ, data.rotY));
            poseStack.scale(ITEM_SIZE, ITEM_SIZE, 0.5f);
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED,
                    RenderHelper.getLightLevel(level, pos),
                    OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, level, 1);
            poseStack.popPose();
        }
    }
}
