/*
 * Copyright (c) 2016, CrafterKina
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package jp.crafterkina.BasicUtilities.blockstate;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * simple implementation of {@link UnlistedPropertyBase}
 *
 * @param <T>
 *         contains type.
 * @see UnlistedPropertyBase
 * @see net.minecraftforge.common.property.IUnlistedProperty
 */
public class PropertyGeneral<T> extends UnlistedPropertyBase<T>{
    private final Class<T> type;

    public PropertyGeneral(String name, Class<T> type){
        this(name, Predicates.<T>alwaysTrue(), type);
    }

    public PropertyGeneral(String name, Predicate<T> validator, Class<T> type){
        super(name, validator);
        this.type = type;
    }

    @Override
    public Class<T> getType(){
        return type;
    }
}
