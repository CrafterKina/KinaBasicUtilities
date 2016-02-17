/*
 * Copyright (c) 2016, CrafterKina
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package jp.crafterkina.BasicUtilities.processor.annotation.register;

import com.google.common.collect.SetMultimap;
import jp.crafterkina.BasicUtilities.processor.annotation.ASMDataTableInterpreter;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.GameData;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class GameRegistrar{
    private static Method add;

    static{
        try{
            add = FMLControlledNamespacedRegistry.class.getDeclaredMethod("add", int.class, ResourceLocation.class, Object.class);
            add.setAccessible(true);
        }catch(NoSuchMethodException e){
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static void start(){
        Map<ModContainer,SetMultimap<Class<? extends Annotation>,Pair<Annotation,AnnotatedElement>>> map = ASMDataTableInterpreter.instance.getAnnotatedClassMap();
        for(ModContainer container : map.keySet()){
            int id = 0;
            for(Map.Entry<Class<? extends Annotation>,Pair<Annotation,AnnotatedElement>> entry : map.get(container).entries()){
                if(entry.getKey() == Registrant.Item.class){
                    item(container, (Registrant.Item) entry.getValue().getLeft(), (Class<? extends Item>) entry.getValue().getRight());
                }else if(entry.getKey() == Registrant.Block.class){
                    block(container, ((Registrant.Block) entry.getValue().getLeft()), (Class<? extends Block>) entry.getValue().getRight());
                }else if(entry.getKey() == Registrant.Entity.class){
                    entity(container, ((Registrant.Entity) entry.getValue().getLeft()), (Class<? extends Entity>) entry.getValue().getRight(), id++);
                }
            }
        }
    }

    private static void item(ModContainer container, Registrant.Item annotation, Class<? extends Item> item){
        try{
            Constructor<? extends Item> constructor = item.getDeclaredConstructor();
            constructor.setAccessible(true);
            registerItem(new ResourceLocation(container.getModId(), annotation.name()), constructor.newInstance());
        }catch(InstantiationException e){
            e.printStackTrace();
        }catch(IllegalAccessException e){
            e.printStackTrace();
        }catch(InvocationTargetException e){
            e.printStackTrace();
        }catch(NoSuchMethodException e){
            e.printStackTrace();
        }
    }

    public static void registerItem(ResourceLocation name, Item item){
        try{
            add.invoke(GameData.getItemRegistry(), -1, name, item);
        }catch(IllegalAccessException e){
            e.printStackTrace();
        }catch(InvocationTargetException e){
            e.printStackTrace();
        }
    }

    private static void block(ModContainer container, Registrant.Block annotation, Class<? extends Block> block){
        try{
            Constructor<? extends Block> constructor = block.getDeclaredConstructor();
            constructor.setAccessible(true);
            registerBlock(new ResourceLocation(container.getModId(), annotation.name()), constructor.newInstance());
        }catch(NoSuchMethodException e){
            e.printStackTrace();
        }catch(IllegalAccessException e){
            e.printStackTrace();
        }catch(InstantiationException e){
            e.printStackTrace();
        }catch(InvocationTargetException e){
            e.printStackTrace();
        }
    }

    public static void registerBlock(ResourceLocation name, Block block){
        try{
            add.invoke(GameData.getBlockRegistry(), -1, name, block);
        }catch(IllegalAccessException e){
            e.printStackTrace();
        }catch(InvocationTargetException e){
            e.printStackTrace();
        }
        throw new IllegalArgumentException();
    }

    public static void mapItemBlock(ItemBlock item){
        GameData.getBlockItemMap().put(item.getBlock(), item);
    }

    private static void entity(ModContainer container, Registrant.Entity annotation, Class<? extends Entity> entity, int id){
        EntityRegistry.registerModEntity(entity, annotation.name(), id, container.getMod(), annotation.trackingRange(), annotation.updateFrequency(), annotation.sendsVelocityUpdates());
    }
}
