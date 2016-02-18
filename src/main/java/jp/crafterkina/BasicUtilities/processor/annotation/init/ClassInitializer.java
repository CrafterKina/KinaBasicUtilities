/*
 * Copyright (c) 2016, CrafterKina
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package jp.crafterkina.BasicUtilities.processor.annotation.init;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.Reflection;
import jp.crafterkina.BasicUtilities.BasicUtilitiesCore;
import jp.crafterkina.BasicUtilities.processor.annotation.ASMDataTableInterpreter;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.Map;

public enum ClassInitializer{
    INSTANCE;

    private final Multimap<Class<? extends FMLEvent>,Class<?>> multimap = HashMultimap.create();

    public void prepare(){
        Map<ModContainer,SetMultimap<Class<? extends Annotation>,Pair<Annotation,AnnotatedElement>>> annotatedClassMap = ASMDataTableInterpreter.instance.getAnnotatedClassMap();
        for(SetMultimap<Class<? extends Annotation>,Pair<Annotation,AnnotatedElement>> setMultimap : annotatedClassMap.values()){
            for(Pair<Annotation,AnnotatedElement> pair : setMultimap.get(Initialize.class)){
                Initialize ann = (Initialize) pair.getLeft();
                if(!("BOTH".equals(ann.side()) || ann.side().equals(FMLLaunchHandler.side().name()))) continue;
                multimap.put(ann.value(), ((Class<?>) pair.getRight()));
            }
        }
    }

    public void run(FMLEvent event){
        Collection<Class<?>> classes = multimap.get(event.getClass());
        Reflection.initialize(classes.toArray(new Class[classes.size()]));
        BasicUtilitiesCore.logger.debug("{} classes initialized on {}", classes.size(), event.getEventType());
    }
}
