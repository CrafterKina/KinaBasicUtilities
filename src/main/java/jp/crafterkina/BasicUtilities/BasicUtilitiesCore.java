/*
 * Copyright (c) 2016, CrafterKina
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package jp.crafterkina.BasicUtilities;

import jp.crafterkina.BasicUtilities.processor.annotation.ASMDataTableInterpreter;
import jp.crafterkina.BasicUtilities.processor.annotation.config.ConfigProcessor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "jp.crafterkina.BasicUtilities")
public class BasicUtilitiesCore{
    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        ASMDataTableInterpreter.instance.init(event);
        ConfigProcessor.parseConfigs();
    }
}
