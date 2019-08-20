package org.inventivetalent.hypixel.skyblockbosstimer;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = BossTimerMod.MODID,
	 name = BossTimerMod.NAME,
	 version = BossTimerMod.VERSION)
public class BossTimerMod {

	public static final String MODID     = "skyblockbosstimer";
	public static final String NAME      = "Hypixel Skyblock Boss Timer";
	public static final String VERSION   = "@VERSION@";
	public static final String MCVERSION = "@MCVERSION@";

	public static Logger logger;

	public SpawnListener  spawnListener  = new SpawnListener(this);
	public RenderListener renderListener = new RenderListener(this);
	public Util           util           = new Util(this);

	public long   spawnEstimate         = 0;
	public String spawnEstimateRelative = "";

	public String lastEvent     = "none";
	public long   lastEventTime = 0;

	public boolean updateAvailable = false;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();

		logger.info("Mod Version: " + VERSION);
		logger.info("Minecraft Version: " + MCVERSION);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		logger.info("Hello World!");

		MinecraftForge.EVENT_BUS.register(spawnListener);
		MinecraftForge.EVENT_BUS.register(renderListener);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		ClientCommandHandler.instance.registerCommand(new TimerCommand(this));

		util.checkUpdate();
	}

}
