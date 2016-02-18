/*
 * Copyright (c) 2016, CrafterKina
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package jp.crafterkina.BasicUtilities.nbt;

import com.google.common.base.Splitter;
import com.google.common.reflect.Reflection;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import java.lang.annotation.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ListIterator;

public enum NBTParser{
    ;

    public static <T> T parse(final NBTTagCompound compound, Class<? extends T> clazz){
        return Reflection.newProxy(clazz, new InvocationHandler(){
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
                if(compound == null) return null;
                NBTBase result = returnNBT(compound, method.getAnnotation(Return.class));
                insertNBT(compound, method.getParameterAnnotations(), args);
                return result;
            }
        });
    }

    private static void insertNBT(NBTTagCompound compound, Annotation[][] annotations, Object[] args){
        for(int i = 0; i < annotations.length; i++){
            Annotation[] annotation = annotations[i];
            Object arg = args[i];
            for(Annotation ann : annotation){
                if(!(ann instanceof Insert)) continue;
                Insert insert = (Insert) ann;
                List<String> list = Splitter.on(insert.separator()).splitToList(insert.value());
                NBTTagCompound cache = compound;
                int j = 0;
                for(; j < (list.size() - 1); j++){
                    String s = list.get(j);
                    if(!cache.hasKey(s, Constants.NBT.TAG_COMPOUND)){
                        cache.setTag(s, new NBTTagCompound());
                    }
                    cache = cache.getCompoundTag(s);
                }
                cache.setTag(list.get(j), (NBTBase) arg);
            }
        }
    }

    private static NBTBase returnNBT(NBTTagCompound compound, Return ann){
        if(ann == null) return null;
        NBTBase cache = compound;
        if(ann.value().isEmpty() && !compound.hasKey(ann.value())){
            return compound;
        }
        for(ListIterator<String> iterator = Splitter.on(ann.separator()).splitToList(ann.value()).listIterator(); iterator.hasNext(); ){
            String next = iterator.next();
            if(cache instanceof NBTTagCompound){
                if(!((NBTTagCompound) cache).hasKey(next) && iterator.hasNext()){
                    throw new IllegalArgumentException("Nonexistent tag");
                }else{
                    cache = ((NBTTagCompound) cache).getTag(next);
                }
            }else{
                throw new IllegalArgumentException("Wrong tag");
            }
        }
        return cache;
    }


    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Return{
        String value();

        String separator() default "/";
    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Insert{
        String value();

        String separator() default "/";
    }
}