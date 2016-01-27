/*
 * Copyright (c) 2016, CrafterKina
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package jp.crafterkina.BasicUtilities.config;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.primitives.Primitives;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

public class ConfigProcessor{
    private final ASMDataTable dataTable;
    private Map<ModContainer,SetMultimap<Class<? extends Annotation>,Pair<Annotation,Field>>> annotations;

    public ConfigProcessor(ASMDataTable dataTable){
        this.dataTable = dataTable;
    }

    public void start(){
        detectAnnotations();
        parseConfigs();
    }

    private void detectAnnotations(){
        Map<ModContainer,SetMultimap<Class<? extends Annotation>,Pair<Annotation,Field>>> parent = Maps.newHashMap();
        for(ModContainer container : Loader.instance().getModList()){
            SetMultimap<String,ASMDataTable.ASMData> annotations = dataTable.getAnnotationsFor(container);
            if(annotations == null) continue;
            SetMultimap<Class<? extends Annotation>,Pair<Annotation,Field>> child = HashMultimap.create();
            for(Map.Entry<String,ASMDataTable.ASMData> entry : annotations.entries()){
                Pair<? extends Class<? extends Annotation>,Pair<Annotation,Field>> pair = convertEntry(entry);
                if(pair == null) continue;
                child.put(pair.getKey(), pair.getValue());
            }
            parent.put(container, child);
        }
        annotations = parent;
    }

    @SuppressWarnings("unchecked")
    private Pair<? extends Class<? extends Annotation>,Pair<Annotation,Field>> convertEntry(Map.Entry<String,ASMDataTable.ASMData> orig){
        ASMDataTable.ASMData data = orig.getValue();
        if(data == null) return null;
        Class<?> clazz;
        try{
            clazz = Class.forName(data.getClassName());
        }catch(ClassNotFoundException e){
            return null;
        }
        Field field;
        try{
            field = clazz.getDeclaredField(data.getObjectName());
        }catch(NoSuchFieldException e){
            return null;
        }
        Class<? extends Annotation> annotationClazz;
        try{
            annotationClazz = (Class<? extends Annotation>) Class.forName(data.getAnnotationName());
        }catch(ClassNotFoundException e){
            return null;
        }
        Annotation annotation = field.getAnnotation(annotationClazz);
        return Pair.of(annotationClazz, Pair.of(annotation, field));
    }

    private void parseConfigs(){
        for(Map.Entry<ModContainer,SetMultimap<Class<? extends Annotation>,Pair<Annotation,Field>>> entry : annotations.entrySet()){
            if(entry.getValue().containsKey(Configurable.class)){
                String name = entry.getKey().getCustomModProperties().get("config.name");
                name = name == null ? entry.getKey().getModId() + ".cfg" : name;
                name = name.matches(".+\\..+") ? name : name + ".cfg";
                Configuration config = new Configuration(new File(Loader.instance().getConfigDir(), name));
                config.load();
                for(Pair<Annotation,Field> pair : entry.getValue().get(Configurable.class)){
                    try{
                        insert(config, (Configurable) pair.getLeft(), pair.getRight());
                    }catch(IllegalAccessException e){
                        e.printStackTrace();
                    }
                }
                config.save();
            }
        }
    }

    private void insert(Configuration config, Configurable info, Field target) throws IllegalAccessException{
        Property property;
        String name = info.name().isEmpty() ? target.getName() : info.name();
        if(target.getType().isArray()){
            if(Primitives.unwrap(target.getType().getComponentType()) == int.class){
                property = config.get(info.category(), name, (int[]) target.get(null), info.comment());
                target.set(null, property.getIntList());
                return;
            }else if(Primitives.unwrap(target.getType().getComponentType()) == boolean.class){
                property = config.get(info.category(), name, (boolean[]) target.get(null), info.comment());
                target.set(null, property.getBooleanList());
                return;
            }else if(Primitives.unwrap(target.getType().getComponentType()) == double.class){
                property = config.get(info.category(), name, (double[]) target.get(null), info.comment());
                target.set(null, property.getDoubleList());
                return;
            }else if(target.getType().getComponentType() == String.class){
                property = config.get(info.category(), name, (String[]) target.get(null), info.comment());
                target.set(null, property.getStringList());
                return;
            }
        }else{
            if(Primitives.unwrap(target.getType()) == int.class){
                property = config.get(info.category(), name, (Integer) target.get(null), info.comment());
                target.setInt(null, property.getInt());
                return;
            }else if(Primitives.unwrap(target.getType()) == boolean.class){
                property = config.get(info.category(), name, (Boolean) target.get(null), info.comment());
                target.setBoolean(null, property.getBoolean());
                return;
            }else if(Primitives.unwrap(target.getType()) == double.class){
                property = config.get(info.category(), name, (Double) target.get(null), info.comment());
                target.setDouble(null, property.getDouble());
                return;
            }else if(target.getType() == String.class){
                property = config.get(info.category(), name, (String) target.get(null), info.comment());
                target.set(null, property.getString());
                return;
            }
        }
        throw new IllegalAccessException(String.format("Illegal type %s on %s.", target.getGenericType(), target));
    }

}
