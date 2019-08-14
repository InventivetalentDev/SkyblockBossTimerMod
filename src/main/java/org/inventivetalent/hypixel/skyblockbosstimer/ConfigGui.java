package org.inventivetalent.hypixel.skyblockbosstimer;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class ConfigGui extends GuiConfig {

	public ConfigGui(GuiScreen parent) {
		super(parent, new ConfigElement(ConfigWrapper.config.getCategory(Configuration.CATEGORY_GENERAL))
				.getChildElements(), BossTimerMod.MODID, false, false, "BossTimerMod Config");
	}
}
