package org.inventivetalent.hypixel.skyblockbosstimer;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = BossTimerMod.MODID,
		name = BossTimerMod.NAME)
public class Configuration {

	public static Position infoPosition  = Position.TOP_RIGHT;
	public static int      paddingLeft   = 2;
	public static int      paddingRight  = 2;
	public static int      paddingTop    = 2;
	public static int      paddingBottom = 2;

	public enum Position {
		TOP_LEFT,
		TOP_RIGHT,
		BOTTOM_LEFT,
		BOTTOM_RIGHT;
	}

	@Mod.EventBusSubscriber(modid = BossTimerMod.MODID)
	private static class EventHandler {

		/**
		 * Inject the new values and save to the config file when the config has been changed from the GUI.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(BossTimerMod.MODID)) {
				ConfigManager.sync(BossTimerMod.MODID, Config.Type.INSTANCE);
			}
		}
	}

}
