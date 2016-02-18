/*
 * Copyright (c) 2016, CrafterKina
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package jp.crafterkina.BasicUtilities.processor.annotation.config;

import com.google.common.base.Predicate;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.primitives.Primitives;
import com.google.common.reflect.Reflection;
import jp.crafterkina.BasicUtilities.BasicUtilitiesCore;
import jp.crafterkina.BasicUtilities.processor.annotation.ASMDataTableInterpreter;
import jp.crafterkina.BasicUtilities.processor.annotation.init.Initialize;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.Map;

@Initialize(FMLConstructionEvent.class)
public enum ConfigProcessor{
    INSTANCE;

    static{
        BasicUtilitiesCore.logger.debug("Start Config Parse");
        INSTANCE.parseConfigs();
    }

    public void parseConfigs(){
        for(Map.Entry<ModContainer,SetMultimap<Class<? extends Annotation>,Pair<Annotation,AnnotatedElement>>> entry : Sets.filter(ASMDataTableInterpreter.instance.getAnnotatedFieldMap().entrySet(), new Predicate<Map.Entry<ModContainer,SetMultimap<Class<? extends Annotation>,Pair<Annotation,AnnotatedElement>>>>(){
            @Override
            public boolean apply(@Nullable Map.Entry<ModContainer,SetMultimap<Class<? extends Annotation>,Pair<Annotation,AnnotatedElement>>> input){
                return input != null && input.getValue() != null && input.getValue().keySet().contains(Configurable.class);
            }
        })){
            Map<String,String> properties = entry.getKey().getCustomModProperties();
            String name = properties == null ? null : properties.get("config.name");
            name = name == null ? entry.getKey().getModId() + ".cfg" : name;
            name = name.matches(".+\\..+") ? name : name + ".cfg";
            Configuration config = new Configuration(new File(Loader.instance().getConfigDir(), name));
            config.load();
            for(Pair<Annotation,AnnotatedElement> pair : entry.getValue().get(Configurable.class)){
                Reflection.initialize(pair.getRight() instanceof Class ? new Class<?>[]{(Class<?>) pair.getRight()} : pair.getRight() instanceof Member ? new Class<?>[]{((Member) pair.getRight()).getDeclaringClass()} : new Class<?>[]{});
                try{
                    insert(config, (Configurable) pair.getLeft(), (Field) pair.getRight());
                }catch(IllegalAccessException e){
                    e.printStackTrace();
                }
            }
            config.save();
        }
    }

    private void insert(Configuration config, Configurable info, Field target) throws IllegalAccessException{
        Property property;
        String name = info.name().isEmpty() ? target.getName() : info.name();
        if(target.getType().isArray()){
            if(Primitives.unwrap(target.getType().getComponentType()) == int.class){
                property = config.get(info.category(), name, (int[]) target.get(null), info.comment());
                if(property != null)
                target.set(null, property.getIntList());
                return;
            }else if(Primitives.unwrap(target.getType().getComponentType()) == boolean.class){
                property = config.get(info.category(), name, (boolean[]) target.get(null), info.comment());
                if(property != null)
                target.set(null, property.getBooleanList());
                return;
            }else if(Primitives.unwrap(target.getType().getComponentType()) == double.class){
                property = config.get(info.category(), name, (double[]) target.get(null), info.comment());
                if(property != null)
                target.set(null, property.getDoubleList());
                return;
            }else if(target.getType().getComponentType() == String.class){
                property = config.get(info.category(), name, (String[]) target.get(null), info.comment());
                if(property != null)
                target.set(null, property.getStringList());
                return;
            }
        }else{
            if(Primitives.unwrap(target.getType()) == int.class){
                property = config.get(info.category(), name, (Integer) target.get(null), info.comment());
                if(property != null)
                target.setInt(null, property.getInt());
                return;
            }else if(Primitives.unwrap(target.getType()) == boolean.class){
                property = config.get(info.category(), name, (Boolean) target.get(null), info.comment());
                if(property != null)
                target.setBoolean(null, property.getBoolean());
                return;
            }else if(Primitives.unwrap(target.getType()) == double.class){
                property = config.get(info.category(), name, (Double) target.get(null), info.comment());
                if(property != null)
                target.setDouble(null, property.getDouble());
                return;
            }else if(target.getType() == String.class){
                property = config.get(info.category(), name, (String) target.get(null), info.comment());
                if(property != null)
                target.set(null, property.getString());
                return;
            }
        }
        throw new IllegalAccessException(String.format("Illegal type %s on %s.", target.getGenericType(), target));
    }

}
