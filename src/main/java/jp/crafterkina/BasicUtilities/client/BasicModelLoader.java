/*
 * Copyright (c) 2016, CrafterKina
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package jp.crafterkina.BasicUtilities.client;

import jp.crafterkina.BasicUtilities.client.models.ModelCross;
import jp.crafterkina.BasicUtilities.client.models.ModelCube;
import jp.crafterkina.BasicUtilities.client.models.ModelItem;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public enum BasicModelLoader{
    INSTANCE;

    public static void mapModel(final Model model, Item item){
        ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition(){
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack){
                return new ModelResourceLocation(new ResourceLocation("jp.crafterkina.basicutilities", "router"), model.name().toLowerCase());
            }
        });
    }

    public static void mapModel(final Model model, Block block){
        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(block), new ItemMeshDefinition(){
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack){
                return new ModelResourceLocation(new ResourceLocation("jp.crafterkina.basicutilities", "router"), model.name().toLowerCase());
            }
        });
        ModelLoader.setCustomStateMapper(block, new StateMapperBase(){
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state){
                return new ModelResourceLocation(new ResourceLocation("jp.crafterkina.basicutilities", "router"), model.name().toLowerCase());
            }
        });
    }

    public void init(){
        MinecraftForge.EVENT_BUS.register(EventHandler.INSTANCE);
    }

    @SuppressWarnings("unused")
    public enum Model{
        CUBE{
            @Override
            public ModelCube getModel(){
                return new ModelCube();
            }
        },
        CROSS{
            @Override
            public ModelCross getModel(){
                return new ModelCross();
            }
        },
        ITEM{
            @Override
            public ModelItem getModel(){
                return new ModelItem();
            }
        },;

        public abstract IBakedModel getModel();
    }

    private enum EventHandler{
        INSTANCE;

        @SubscribeEvent
        protected void onModelBake(ModelBakeEvent event){
            for(Model model : Model.values()){
                event.modelRegistry.putObject(new ModelResourceLocation(new ResourceLocation("jp.crafterkina.basicutilities", "router"), model.name().toLowerCase()), model.getModel());
            }
        }
    }
}
