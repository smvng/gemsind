package com.somevanguy.gemsind.content.geode_containing_block;

import com.somevanguy.gemsind.GemstoneIndustryMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GeodeContainingBlockBakedModel implements IDynamicBakedModel {
    private final BakedModel originalModel;
    private final BakedModel overlayModel;

    public GeodeContainingBlockBakedModel(BakedModel originalModel, BakedModel overlayModel) {
        this.originalModel = originalModel;
        this.overlayModel = overlayModel;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random, ModelData modelData, @Nullable RenderType renderType) {
        BlockState replacementState = modelData.get(GeodeContainingBlockEntity.REPLACEMENT_STATE);
        if (replacementState == null) {
            return originalModel.getQuads(state, side, random, modelData, renderType);
        }
        BakedModel replacementModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(replacementState);
        List<BakedQuad> quads = new ArrayList<>();
        quads.addAll(replacementModel.getQuads(state, side, random, modelData, renderType));
        if (renderType == null || renderType == RenderType.TRANSLUCENT) {
            quads.addAll(overlayModel.getQuads(state, side, random, modelData, RenderType.TRANSLUCENT));
        }
        return quads;
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData modelData) {
        BlockState replacementState = modelData.get(GeodeContainingBlockEntity.REPLACEMENT_STATE);
        if (replacementState != null) {
            BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(replacementState);
            return ChunkRenderTypeSet.union(
                    model.getRenderTypes(state, rand, modelData),
                    ChunkRenderTypeSet.of(RenderType.TRANSLUCENT)
            );
        }
        return originalModel.getRenderTypes(state, rand, modelData);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data) {
        BlockState replacementState = data.get(GeodeContainingBlockEntity.REPLACEMENT_STATE);
        if (replacementState != null) {
            return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(replacementState);
        }
        return originalModel.getParticleIcon(data);
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return originalModel.getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides() {
        return originalModel.getOverrides();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return originalModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return originalModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return originalModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }
}
