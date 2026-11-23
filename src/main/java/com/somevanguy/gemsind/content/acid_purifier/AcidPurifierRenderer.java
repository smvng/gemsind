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
        var texture = extensions.getStillTexture();
        //var texture = ResourceLocation.withDefaultNamespace("block/jigsaw_side");
        sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
    }

    public final TextureAtlasSprite sprite;

    public static final float PIXEL = (float) 1 / 16;

    @Override
    public void render(AcidPurifierEntity acidPurifierEntity, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        if (acidPurifierEntity.acidPackContained != null && acidPurifierEntity.acidLeft > 0) {
            VertexConsumer consumer = multiBufferSource.getBuffer(RenderType.TRANSLUCENT);

            float height = (float) acidPurifierEntity.acidLeft / acidPurifierEntity.MAX_ACID_LEFT;
            float red = (float) acidPurifierEntity.acidPackContained.r / 255;
            float green = (float) acidPurifierEntity.acidPackContained.g / 255;
            float blue = (float) acidPurifierEntity.acidPackContained.b / 255;

            poseStack.pushPose();
            RenderHelper.renderBox(poseStack, consumer,
                    PIXEL, PIXEL, PIXEL, PIXEL * 15, PIXEL + height * 14 * PIXEL, PIXEL * 15,
                    sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1(),
                    LightTexture.FULL_BRIGHT,
                    1f, red, green, blue);
            poseStack.popPose();

            // TODO:RENDER ITEMS
            //var containedGems = acidPurifierEntity.getContained();

//            poseStack.pushPose();
//            poseStack.translate(PIXEL, 0, PIXEL);
//            poseStack.scale(PIXELSUB, height * PIXELSUB, PIXELSUB);
//            blockRenderer.renderSingleBlock(Blocks.WHITE_WOOL.defaultBlockState(), poseStack, multiBufferSource, packedLight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.SOLID);
//            poseStack.popPose();
        }
    }
}
