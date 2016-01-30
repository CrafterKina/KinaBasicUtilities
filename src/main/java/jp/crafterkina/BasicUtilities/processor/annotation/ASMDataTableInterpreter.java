/*
 * Copyright (c) 2016, CrafterKina
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package jp.crafterkina.BasicUtilities.processor.annotation;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.*;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.Type;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public enum ASMDataTableInterpreter{
    instance;

    private ASMDataTable dataTable;
    private Table<Class<? extends AnnotatedElement>,ModContainer,SetMultimap<Class<? extends Annotation>,Pair<Annotation,AnnotatedElement>>> annotations = HashBasedTable.create();

    public void init(FMLPreInitializationEvent event){
        dataTable = event.getAsmData();
        interpretDataTable();
    }

    @SuppressWarnings("unchecked")
    private void interpretDataTable(){
        for(ModContainer container : Loader.instance().getModList()){
            SetMultimap<String,ASMDataTable.ASMData> orig = getDataTable().getAnnotationsFor(container);
            Map<Class<? extends AnnotatedElement>,SetMultimap<Class<? extends Annotation>,Pair<Annotation,AnnotatedElement>>> converted = new Supplier<Map<Class<? extends AnnotatedElement>,SetMultimap<Class<? extends Annotation>,Pair<Annotation,AnnotatedElement>>>>(){
                @Override
                public Map<Class<? extends AnnotatedElement>,SetMultimap<Class<? extends Annotation>,Pair<Annotation,AnnotatedElement>>> get(){
                    return Maps.transformValues(Maps.<Class<? extends AnnotatedElement>,SetMultimap<Class<? extends Annotation>,Pair<Annotation,AnnotatedElement>>>newHashMap(), new Function<SetMultimap<Class<? extends Annotation>,Pair<Annotation,AnnotatedElement>>,SetMultimap<Class<? extends Annotation>,Pair<Annotation,AnnotatedElement>>>(){
                        @Nullable
                        @Override
                        public SetMultimap<Class<? extends Annotation>,Pair<Annotation,AnnotatedElement>> apply(@Nullable SetMultimap<Class<? extends Annotation>,Pair<Annotation,AnnotatedElement>> input){
                            return input == null ? HashMultimap.<Class<? extends Annotation>,Pair<Annotation,AnnotatedElement>>create() : input;
                        }
                    });
                }
            }.get();
            if(orig == null) continue;
            for(ASMDataTable.ASMData data : orig.values()){
                if(data == null) continue;
                Class<?> clazz;
                Class<? extends Annotation> annotationClass;
                Annotation annotation;
                AnnotatedElement element;
                try{
                    clazz = ClassUtils.getClass(data.getClassName(), false);
                    annotationClass = (Class<? extends Annotation>) ClassUtils.getClass(data.getAnnotationName(), false);
                }catch(ClassNotFoundException e){
                    throw new RuntimeException(e);
                }
                try{
                    element = clazz.getDeclaredField(data.getObjectName());
                    annotation = element.getAnnotation(annotationClass);
                    converted.get(Field.class).put(annotationClass, Pair.of(annotation, element));
                }catch(NoSuchFieldException ignored){
                }
                try{
                    element = ClassUtils.getClass(data.getObjectName(), false);
                    annotation = element.getAnnotation(annotationClass);
                    converted.get(Class.class).put(annotationClass, Pair.of(annotation, element));
                }catch(ClassNotFoundException ignored){
                }
                try{
                    Pair<String,Class<?>[]> pair = interpretMethod(data.getObjectName());
                    if(pair != null){
                        element = clazz.getMethod(pair.getLeft(), pair.getRight());
                        annotation = element.getAnnotation(annotationClass);
                        converted.get(Method.class).put(annotationClass, Pair.of(annotation, element));
                    }
                }catch(NoSuchMethodException ignored){
                }
            }
            annotations.column(container).putAll(converted);
        }
    }

    private Pair<String,Class<?>[]> interpretMethod(String namedesc){
        if(!namedesc.matches(".+\\(.*\\).+")){
            return null;
        }
        String name = namedesc.substring(0, namedesc.indexOf('('));
        String desc = namedesc.substring(namedesc.indexOf('('));
        Type[] args = Type.getArgumentTypes(desc);
        List<Class<?>> argClasses = Lists.newArrayListWithCapacity(args.length);
        for(Type arg : args){
            try{
                argClasses.add(ClassUtils.getClass(arg.getClassName(), false));
            }catch(ClassNotFoundException e){
                throw new IllegalArgumentException(e);
            }
        }
        return Pair.of(name, argClasses.toArray(new Class<?>[args.length]));
    }

    public ASMDataTable getDataTable(){
        return dataTable;
    }

    public Map<ModContainer,SetMultimap<Class<? extends Annotation>,Pair<Annotation,AnnotatedElement>>> getAnnotatedFieldMap(){
        return Collections.unmodifiableMap(annotations.row(Field.class));
    }

    public Map<ModContainer,SetMultimap<Class<? extends Annotation>,Pair<Annotation,AnnotatedElement>>> getAnnotatedMethodMap(){
        return Collections.unmodifiableMap(annotations.row(Method.class));
    }

    public Map<ModContainer,SetMultimap<Class<? extends Annotation>,Pair<Annotation,AnnotatedElement>>> getAnnotatedClassMap(){
        return Collections.unmodifiableMap(annotations.row(Class.class));
    }
}
