package org.inventivetalent.hypixel.skyblockbosstimer;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Util {

	public static final Pattern SERVER_REGEX = Pattern.compile("([0-9]{2}/[0-9]{2}/[0-9]{2}) (mini[0-9]{1,3}[A-Za-z])");

	private BossTimerMod mod;

	public boolean  onSkyblock = false;
	public Location location   = Location.UNKNOWN;
	public String   serverId   = "";

	public Util(BossTimerMod mod) {
		this.mod = mod;
	}

	public void fetchEstimateFromServer() {
		new Thread(() -> {
			BossTimerMod.logger.info("GETting spawn estimate from server");

			try {
				URL url = new URL("https://hypixel.inventivetalent.org/skyblock-magma-timer/get_estimated_spawn.php");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("User-Agent", "BossTimerMod/" + BossTimerMod.VERSION);

				BossTimerMod.logger.info("Got response code " + connection.getResponseCode());

				StringBuilder response = new StringBuilder();
				try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
					String line;
					while ((line = in.readLine()) != null) {
						//							BossTimerMod.logger.debug(line);
						response.append(line);
					}
				}

				JsonObject responseJson = new Gson().fromJson(response.toString(), JsonObject.class);
				mod.spawnEstimate = responseJson.get("estimate").getAsLong();
				mod.spawnEstimateRelative = responseJson.get("estimateRelative").getAsString();
			} catch (IOException e) {
				BossTimerMod.logger.error("Failed to get spawn estimate from server", e);
			}
		}).start();
	}

	public void sendPing(final String username) {
		new Thread(() -> {
			BossTimerMod.logger.info("pinging server");

			try {
				URL url = new URL("https://hypixel.inventivetalent.org/skyblock-magma-timer/ping.php");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("User-Agent", "BossTimerMod/" + BossTimerMod.VERSION);

				String postString = "minecraftUser=" + username + "&lastFocused=" + System.currentTimeMillis() / 1000 + "&serverId=" + mod.util.serverId;
				//TODO: might wanna keep track on when the player *actually* was active ingame

				doPost(connection, postString);
			} catch (IOException e) {
				BossTimerMod.logger.error("Failed to POST event to server", e);
			}
		}).start();
	}

	public void postEventToServer(final String event, final String username) {
		new Thread(() -> {
			BossTimerMod.logger.info("POSTing " + event + " event to server");

			try {
				URL url = new URL("https://hypixel.inventivetalent.org/skyblock-magma-timer/add_event.php");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("User-Agent", "BossTimerMod/" + BossTimerMod.VERSION);

				String postString = "type=" + event + "&isModRequest=true&minecraftUser=" + username + "&serverId=" + mod.util.serverId;

				doPost(connection, postString);
			} catch (IOException e) {
				BossTimerMod.logger.error("Failed to POST event to server", e);
			}
		}).start();
	}

	private void doPost(HttpURLConnection connection, String postString) throws IOException {
		connection.setDoOutput(true);
		try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
			out.writeBytes(postString);
			out.flush();
		}

		BossTimerMod.logger.info("Got response code " + connection.getResponseCode());
		try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			String line;
			while ((line = in.readLine()) != null) {
				BossTimerMod.logger.info(line);
			}
		}
	}

	///// Stuff based on / copied from https://github.com/biscuut/SkyblockAddons/blob/master/src/main/java/codes/biscuit/skyblockaddons/utils/Utils.java

	public void checkGameAndLocation() { // Most of this is replicated from the scoreboard rendering code so not many comments here xD
		Minecraft mc = Minecraft.getMinecraft();
		if (mc != null && mc.world != null) {
			Scoreboard scoreboard = mc.world.getScoreboard();
			ScoreObjective objective = null;
			ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(mc.player.getName());
			if (scoreplayerteam != null) {
				int slot = scoreplayerteam.getColor().getColorIndex();
				if (slot >= 0) { objective = scoreboard.getObjectiveInDisplaySlot(3 + slot); }
			}
			ScoreObjective scoreobjective1 = objective != null ? objective : scoreboard.getObjectiveInDisplaySlot(1);
			if (scoreobjective1 != null) {
				objective = scoreobjective1;
				onSkyblock = stripColor(objective.getDisplayName()).startsWith("SKYBLOCK");
				scoreboard = objective.getScoreboard();
				Collection<Score> collection = scoreboard.getSortedScores(objective);
				List<Score> list = Lists.newArrayList(collection.stream().filter(score -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#")).collect(Collectors.toList()));
				if (list.size() > 15) {
					collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
				} else {
					collection = list;
				}
				for (Score score1 : collection) {
					ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(score1.getPlayerName());
					String stripped = stripColor(ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName()));
					String locationString = getStringOnlyExtensive(stripped);
					if (locationString.contains("mini")) {
						Matcher matcher = SERVER_REGEX.matcher(locationString);
						if (matcher.matches()) {
							serverId = matcher.group(2);
							continue;// skip to next line
						}
					}
					for (Location loopLocation : Location.values()) {
						if (loopLocation == Location.UNKNOWN) { continue; }
						if (locationString.endsWith(loopLocation.getScoreboardName())) {//s1.equals(" \u00A77\u23E3 \u00A7aYour Isla\uD83C\uDFC0\u00A7and")) {
							location = loopLocation;
							continue;
						}
					}
				}
			} else {
				onSkyblock = false;
			}
		} else {
			onSkyblock = false;
		}
		location = Location.UNKNOWN;
	}

	private final Pattern STRIP_COLOR_PATTERN           = Pattern.compile("(?i)" + '\u00A7' + "[0-9A-FK-OR]");
	private final Pattern STRING_ONLY_EXTENSIVE_PATTERN = Pattern.compile("[^a-z A-Z:0-9/]");

	private String getStringOnlyExtensive(String text) {
		return STRING_ONLY_EXTENSIVE_PATTERN.matcher(text).replaceAll("");
	}

	public String stripColor(final String input) {
		return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
	}

	public enum Location {
		UNKNOWN(""),
		ISLAND("Your Island"),
		BLAZING_FORTRESS("Blazing Fortress"),
		VILLAGE("Village"),
		AUCTION_HOUSE("Auction House");

		private String scoreboardName;

		Location(String scoreboardName) {
			this.scoreboardName = scoreboardName;
		}

		public String getScoreboardName() {
			return scoreboardName;
		}
	}

}
