/*
 * Copyright (c) 2016, CrafterKina
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package jp.crafterkina.BasicUtilities;

import jp.crafterkina.BasicUtilities.processor.annotation.ASMDataTableInterpreter;
import jp.crafterkina.BasicUtilities.processor.annotation.init.ClassInitializer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "jp.crafterkina.BasicUtilities")
public class BasicUtilitiesCore{
    public static final Logger logger = LogManager.getLogger("jp.crafterkina.BasicUtilities");

    @EventHandler
    private void construction(FMLConstructionEvent event){
        logger.debug("Start Interpret DataTable");
        ASMDataTableInterpreter.instance.init(event.getASMHarvestedData());
        logger.debug("Start Initializer Prepare");
        ClassInitializer.INSTANCE.prepare();
        ClassInitializer.INSTANCE.run(event);
    }

    @EventHandler
    private void fingerprint(FMLFingerprintViolationEvent event){
        ClassInitializer.INSTANCE.run(event);
    }

    @EventHandler
    private void preInit(FMLPreInitializationEvent event){
        ClassInitializer.INSTANCE.run(event);
    }

    @EventHandler
    private void init(FMLInitializationEvent event){
        ClassInitializer.INSTANCE.run(event);
    }

    @Mod.EventHandler
    private void receiveIMC(FMLInterModComms.IMCEvent event){
        ClassInitializer.INSTANCE.run(event);
    }

    @EventHandler
    private void postInit(FMLPostInitializationEvent event){
        ClassInitializer.INSTANCE.run(event);
    }

    @EventHandler
    private void loaded(FMLLoadCompleteEvent event){
        ClassInitializer.INSTANCE.run(event);
    }

    @Mod.EventHandler
    private void toStartServer(FMLServerAboutToStartEvent event){
        ClassInitializer.INSTANCE.run(event);
    }

    @Mod.EventHandler
    private void startingServer(FMLServerStartingEvent event){
        ClassInitializer.INSTANCE.run(event);
    }

    @Mod.EventHandler
    private void startedServer(FMLServerStartedEvent event){
        ClassInitializer.INSTANCE.run(event);
    }

    @Mod.EventHandler
    private void stoppingServer(FMLServerStoppingEvent event){
        ClassInitializer.INSTANCE.run(event);
    }

    @Mod.EventHandler
    private void stoppedServer(FMLServerStoppedEvent event){
        ClassInitializer.INSTANCE.run(event);
    }

    @Mod.EventHandler
    private void disabled(FMLModDisabledEvent event){
        ClassInitializer.INSTANCE.run(event);
    }
}
