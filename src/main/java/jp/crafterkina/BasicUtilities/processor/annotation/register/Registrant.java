/*
 * Copyright (c) 2016, CrafterKina
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package jp.crafterkina.BasicUtilities.processor.annotation.register;

import net.minecraft.item.ItemBlock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public @interface Registrant{
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Item{
        String name();
    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Block{
        String name();

        Class<? extends net.minecraft.item.Item> itemblock() default ItemBlock.class;

        boolean hasItem() default true;
    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Entity{
        String name();

        int trackingRange() default 250;

        int updateFrequency() default 1;

        boolean sendsVelocityUpdates() default false;
    }
}
