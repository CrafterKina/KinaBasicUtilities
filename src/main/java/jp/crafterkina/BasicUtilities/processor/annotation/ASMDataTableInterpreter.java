/*
 * Copyright (c) 2016, CrafterKina
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package jp.crafterkina.BasicUtilities.processor.annotation;

import com.google.common.base.Predicate;
import com.google.common.collect.*;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nullable;
import java.io.IOException;
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

    public void init(ASMDataTable table){
        dataTable = table;
        interpretDataTable();
    }

    @SuppressWarnings("unchecked")
    private void interpretDataTable(){
        for(ModContainer container : Loader.instance().getModList()){
            SetMultimap<String,ASMDataTable.ASMData> orig = getDataTable().getAnnotationsFor(container);
            Map<Class<? extends AnnotatedElement>,SetMultimap<Class<? extends Annotation>,Pair<Annotation,AnnotatedElement>>> converted = Maps.newHashMap();
            converted.put(Class.class, HashMultimap.<Class<? extends Annotation>,Pair<Annotation,AnnotatedElement>>create());
            converted.put(Field.class, HashMultimap.<Class<? extends Annotation>,Pair<Annotation,AnnotatedElement>>create());
            converted.put(Method.class, HashMultimap.<Class<? extends Annotation>,Pair<Annotation,AnnotatedElement>>create());
            if(orig == null) continue;
            for(final ASMDataTable.ASMData data : orig.values()){
                if(data == null) continue;
                Class<?> clazz;
                Class<? extends Annotation> annotationClass;
                Annotation annotation;
                AnnotatedElement element;
                ClassReader reader;
                try{
                    reader = new ClassReader(data.getClassName());
                }catch(IOException e){
                    continue;
                }
                ClassNode node = new ClassNode();
                reader.accept(node, 0);
                try{
                    if(isInvalid(node.visibleAnnotations, FMLLaunchHandler.side().name())){
                        continue;
                    }
                    clazz = ClassUtils.getClass(data.getClassName(), false);
                    annotationClass = (Class<? extends Annotation>) ClassUtils.getClass(data.getAnnotationName(), false);
                }catch(ClassNotFoundException e){
                    continue;
                }
                try{
                    if(!Collections2.filter(node.fields, new Predicate<FieldNode>(){
                        @Override
                        public boolean apply(@Nullable FieldNode input){
                            return input != null && input.name.equals(data.getObjectName()) && !isInvalid(input.visibleAnnotations, FMLLaunchHandler.side().name());
                        }
                    }).isEmpty()){
                        element = clazz.getDeclaredField(data.getObjectName());
                        annotation = element.getAnnotation(annotationClass);
                        converted.get(Field.class).put(annotationClass, Pair.of(annotation, element));
                    }
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
                    if(!Collections2.filter(node.methods, new Predicate<MethodNode>(){
                        @Override
                        public boolean apply(@Nullable MethodNode input){
                            return input != null && (input.name + input.desc).equals(data.getObjectName()) && !isInvalid(input.visibleAnnotations, FMLLaunchHandler.side().name());
                        }
                    }).isEmpty()){
                        if(pair != null){
                            element = clazz.getMethod(pair.getLeft(), pair.getRight());
                            annotation = element.getAnnotation(annotationClass);
                            converted.get(Method.class).put(annotationClass, Pair.of(annotation, element));
                        }
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

    private boolean isInvalid(List<AnnotationNode> anns, String side){
        if(anns == null){
            return false;
        }
        for(AnnotationNode ann : anns){
            if(ann.desc.equals(Type.getDescriptor(SideOnly.class))){
                if(ann.values != null){
                    for(int x = 0; x < ann.values.size() - 1; x += 2){
                        Object key = ann.values.get(x);
                        Object value = ann.values.get(x + 1);
                        if(key instanceof String && "value".equals(key)){
                            if(value instanceof String[]){
                                if(!((String[]) value)[1].equals(side)){
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
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
