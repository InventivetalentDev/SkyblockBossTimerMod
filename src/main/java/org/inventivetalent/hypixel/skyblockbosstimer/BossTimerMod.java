package org.inventivetalent.hypixel.skyblockbosstimer;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = BossTimerMod.MODID,
	 name = BossTimerMod.NAME,
	 version = BossTimerMod.VERSION,
	 guiFactory = "org.inventivetalent.hypixel.skyblockbosstimer.GuiFactory")
public class BossTimerMod {

	public static final String MODID   = "skyblockbosstimer";
	public static final String NAME    = "Hypixel Skyblock Boss Timer";
	public static final String VERSION = "@VERSION@";

	public static Logger logger;

	public SpawnListener  spawnListener  = new SpawnListener(this);
	public RenderListener renderListener = new RenderListener(this);
	public Util           util           = new Util(this);

	public long   spawnEstimate
			= 0;
	public String spawnEstimateRelative = "";

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		logger.info("Hello World!");

		MinecraftForge.EVENT_BUS.register(spawnListener);
		MinecraftForge.EVENT_BUS.register(renderListener);

		MinecraftForge.EVENT_BUS.register(this);


		ConfigWrapper.init(new File(Loader.instance().getConfigDir(), NAME + ".cfg"));
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		ClientCommandHandler.instance.registerCommand(new TimerCommand(this));
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.modID.equals(MODID)) {
			ConfigWrapper.reloadConfig();
		}
	}

}
