/*
 * Copyright (c) 2016, CrafterKina
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package jp.crafterkina.BasicUtilities.client.models;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ModelCross implements IFlexibleBakedModel, ISmartBlockModel, ISmartItemModel{
    private static final FaceBakery bakery = new FaceBakery();
    private static final Vector3f startZ = new Vector3f(0.8f, 0, 8);
    private static final Vector3f endZ = new Vector3f(15.2f, 16, 8);
    private static final Vector3f startX = new Vector3f(8, 0, 0.8f);
    private static final Vector3f endX = new Vector3f(8, 16, 15.2f);
    private static final BlockFaceUV uv = new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0);
    private static final BlockPartRotation rotation = new BlockPartRotation(new Vector3f(8, 8, 8), EnumFacing.Axis.Y, 45, true);
    private final ListMultimap<EnumFacing,BakedQuad> quads;
    private final TextureAtlasSprite particle;

    public ModelCross(){
        this(ImmutableListMultimap.<EnumFacing,BakedQuad>of(), ModelLoader.defaultTextureGetter().apply(new ResourceLocation("missingno")));
    }

    private ModelCross(ListMultimap<EnumFacing,BakedQuad> quads, TextureAtlasSprite particle){
        this.quads = quads;
        this.particle = particle;
    }

    @Override
    public VertexFormat getFormat(){
        return Attributes.DEFAULT_BAKED_FORMAT;
    }

    @Override
    public IBakedModel handleBlockState(IBlockState raw){
        IExtendedBlockState state = (IExtendedBlockState) raw;
        TextureAtlasSprite particle = ModelLoader.defaultTextureGetter().apply(state.getValue(ModelCube.TextureContainer.TEXTURE).getParticle());
        final ITransformation transformation = state.getUnlistedProperties().containsKey(ModelCube.TextureContainer.TRANSFORM) ? state.getValue(ModelCube.TextureContainer.TRANSFORM) : ModelRotation.X0_Y0;
        ListMultimap<EnumFacing,BakedQuad> quads = ArrayListMultimap.create();
        quads.putAll(EnumFacing.NORTH, Lists.transform(state.getValue(ModelCube.TextureContainer.TEXTURE).getTextures(EnumFacing.NORTH), new Function<Pair<Integer,ResourceLocation>,BakedQuad>(){
            @Nullable
            @Override
            public BakedQuad apply(@Nullable Pair<Integer,ResourceLocation> input){
                input = input == null ? Pair.of(-1, new ResourceLocation("missingno")) : input;
                return bakery.makeBakedQuad(startZ, endZ, new BlockPartFace(EnumFacing.NORTH, input.getLeft(), input.toString(), uv), ModelLoader.defaultTextureGetter().apply(input.getRight()), EnumFacing.NORTH, transformation, rotation, true, false);
            }
        }));
        quads.putAll(EnumFacing.SOUTH, Lists.transform(state.getValue(ModelCube.TextureContainer.TEXTURE).getTextures(EnumFacing.SOUTH), new Function<Pair<Integer,ResourceLocation>,BakedQuad>(){
            @Nullable
            @Override
            public BakedQuad apply(@Nullable Pair<Integer,ResourceLocation> input){
                input = input == null ? Pair.of(-1, new ResourceLocation("missingno")) : input;
                return bakery.makeBakedQuad(startZ, endZ, new BlockPartFace(EnumFacing.SOUTH, input.getLeft(), input.toString(), uv), ModelLoader.defaultTextureGetter().apply(input.getRight()), EnumFacing.SOUTH, transformation, rotation, true, false);
            }
        }));
        quads.putAll(EnumFacing.WEST, Lists.transform(state.getValue(ModelCube.TextureContainer.TEXTURE).getTextures(EnumFacing.WEST), new Function<Pair<Integer,ResourceLocation>,BakedQuad>(){
            @Nullable
            @Override
            public BakedQuad apply(@Nullable Pair<Integer,ResourceLocation> input){
                input = input == null ? Pair.of(-1, new ResourceLocation("missingno")) : input;
                return bakery.makeBakedQuad(startX, endX, new BlockPartFace(EnumFacing.WEST, input.getLeft(), input.toString(), uv), ModelLoader.defaultTextureGetter().apply(input.getRight()), EnumFacing.WEST, transformation, rotation, true, false);
            }
        }));
        quads.putAll(EnumFacing.EAST, Lists.transform(state.getValue(ModelCube.TextureContainer.TEXTURE).getTextures(EnumFacing.EAST), new Function<Pair<Integer,ResourceLocation>,BakedQuad>(){
            @Nullable
            @Override
            public BakedQuad apply(@Nullable Pair<Integer,ResourceLocation> input){
                input = input == null ? Pair.of(-1, new ResourceLocation("missingno")) : input;
                return bakery.makeBakedQuad(startX, endX, new BlockPartFace(EnumFacing.EAST, input.getLeft(), input.toString(), uv), ModelLoader.defaultTextureGetter().apply(input.getRight()), EnumFacing.EAST, transformation, rotation, true, false);
            }
        }));
        return new ModelCross(quads, particle);
    }

    @Override
    public IBakedModel handleItemState(ItemStack stack){
        return new ModelItem().handleItemState(stack);
    }

    @Override
    public List<BakedQuad> getFaceQuads(EnumFacing facing){
        return quads.get(facing);
    }

    @Override
    public List<BakedQuad> getGeneralQuads(){
        return Collections.emptyList();
    }

    @Override
    public boolean isAmbientOcclusion(){
        return false;
    }

    @Override
    public boolean isGui3d(){
        return false;
    }

    @Override
    public boolean isBuiltInRenderer(){
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture(){
        return particle;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemCameraTransforms getItemCameraTransforms(){
        return ItemCameraTransforms.DEFAULT;
    }
}
