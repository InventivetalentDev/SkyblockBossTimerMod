package org.inventivetalent.hypixel.skyblockbosstimer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RenderListener {

	private BossTimerMod mod;
	private DateFormat   dateFormat = new SimpleDateFormat("HH:mm");

	private ResourceLocation magmaIconResourceLocation = new ResourceLocation(BossTimerMod.MODID.toLowerCase(), "textures/magma_cube.png");

	public RenderListener(BossTimerMod mod) {
		this.mod = mod;
	}

	@SubscribeEvent()
	public void onRenderRegular(RenderGameOverlayEvent.Post event) {
		if ((Minecraft.getMinecraft().ingameGUI instanceof GuiIngameForge) && mod.util.onSkyblock) {
			if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
				if (mod.spawnEstimate != 0 && (mod.util.location == Util.Location.BLAZING_FORTRESS || (mod.spawnEstimate - System.currentTimeMillis() < 1.2e+6/*20min*/))) {
					renderInfo(event.getResolution());
				}
			}
		}
	}

	public void renderInfo(ScaledResolution resolution) {
		Minecraft minecraft = Minecraft.getMinecraft();
		int width = resolution.getScaledWidth();
		int height = resolution.getScaledHeight();

		Configuration.Position position = Configuration.infoPosition;

		int xStart = 0;
		int yStart = 0;

		int infoWidth = 100;
		int infoHeight = 32;

		switch (position) {
			case TOP_LEFT:
				xStart = yStart = 0;

				xStart += Configuration.paddingLeft;
				yStart += Configuration.paddingTop;
				break;
			case TOP_RIGHT:
				xStart = width - infoWidth;
				yStart = 0;

				xStart -= Configuration.paddingRight;
				yStart += Configuration.paddingTop;
				break;
			case BOTTOM_LEFT:
				xStart = 0;
				yStart = height - infoHeight;

				xStart += Configuration.paddingLeft;
				yStart -= Configuration.paddingBottom;
				break;
			case BOTTOM_RIGHT:
				xStart = width - infoWidth;
				yStart = height - infoHeight;

				xStart -= Configuration.paddingRight;
				yStart -= Configuration.paddingBottom;
				break;
		}

		int textXOffset = 34;
		int textTopYOffset = 6;
		int textBottomYOffset = 16;

		GlStateManager.pushMatrix();
		GlStateManager.translate(xStart, yStart, 0);

		GlStateManager.pushMatrix();
		minecraft.getTextureManager().bindTexture(magmaIconResourceLocation);
		GlStateManager.scale(.125f, .125f, .125f);
		minecraft.ingameGUI.drawTexturedModalRect(0, 0, 0, 0, 256, 256);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		minecraft.ingameGUI.getFontRenderer().drawStringWithShadow(mod.spawnEstimateRelative, textXOffset, textTopYOffset, Color.white.getRGB());
		minecraft.ingameGUI.getFontRenderer().drawStringWithShadow("(" + dateFormat.format(new Date(mod.spawnEstimate)) + ")", textXOffset, textBottomYOffset, Color.white.getRGB());
		GlStateManager.popMatrix();

		GlStateManager.popMatrix();// reset translation
	}

	@SubscribeEvent
	public void onRenderText(RenderGameOverlayEvent.Text event) {
		if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			event.getLeft().add("");
			event.getLeft().add("On Skyblock: " + mod.util.onSkyblock);
			if (mod.util.onSkyblock) {
				event.getLeft().add("Skyblock Server: " + mod.util.serverId);
				event.getLeft().add("Skyblock Location: " + mod.util.location.name());
				if (mod.util.location == Util.Location.BLAZING_FORTRESS) {
					event.getLeft().add("Blaze Spawn Counter: " + mod.spawnListener.blazeSpawnCounter);
					event.getLeft().add("Magma Spawn Counter: " + mod.spawnListener.magmaSpawnCounter);
				}
				event.getLeft().add("Last Boss Event: " + mod.lastEvent + " (" + dateFormat.format(new Date(mod.lastEventTime)) + ")");

				event.getRight().add("");
				event.getRight().add("Magma Boss Spawn Estimate: " + mod.spawnEstimateRelative);
				event.getRight().add("(" + dateFormat.format(new Date(mod.spawnEstimate)) + ")");
			}
		}
	}

}
