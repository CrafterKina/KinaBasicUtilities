/*
 * Copyright (c) 2016, CrafterKina
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package jp.crafterkina.BasicUtilities.processor.annotation;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

public enum AnnotationHelper{
    instance;

    private ASMDataTable dataTable;
    private Map<ModContainer,SetMultimap<Class<? extends Annotation>,Pair<Annotation,Field>>> annotations;

    public void init(FMLPreInitializationEvent event){
        dataTable = event.getAsmData();
        annotations = interpretDataTable();
    }

    private Map<ModContainer,SetMultimap<Class<? extends Annotation>,Pair<Annotation,Field>>> interpretDataTable(){
        Map<ModContainer,SetMultimap<Class<? extends Annotation>,Pair<Annotation,Field>>> parent = Maps.newHashMap();
        for(ModContainer container : Loader.instance().getModList()){
            SetMultimap<String,ASMDataTable.ASMData> annotations = getDataTable().getAnnotationsFor(container);
            if(annotations == null) continue;
            SetMultimap<Class<? extends Annotation>,Pair<Annotation,Field>> child = HashMultimap.create();
            for(Map.Entry<String,ASMDataTable.ASMData> entry : annotations.entries()){
                Pair<? extends Class<? extends Annotation>,Pair<Annotation,Field>> pair = convertEntry(entry);
                if(pair == null) continue;
                child.put(pair.getKey(), pair.getValue());
            }
            parent.put(container, child);
        }
        return parent;
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

    public ASMDataTable getDataTable(){
        return dataTable;
    }

    public Map<ModContainer,SetMultimap<Class<? extends Annotation>,Pair<Annotation,Field>>> getAnnotations(){
        return Collections.unmodifiableMap(annotations);
    }
}
