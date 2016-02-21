/*
 * Copyright (c) 2016, CrafterKina
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package jp.crafterkina.BasicUtilities.client.models;

import jp.crafterkina.BasicUtilities.nbt.NBTParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class ModelItemTest{
    /**
     * Try to insert texture information of itemstack to NBTTagCompound with TextureContainerWrapper or NBTParser.
     * Assert that they are same.
     */
    @Test
    public void testTextureContainerWrapperApply(){
        NBTTagCompound wrapper = new NBTTagCompound();
        NBTTagCompound parser = new NBTTagCompound();
        final ResourceLocation texture = new ResourceLocation("items/apple");
        {
            new ModelItem.TextureContainerWrapper(){
                @Override
                protected ResourceLocation getParticle(NBTTagCompound compound){
                    return texture;
                }

                @Override
                protected ResourceLocation[] getTextures(NBTTagCompound compound){
                    return new ResourceLocation[]{texture};
                }
            }.apply(wrapper);
        }
        {
            ModelItem.TextureContainer parse = NBTParser.parse(parser, ModelItem.TextureContainer.class);
            parse.setParticle(new NBTTagString(texture.toString()));
            NBTTagList list = new NBTTagList();
            list.appendTag(new NBTTagString(texture.toString()));
            parse.setTextures(list);
        }
        assertThat(wrapper, is(parser));
    }
}
