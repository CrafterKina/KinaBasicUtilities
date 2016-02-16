/*
 * Copyright (c) 2016, CrafterKina
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package jp.crafterkina.BasicUtilities;

import net.minecraftforge.fml.common.event.*;

public class Proxy{
    protected Proxy(){}

    protected void construct(FMLConstructionEvent event){}

    protected void preInit(FMLPreInitializationEvent event){}

    protected void init(FMLInitializationEvent event){}

    protected void postInit(FMLPostInitializationEvent event){}

    protected void loaded(FMLLoadCompleteEvent event){}
}
