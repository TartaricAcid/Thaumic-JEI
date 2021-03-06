package com.buuz135.thaumicjei;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = ThaumicJEI.MOD_ID,
        name = ThaumicJEI.MOD_NAME,
        version = ThaumicJEI.VERSION,
        dependencies = "required-after:jei@[1.12.2-4.9.1.169,);required-after:thaumcraft@[6.1.BETA10,);",
        clientSideOnly = true
)
public class ThaumicJEI {

    public static final String MOD_ID = "thaumicjei";
    public static final String MOD_NAME = "ThaumicJEI";
    public static final String VERSION = "1.2.1";
    public static Logger LOGGER;
    
    /**
     * This is the instance of your mod as created by Forge. It will never be null.
     */
    @Mod.Instance(MOD_ID)
    public static ThaumicJEI INSTANCE;

    @Mod.EventHandler
    public void onPre(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();
    }
}
