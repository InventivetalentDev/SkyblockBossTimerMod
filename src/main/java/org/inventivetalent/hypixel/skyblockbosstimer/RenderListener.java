package org.inventivetalent.hypixel.skyblockbosstimer;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RenderListener {

	private BossTimerMod mod;
	private DateFormat dateFormat = new SimpleDateFormat("HH:mm");

	public RenderListener(BossTimerMod mod) {
		this.mod = mod;
	}

	@SubscribeEvent()
	public void onRenderRegular(RenderGameOverlayEvent.Post event) {
		if ((Minecraft.getMinecraft().ingameGUI instanceof GuiIngameForge) && mod.util.onSkyblock) {
			if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
				//TODO
			}
		}
	}

	@SubscribeEvent
	public void onRenderDebug(RenderGameOverlayEvent.Text event) {
		if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			event.getLeft().add("");
			event.getLeft().add("On Skyblock: " + mod.util.onSkyblock);
			if (mod.util.onSkyblock) {
				event.getLeft().add("Skyblock Location: " + mod.util.location.name());
				if (mod.util.location == Util.Location.BLAZING_FORTRESS) {
					event.getLeft().add("Blaze Spawn Counter: " + mod.spawnListener.blazeSpawnCounter);
					event.getLeft().add("Magma Spawn Counter: " + mod.spawnListener.magmaSpawnCounter);
				}
			}
		}

		if (mod.util.onSkyblock) {
			if (mod.util.location == Util.Location.BLAZING_FORTRESS || Minecraft.getMinecraft().gameSettings.showDebugInfo || (mod.spawnEstimate - System.currentTimeMillis() < 1.2e+6/*20min*/)) {
				if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
					event.getRight().add("");
				}
				event.getRight().add("Magma Boss Spawn Estimate: " + mod.spawnEstimateRelative);
				event.getRight().add("(" + dateFormat.format(new Date(mod.spawnEstimate)) + ")");
			}
		}
	}

}
