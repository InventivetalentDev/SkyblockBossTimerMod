package org.inventivetalent.hypixel.skyblockbosstimer;

import java.io.File;

public class ConfigWrapper {

	static net.minecraftforge.common.config.Configuration config;

	static String[] positionValueStrings;

	static {
		positionValueStrings = new String[Configuration.Position.values().length];
		for (int i = 0; i < positionValueStrings.length; i++) {
			positionValueStrings[i] = Configuration.Position.values()[i].name();
		}
	}

	public static void init(File file) {
		config = new net.minecraftforge.common.config.Configuration(file);
		reloadConfig();
		config.load();
	}

	public static void reloadConfig() {
		Configuration.infoPosition = Configuration.Position.valueOf(config.getString("general", "infoPosition", Configuration.infoPosition.name(), "Position of the UI", positionValueStrings));
		Configuration.paddingLeft = config.get("general", "paddingLeft", Configuration.paddingLeft).getInt();
		Configuration.paddingRight = config.get("general", "paddingRight", Configuration.paddingRight).getInt();
		Configuration.paddingBottom = config.get("general", "paddingBottom", Configuration.paddingBottom).getInt();
		Configuration.paddingTop = config.get("general", "paddingTop", Configuration.paddingTop).getInt();

		config.save();
	}

}
