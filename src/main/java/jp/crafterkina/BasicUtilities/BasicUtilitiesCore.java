/*
 * Copyright (c) 2016, CrafterKina
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package jp.crafterkina.BasicUtilities;

import jp.crafterkina.BasicUtilities.processor.annotation.ASMDataTableInterpreter;
import jp.crafterkina.BasicUtilities.processor.annotation.config.ConfigProcessor;
import jp.crafterkina.BasicUtilities.processor.annotation.register.GameRegistrar;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "jp.crafterkina.BasicUtilities")
public class BasicUtilitiesCore{
    public static final Logger logger = LogManager.getLogger("jp.crafterkina.BasicUtilities");

    @EventHandler
    public void construction(FMLConstructionEvent event){
        logger.debug("Start Interpret DataTable");
        ASMDataTableInterpreter.instance.init(event.getASMHarvestedData());
        logger.debug("Start Config Parse");
        ConfigProcessor.parseConfigs();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        logger.debug("Start Game Register");
        GameRegistrar.start();
    }
}
