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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.GameData;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
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
            add.invoke(GameData.getItemRegistry(), -1, new ResourceLocation(container.getModId(), annotation.name()), item.getDeclaredConstructor().newInstance());
        }catch(IllegalAccessException e){
            e.printStackTrace();
        }catch(InvocationTargetException e){
            e.printStackTrace();
        }catch(InstantiationException e){
            e.printStackTrace();
        }catch(NoSuchMethodException e){
            e.printStackTrace();
        }
    }

    private static void block(ModContainer container, Registrant.Block annotation, Class<? extends Block> block){
        try{
            Block instance = block.getConstructor().newInstance();
            add.invoke(GameData.getBlockRegistry(), -1, new ResourceLocation(container.getModId(), annotation.name()), instance);
            if(annotation.hasItem()){
                Item item = annotation.itemblock().getDeclaredConstructor().newInstance();
                add.invoke(GameData.getItemRegistry(), -1, new ResourceLocation(container.getModId(), annotation.name()), item);
                GameData.getBlockItemMap().put(instance, item);
            }

        }catch(IllegalAccessException e){
            e.printStackTrace();
        }catch(InvocationTargetException e){
            e.printStackTrace();
        }catch(InstantiationException e){
            e.printStackTrace();
        }catch(NoSuchMethodException e){
            e.printStackTrace();
        }
    }

    private static void entity(ModContainer container, Registrant.Entity annotation, Class<? extends Entity> entity, int id){
        EntityRegistry.registerModEntity(entity, annotation.name(), id, container.getMod(), annotation.trackingRange(), annotation.updateFrequency(), annotation.sendsVelocityUpdates());
    }
}
