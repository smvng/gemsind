package com.somevanguy.gemsind.content.acid_purifier;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.somevanguy.gemsind.helpers.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

public class AcidPurifierRenderer implements BlockEntityRenderer<AcidPurifierEntity> {
    public final BlockRenderDispatcher blockRenderer;

    public AcidPurifierRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderer = context.getBlockRenderDispatcher();
        var extensions = IClientFluidTypeExtensions.of(Fluids.WATER);
        var stillTexture = extensions.getStillTexture();
        sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);
    }

    public final TextureAtlasSprite sprite;

    public static final float MIN = (float) 1 / 16;
    public static final float MAX = (float) 15 / 16;

    @Override
    public void render(AcidPurifierEntity acidPurifierEntity, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        if (acidPurifierEntity.acidContained != null && acidPurifierEntity.acidLeft > 0) {
            VertexConsumer consumer = multiBufferSource.getBuffer(RenderType.TRANSLUCENT);

            float height = (float) acidPurifierEntity.acidLeft / acidPurifierEntity.maxAcidLeft;
            float red = (float) acidPurifierEntity.acidContained.r / 255;
            float green = (float) acidPurifierEntity.acidContained.g / 255;
            float blue = (float) acidPurifierEntity.acidContained.b / 255;

            RenderHelper.renderBox(poseStack, consumer,
                    MIN, MIN, MIN, MAX, height * MAX, MAX,
                    sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1(),
                    LightTexture.FULL_BRIGHT,
                    1f, red, green, blue);
//            poseStack.pushPose();
//            poseStack.translate(PIXEL, 0, PIXEL);
//            poseStack.scale(PIXELSUB, height * PIXELSUB, PIXELSUB);
//            blockRenderer.renderSingleBlock(Blocks.WHITE_WOOL.defaultBlockState(), poseStack, multiBufferSource, packedLight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.SOLID);
//            poseStack.popPose();
        }
    }
}
