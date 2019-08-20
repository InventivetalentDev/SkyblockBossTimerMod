package org.inventivetalent.hypixel.skyblockbosstimer;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SpawnListener {

	private static final int WAVE_THRESHOLD = 20;

	private BossTimerMod mod;

	private int     tick              = 1;
	private int     seconds           = 0;
	public  int     blazeSpawnCounter = 0;
	public  int     magmaSpawnCounter = 0;
	public  String  username          = "";
	public  boolean gotSpawnMessage   = false;
	public  boolean gotDeathMessage   = false;

	public boolean blazeWaveSpawned = false;
	public boolean magmaWaveSpawned = false;
	public boolean musicPlaying     = false;
	public boolean magmaBossSpawned = false;
	public boolean magmaBossDied    = false;

	public SpawnListener(BossTimerMod mod) {
		this.mod = mod;
	}

	@SubscribeEvent
	public void on(EntityJoinWorldEvent event) {
		Entity entity = event.entity;
		if (entity == Minecraft.getMinecraft().thePlayer) {
			BossTimerMod.logger.info("Joined World!");

			username = Minecraft.getMinecraft().thePlayer.getName();

			gotSpawnMessage = false;
			gotDeathMessage = false;

			Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.CHAT,
					new TextComponentString(" "));
			Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.CHAT,
					new TextComponentString("There's an update available for the SkyblockBossTimerMod!").setStyle(new Style().setColor(TextFormatting.YELLOW)));
			Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.CHAT,
					new TextComponentString("Get it here").setStyle(new Style().setColor(TextFormatting.YELLOW).setUnderlined(true)
							.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://hypixel.inventivetalent.org/skyblock-magma-timer/mod"))));
		} else if (entity instanceof EntityLiving) {
			if (mod.util.onSkyblock) {
				if (mod.util.location == Util.Location.BLAZING_FORTRESS) {
					if (entity instanceof EntityBlaze) {
						blazeSpawnCounter++;

						if (blazeSpawnCounter > WAVE_THRESHOLD) {
							System.out.println("blaze counter: " + blazeSpawnCounter);
							blazeWaveSpawned = true;
						}
					} else if (entity instanceof EntityMagmaCube) {
						magmaSpawnCounter++;

						if (magmaSpawnCounter > WAVE_THRESHOLD) {
							System.out.println("magma counter: " + magmaSpawnCounter);
							magmaWaveSpawned = true;
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void on(ClientChatReceivedEvent event) {
		String message = event.message.getUnformattedText();
		if (mod.util.onSkyblock && mod.util.location == Util.Location.BLAZING_FORTRESS) {
			if (message.contains("The Magma Boss is spawning")) {
				gotSpawnMessage = true;
			}
			if (message.contains("MAGMA CUBE BOSS DOWN")) {
				gotDeathMessage = true;
			}
		}
	}

	@SubscribeEvent
	public void on(PlaySoundEvent event) {
		if (mod.util.onSkyblock && mod.util.location == Util.Location.BLAZING_FORTRESS) {
			String name = event.name;
			if (name.startsWith("record")) {
				musicPlaying = true;
			}
		}
	}

	@SubscribeEvent
	public void on(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			if (tick % 5 == 0) {
				if (blazeSpawnCounter > 0) {
					blazeSpawnCounter--;
				}
				if (magmaSpawnCounter > 0) {
					magmaSpawnCounter--;
				}
			}
			if (tick >= 20) {
				mod.util.checkGameAndLocation();

				if (gotSpawnMessage) {
					if (checkForMagmaBoss()) {
						gotSpawnMessage = false;
						magmaBossSpawned = true;
					} else {
						BossTimerMod.logger.warn("Received a spawn message but didn't find a boss!");
					}
				}
				if (gotDeathMessage) {
					if (!checkForMagmaBoss()) {
						gotDeathMessage = false;
						magmaBossDied = true;
					} else {
						BossTimerMod.logger.warn("Received a death message but boss still exists!");
					}
				}

				if (blazeWaveSpawned) {
					blazeWaveSpawned = false;
					mod.lastEvent = "blaze";
					mod.lastEventTime = System.currentTimeMillis();
					mod.util.postEventToServer("blaze", username);
				}
				if (magmaWaveSpawned) {
					magmaWaveSpawned = false;
					mod.lastEvent = "magma";
					mod.lastEventTime = System.currentTimeMillis();
					mod.util.postEventToServer("magma", username);
				}
				if (musicPlaying) {
					musicPlaying = false;
					mod.lastEvent = "music";
					mod.lastEventTime = System.currentTimeMillis();
					mod.util.postEventToServer("music", username);
				}
				if (magmaBossSpawned) {
					magmaBossSpawned = false;
					mod.lastEvent = "spawn";
					mod.lastEventTime = System.currentTimeMillis();
					mod.util.postEventToServer("spawn", username);
				}
				if (magmaBossDied) {
					magmaBossDied = false;
					mod.lastEvent = "death";
					mod.lastEventTime = System.currentTimeMillis();
					mod.util.postEventToServer("death", username);
				}

				tick = 1;
				seconds++;
			}
			tick++;

			if (seconds % 60 == 0) {
				if (mod.util.onSkyblock) {
					mod.util.fetchEstimateFromServer();
				}
				seconds = 1;
			} else if (seconds % 30 == 0) {
				if (mod.util.onSkyblock && mod.util.location == Util.Location.BLAZING_FORTRESS) {
					mod.util.sendPing(username);
					seconds++;
				}
			}

		}
	}

	boolean checkForMagmaBoss() {
		Minecraft minecraft = Minecraft.getMinecraft();
		for (Entity entity : minecraft.theWorld.loadedEntityList) { // Loop through all the entities.
			if (entity instanceof EntityMagmaCube) {
				EntitySlime magma = (EntitySlime) entity;
				int size = magma.getSlimeSize();
				double health = magma.getHealth();
				if (size > 10 && health > 0) { // Find a big magma boss
					BossTimerMod.logger.info("Found a big Magma Cube!");
					return true;
				}
			}
		}
		return false;
	}

}
