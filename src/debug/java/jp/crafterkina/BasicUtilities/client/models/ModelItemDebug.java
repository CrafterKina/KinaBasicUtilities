/*
 * Copyright (c) 2016, CrafterKina
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package jp.crafterkina.BasicUtilities.client.models;

import jp.crafterkina.BasicUtilities.client.BasicModelLoader;
import jp.crafterkina.BasicUtilities.nbt.NBTParser;
import jp.crafterkina.BasicUtilities.processor.annotation.init.Initialize;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Initialize
public class ModelItemDebug extends Item{
    private static final ModelItemDebug debug;
    private static final ModelItemDebug debug3D;

    static{
        GameRegistry.registerItem(debug = new ModelItemDebug(false), "jp.crafterkina.BasicUtilities:ModelItemDebug");
        GameRegistry.registerItem(debug3D = new ModelItemDebug(true), "jp.crafterkina.BasicUtilities:ModelItemDebug3D");
        BasicModelLoader.INSTANCE.mapModel(BasicModelLoader.Model.ITEM, debug);
        BasicModelLoader.INSTANCE.mapModel(BasicModelLoader.Model.ITEM, debug3D);
    }

    {
        setCreativeTab(CreativeTabs.tabMisc);
    }

    private ModelItemDebug(boolean is3D){
        if(is3D) setFull3D();
        setUnlocalizedName("jp.crafterkina.BasicUtilities.debug:ModelItemDebug" + (is3D ? "3D" : ""));
    }

    @Override
    public boolean updateItemStackNBT(NBTTagCompound nbt){
        ModelItem.TextureContainer parse = NBTParser.parse(nbt, ModelItem.TextureContainer.class);
        parse.setParticle(new NBTTagString(new ResourceLocation("items/stick").toString()));
        NBTTagList list = new NBTTagList();
        list.appendTag(new NBTTagString(new ResourceLocation("items/stick").toString()));
        parse.setTextures(list);
        return true;
    }
}
