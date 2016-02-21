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
import jp.crafterkina.BasicUtilities.blockstate.PropertyGeneral;
import net.minecraft.block.Block;
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

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.util.Collections;
import java.util.List;

public class ModelCube implements IFlexibleBakedModel, ISmartBlockModel, ISmartItemModel, IPerspectiveAwareModel{
    private static final FaceBakery bakery = new FaceBakery();
    private static final org.lwjgl.util.vector.Vector3f start = new org.lwjgl.util.vector.Vector3f(0, 0, 0);
    private static final org.lwjgl.util.vector.Vector3f end = new org.lwjgl.util.vector.Vector3f(16, 16, 16);
    private static final BlockFaceUV uv = new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0);
    private static final Matrix4f tpv = TRSRTransformation.mul(new Vector3f(0, 1.5f / 16, -2.75f / 16), TRSRTransformation.quatFromYXZDegrees(new Vector3f(10, -45, 170)), new Vector3f(0.375f, 0.375f, 0.375f), null);
    private final ListMultimap<EnumFacing,BakedQuad> quads;
    private final TextureAtlasSprite particle;

    public ModelCube(){
        this(ImmutableListMultimap.<EnumFacing,BakedQuad>of(), ModelLoader.defaultTextureGetter().apply(new ResourceLocation("missingno")));
    }

    private ModelCube(ListMultimap<EnumFacing,BakedQuad> quads, TextureAtlasSprite particle){
        this.quads = quads;
        this.particle = particle;
    }

    @Override
    @SuppressWarnings("deprecation")
    public Pair<? extends IFlexibleBakedModel,Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType){
        return Pair.of(this, ItemCameraTransforms.TransformType.THIRD_PERSON == cameraTransformType ? tpv : null);
    }

    @Override
    public VertexFormat getFormat(){
        return Attributes.DEFAULT_BAKED_FORMAT;
    }

    @Override
    public IBakedModel handleBlockState(IBlockState raw){
        IExtendedBlockState state = (IExtendedBlockState) raw;
        TextureAtlasSprite particle = ModelLoader.defaultTextureGetter().apply(state.getValue(TextureContainer.TEXTURE).getParticle());
        final ITransformation transformation = state.getUnlistedProperties().containsKey(TextureContainer.TRANSFORM) ? state.getValue(TextureContainer.TRANSFORM) : ModelRotation.X0_Y0;
        ListMultimap<EnumFacing,BakedQuad> quads = ArrayListMultimap.create();
        for(final EnumFacing value : EnumFacing.VALUES){
            quads.putAll(value, Lists.transform(state.getValue(TextureContainer.TEXTURE).getTextures(value), new Function<Pair<Integer,ResourceLocation>,BakedQuad>(){
                @Nullable
                @Override
                public BakedQuad apply(@Nullable Pair<Integer,ResourceLocation> input){
                    input = input == null ? Pair.of(-1, new ResourceLocation("missingno")) : input;
                    return bakery.makeBakedQuad(start, end, new BlockPartFace(value, input.getLeft(), input.getRight().toString(), uv), ModelLoader.defaultTextureGetter().apply(input.getRight()), value, transformation, null, true, true);
                }
            }));
        }
        return new ModelCube(quads, particle);
    }

    @Override
    public IBakedModel handleItemState(ItemStack stack){
        IBlockState state = Block.getBlockFromItem(stack.getItem()).getStateFromMeta(stack.getItemDamage());
        if(!(state instanceof IExtendedBlockState)) return new ModelItem().handleItemState(stack);
        return handleBlockState(state);
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
        return true;
    }

    @Override
    public boolean isGui3d(){
        return true;
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


    public interface TextureContainer{
        PropertyGeneral<TextureContainer> TEXTURE = new PropertyGeneral<TextureContainer>("texture", TextureContainer.class);
        PropertyGeneral<? extends ITransformation> TRANSFORM = new PropertyGeneral<ITransformation>("transform", ITransformation.class);

        List<Pair<Integer,ResourceLocation>> getTextures(EnumFacing facing);

        ResourceLocation getParticle();
    }
}
