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
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "jp.crafterkina.BasicUtilities")
public class BasicUtilitiesCore{
    public static final Logger logger = LogManager.getLogger("jp.crafterkina.BasicUtilities");
    @SidedProxy(clientSide = "jp.crafterkina.BasicUtilities.client.ClientProxy", serverSide = "jp.crafterkina.BasicUtilities.server.ServerProxy")
    private static Proxy proxy;

    public static Proxy getProxy(){
        return proxy;
    }

    @EventHandler
    private void construction(FMLConstructionEvent event){
        logger.debug("Start Interpret DataTable");
        ASMDataTableInterpreter.instance.init(event.getASMHarvestedData());
        logger.debug("Start Config Parse");
        ConfigProcessor.parseConfigs();
        proxy.construct(event);
    }

    @EventHandler
    private void preInit(FMLPreInitializationEvent event){
        logger.debug("Start Game Register");
        GameRegistrar.start();
        proxy.preInit(event);
    }

    @EventHandler
    private void init(FMLInitializationEvent event){
        proxy.init(event);
    }

    @EventHandler
    private void postInit(FMLPostInitializationEvent event){
        proxy.postInit(event);
    }

    @EventHandler
    private void loaded(FMLLoadCompleteEvent event){
        proxy.loaded(event);
    }
}
