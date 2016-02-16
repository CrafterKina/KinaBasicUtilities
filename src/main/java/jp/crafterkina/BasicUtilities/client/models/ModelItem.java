/*
 * Copyright (c) 2016, CrafterKina
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package jp.crafterkina.BasicUtilities.client.models;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import jp.crafterkina.BasicUtilities.nbt.NBTParser;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class ModelItem implements IFlexibleBakedModel, ISmartItemModel, IPerspectiveAwareModel{
    private static final Matrix4f normalTPV = TRSRTransformation.mul(new Vector3f(0, 1, -3), TRSRTransformation.quatFromYXZDegrees(new Vector3f(-90, 0, 0)), new Vector3f(0.55f, 0.55f, 0.55f), null);
    private static final Matrix4f FPV = TRSRTransformation.mul(new Vector3f(0, 4, 2), TRSRTransformation.quatFromYXZDegrees(new Vector3f(0, -135, 25)), new Vector3f(1.7f, 1.7f, 1.7f), null);
    private static final Matrix4f full3dTPV = TRSRTransformation.mul(new Vector3f(0, 1.25f, -3.5f), TRSRTransformation.quatFromYXZDegrees(new Vector3f(0, 90, -35)), new Vector3f(0.85f, 0.85f, 0.85f), null);
    private final TextureAtlasSprite particle;
    private final List<BakedQuad> quads = Lists.newArrayList();
    private final Matrix4f TPV;

    public ModelItem(){
        this(false, Lists.newArrayList(new ResourceLocation("missingno")), new ResourceLocation("missingno"));
    }

    private ModelItem(boolean isFull3D, List<ResourceLocation> locations, ResourceLocation particle){
        TPV = isFull3D ? full3dTPV : normalTPV;
        List<TextureAtlasSprite> transform = Lists.transform(locations, ModelLoader.defaultTextureGetter());
        for(int i = 0; i < transform.size(); i++){
            TextureAtlasSprite sprite = transform.get(i);
            quads.addAll(getQuadsForSprite(i, sprite, getFormat(), Optional.<TRSRTransformation>absent()));
        }
        this.particle = ModelLoader.defaultTextureGetter().apply(particle);
    }

    private static void addSideQuad(ImmutableList.Builder<BakedQuad> builder, BitSet faces, VertexFormat format, Optional<TRSRTransformation> transform, EnumFacing side, int tint, TextureAtlasSprite sprite, int uMax, int vMax, int u, int v){
        int si = side.ordinal();
        if(si > 4) si -= 2;
        int index = (vMax + 1) * ((uMax + 1) * si + u) + v;
        if(!faces.get(index)){
            faces.set(index);
            builder.add(buildSideQuad(format, transform, side, tint, sprite, u, v));
        }
    }

    private static BakedQuad buildSideQuad(VertexFormat format, Optional<TRSRTransformation> transform, EnumFacing side, int tint, TextureAtlasSprite sprite, int u, int v){
        final float eps0 = 30e-5f;
        final float eps1 = 45e-5f;
        final float eps2 = .5f;
        final float eps3 = .5f;
        float x0 = (float) u / sprite.getIconWidth();
        float y0 = (float) v / sprite.getIconHeight();
        float x1 = x0, y1 = y0;
        float z1 = 7.5f / 16f - eps1, z2 = 8.5f / 16f + eps1;
        switch(side){
            case WEST:
                z1 = 8.5f / 16f + eps1;
                z2 = 7.5f / 16f - eps1;
            case EAST:
                y1 = (v + 1f) / sprite.getIconHeight();
                break;
            case DOWN:
                z1 = 8.5f / 16f + eps1;
                z2 = 7.5f / 16f - eps1;
            case UP:
                x1 = (u + 1f) / sprite.getIconWidth();
                break;
            default:
                throw new IllegalArgumentException("can't handle z-oriented side");
        }
        float u0 = 16f * (x0 - side.getDirectionVec().getX() * eps3 / sprite.getIconWidth());
        float u1 = 16f * (x1 - side.getDirectionVec().getX() * eps3 / sprite.getIconWidth());
        float v0 = 16f * (1f - y0 - side.getDirectionVec().getY() * eps3 / sprite.getIconHeight());
        float v1 = 16f * (1f - y1 - side.getDirectionVec().getY() * eps3 / sprite.getIconHeight());
        switch(side){
            case WEST:
            case EAST:
                y0 -= eps1;
                y1 += eps1;
                v0 -= eps2 / sprite.getIconHeight();
                v1 += eps2 / sprite.getIconHeight();
                break;
            case DOWN:
            case UP:
                x0 -= eps1;
                x1 += eps1;
                u0 += eps2 / sprite.getIconWidth();
                u1 -= eps2 / sprite.getIconWidth();
                break;
            default:
                throw new IllegalArgumentException("can't handle z-oriented side");
        }
        switch(side){
            case WEST:
                x0 += eps0;
                x1 += eps0;
                break;
            case EAST:
                x0 -= eps0;
                x1 -= eps0;
                break;
            case DOWN:
                y0 -= eps0;
                y1 -= eps0;
                break;
            case UP:
                y0 += eps0;
                y1 += eps0;
                break;
            default:
                throw new IllegalArgumentException("can't handle z-oriented side");
        }
        return buildQuad(format, transform, side.getOpposite(), tint, // getOpposite is related either to the swapping of V direction, or something else
                x0, y0, z1, sprite.getInterpolatedU(u0), sprite.getInterpolatedV(v0), x1, y1, z1, sprite.getInterpolatedU(u1), sprite.getInterpolatedV(v1), x1, y1, z2, sprite.getInterpolatedU(u1), sprite.getInterpolatedV(v1), x0, y0, z2, sprite.getInterpolatedU(u0), sprite.getInterpolatedV(v0));
    }

    private static BakedQuad buildQuad(VertexFormat format, Optional<TRSRTransformation> transform, EnumFacing side, int tint, float x0, float y0, float z0, float u0, float v0, float x1, float y1, float z1, float u1, float v1, float x2, float y2, float z2, float u2, float v2, float x3, float y3, float z3, float u3, float v3){
        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
        builder.setQuadTint(tint);
        builder.setQuadOrientation(side);
        putVertex(builder, format, transform, side, x0, y0, z0, u0, v0);
        putVertex(builder, format, transform, side, x1, y1, z1, u1, v1);
        putVertex(builder, format, transform, side, x2, y2, z2, u2, v2);
        putVertex(builder, format, transform, side, x3, y3, z3, u3, v3);
        return builder.build();
    }

    private static void putVertex(UnpackedBakedQuad.Builder builder, VertexFormat format, Optional<TRSRTransformation> transform, EnumFacing side, float x, float y, float z, float u, float v){
        Vector4f vec = new Vector4f();
        for(int e = 0; e < format.getElementCount(); e++){
            switch(format.getElement(e).getUsage()){
                case POSITION:
                    if(transform.isPresent()){
                        vec.x = x;
                        vec.y = y;
                        vec.z = z;
                        vec.w = 1;
                        transform.get().getMatrix().transform(vec);
                        builder.put(e, vec.x, vec.y, vec.z, vec.w);
                    }else{
                        builder.put(e, x, y, z, 1);
                    }
                    break;
                case COLOR:
                    builder.put(e, 1f, 1f, 1f, 1f);
                    break;
                case UV:
                    if(format.getElement(e).getIndex() == 0){
                        builder.put(e, u, v, 0f, 1f);
                        break;
                    }
                case NORMAL:
                    builder.put(e, (float) side.getFrontOffsetX(), (float) side.getFrontOffsetY(), (float) side.getFrontOffsetZ(), 0f);
                    break;
                default:
                    builder.put(e);
                    break;
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public Pair<? extends IFlexibleBakedModel,Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType){
        Matrix4f matrix4f = null;
        switch(cameraTransformType){
            case FIRST_PERSON:
                matrix4f = FPV;
                break;
            case THIRD_PERSON:
                matrix4f = TPV;
                break;
        }
        return Pair.of(this, matrix4f);
    }

    @Override
    public VertexFormat getFormat(){
        return Attributes.DEFAULT_BAKED_FORMAT;
    }

    @Override
    public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_){
        return Collections.emptyList();
    }

    @Override
    public List<BakedQuad> getGeneralQuads(){
        return quads;
    }

    @Override
    public boolean isAmbientOcclusion(){
        return true;
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

    @Override
    public IBakedModel handleItemState(ItemStack stack){
        TextureContainer container = NBTParser.parse(stack.getTagCompound(), TextureContainer.class);
        List<ResourceLocation> locations = Lists.newArrayList();
        for(int i = 0; i < container.getTextures().tagCount(); i++){
            locations.add(new ResourceLocation(container.getTextures().getStringTagAt(i)));
        }
        String rawParticle = container.getParticle().getString();
        return new ModelItem(stack.getItem().isFull3D(), locations, new ResourceLocation(rawParticle.isEmpty() ? "missingno" : rawParticle));
    }

    /** Imported from {@link ItemLayerModel} */
    public ImmutableList<BakedQuad> getQuadsForSprite(int tint, TextureAtlasSprite sprite, VertexFormat format, Optional<TRSRTransformation> transform){
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

        int uMax = sprite.getIconWidth();
        int vMax = sprite.getIconHeight();

        BitSet faces = new BitSet((uMax + 1) * (vMax + 1) * 4);
        for(int f = 0; f < sprite.getFrameCount(); f++){
            int[] pixels = sprite.getFrameTextureData(f)[0];
            boolean ptu;
            boolean[] ptv = new boolean[uMax];
            Arrays.fill(ptv, true);
            for(int v = 0; v < vMax; v++){
                ptu = true;
                for(int u = 0; u < uMax; u++){
                    boolean t = isTransparent(pixels, uMax, vMax, u, v);
                    if(ptu && !t) // left - transparent, right - opaque
                    {
                        addSideQuad(builder, faces, format, transform, EnumFacing.WEST, tint, sprite, uMax, vMax, u, v);
                    }
                    if(!ptu && t) // left - opaque, right - transparent
                    {
                        addSideQuad(builder, faces, format, transform, EnumFacing.EAST, tint, sprite, uMax, vMax, u, v);
                    }
                    if(ptv[u] && !t) // up - transparent, down - opaque
                    {
                        addSideQuad(builder, faces, format, transform, EnumFacing.UP, tint, sprite, uMax, vMax, u, v);
                    }
                    if(!ptv[u] && t) // up - opaque, down - transparent
                    {
                        addSideQuad(builder, faces, format, transform, EnumFacing.DOWN, tint, sprite, uMax, vMax, u, v);
                    }
                    ptu = t;
                    ptv[u] = t;
                }
                if(!ptu) // last - opaque
                {
                    addSideQuad(builder, faces, format, transform, EnumFacing.EAST, tint, sprite, uMax, vMax, uMax, v);
                }
            }
            // last line
            for(int u = 0; u < uMax; u++){
                if(!ptv[u]){
                    addSideQuad(builder, faces, format, transform, EnumFacing.DOWN, tint, sprite, uMax, vMax, u, vMax);
                }
            }
        }
        // front
        builder.add(buildQuad(format, transform, EnumFacing.NORTH, tint, 0, 0, 7.5f / 16f, sprite.getMinU(), sprite.getMaxV(), 0, 1, 7.5f / 16f, sprite.getMinU(), sprite.getMinV(), 1, 1, 7.5f / 16f, sprite.getMaxU(), sprite.getMinV(), 1, 0, 7.5f / 16f, sprite.getMaxU(), sprite.getMaxV()));
        // back
        builder.add(buildQuad(format, transform, EnumFacing.SOUTH, tint, 0, 0, 8.5f / 16f, sprite.getMinU(), sprite.getMaxV(), 1, 0, 8.5f / 16f, sprite.getMaxU(), sprite.getMaxV(), 1, 1, 8.5f / 16f, sprite.getMaxU(), sprite.getMinV(), 0, 1, 8.5f / 16f, sprite.getMinU(), sprite.getMinV()));
        return builder.build();
    }

    protected boolean isTransparent(int[] pixels, int uMax, int vMax, int u, int v){
        return (pixels[u + (vMax - 1 - v) * uMax] >> 24 & 0xFF) == 0;
    }

    public interface TextureContainer{
        @NBTParser.Return("model/textures")
        NBTTagList getTextures();

        void setTextures(@NBTParser.Insert("model/textures") NBTTagList locationList);

        @NBTParser.Return("model/particle")
        NBTTagString getParticle();

        void setParticle(@NBTParser.Insert("model/particle") NBTTagString location);
    }
}
