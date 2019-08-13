package org.inventivetalent.hypixel.skyblockbosstimer;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SpawnListener {

	private static final int WAVE_THRESHOLD = 20;

	private BossTimerMod mod;

	private int    tick              = 1;
	private int    seconds           = 0;
	public  int    blazeSpawnCounter = 0;
	public  int    magmaSpawnCounter = 0;
	public  String username          = "";

	public boolean blazeWaveSpawned = false;
	public boolean magmaWaveSpawned = false;
	public boolean magmaBossSpawned = false;
	public boolean magmaBossDied    = false;

	public SpawnListener(BossTimerMod mod) {
		this.mod = mod;
	}

	@SubscribeEvent
	public void on(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		if (entity == Minecraft.getMinecraft().player) {
			BossTimerMod.logger.info("Joined World!");

			username = Minecraft.getMinecraft().player.getName();
		} else if (entity instanceof EntityLiving) {
			if (mod.util.onSkyblock) {
				//				System.out.println(entity.getName());
				if (mod.util.location == Util.Location.BLAZING_FORTRESS) {
					if (entity instanceof EntityBlaze) {
						blazeSpawnCounter++;

						if (blazeSpawnCounter > WAVE_THRESHOLD) {
							System.out.println("blaze counter: " + blazeSpawnCounter);
							blazeWaveSpawned = true;
						}
					} else if (entity instanceof EntityMagmaCube) {
						EntityMagmaCube magmaCube = (EntityMagmaCube) entity;
						int size = magmaCube.getSlimeSize();
						//						System.out.println("Cube Size: " + size);

						// This doesn't seem to be working very well :/
						if (size >= 10) {// should be the boss
							magmaBossSpawned = true;
						} else {
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
	}

	@SubscribeEvent
	public void on(ClientChatReceivedEvent event) {
		String message = event.getMessage().getUnformattedText();
		if (mod.util.onSkyblock && mod.util.location == Util.Location.BLAZING_FORTRESS) {
			if (message.contains("The Magma Boss is spawning")) {
				magmaBossSpawned = true;
			}
			if (message.contains("MAGMA CUBE BOSS DOWN")) {
				magmaBossDied = true;
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

				if (blazeWaveSpawned) {
					blazeWaveSpawned = false;
					mod.util.postEventToServer("blaze", username);
				}
				if (magmaWaveSpawned) {
					magmaWaveSpawned = false;
					mod.util.postEventToServer("magma", username);
				}
				if (magmaBossSpawned) {
					magmaBossSpawned = false;
					mod.util.postEventToServer("spawn", username);
				}
				if (magmaBossDied) {
					magmaBossDied = false;
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

}
