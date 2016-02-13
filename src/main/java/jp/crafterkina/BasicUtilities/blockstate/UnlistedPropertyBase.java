/*
 * Copyright (c) 2016, CrafterKina
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package jp.crafterkina.BasicUtilities.blockstate;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * basic implements of {@link IUnlistedProperty}.
 *
 * @param <T>
 *         contains type.
 * @see IUnlistedProperty
 */
public abstract class UnlistedPropertyBase<T> implements IUnlistedProperty<T>{
    private final String name;
    private final Predicate<T> validator;

    public UnlistedPropertyBase(String name, Predicate<T> validator){
        this.name = name;
        this.validator = validator;
    }

    public UnlistedPropertyBase(String name){
        this(name, Predicates.<T>alwaysTrue());
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public boolean isValid(T value){
        return validator.apply(value);
    }

    @Override
    public abstract Class<T> getType();

    @Override
    public String valueToString(T value){
        return value.toString();
    }
}
