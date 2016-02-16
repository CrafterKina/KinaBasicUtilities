/*
 * Copyright (c) 2016, CrafterKina
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package jp.crafterkina.BasicUtilities.client;

import jp.crafterkina.BasicUtilities.BasicUtilitiesCore;
import jp.crafterkina.BasicUtilities.Proxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends Proxy{
    @Override
    protected void preInit(FMLPreInitializationEvent event){
        BasicUtilitiesCore.logger.debug("Start ModelLoader Initialize");
        BasicModelLoader.INSTANCE.init();
    }
}
