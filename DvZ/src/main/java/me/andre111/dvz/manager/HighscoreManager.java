package me.andre111.dvz.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.config.ConfigManager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class HighscoreManager {
	private static HashMap<UUID, PlayerScore> pointMap = new HashMap<UUID, PlayerScore>();
	private static HashMap<UUID, Scoreboard> playerScoreboard = new HashMap<UUID, Scoreboard>();
	
	public static void addPoints(UUID player, int add) {
		if(pointMap.containsKey(player)) {
			setPoints(player, pointMap.get(player).getPoints()+add);
		} else {
			setPoints(player, add);
		}
	}
	public static void setPoints(UUID player, int set) {
		if(!pointMap.containsKey(player)) {
			pointMap.put(player, new PlayerScore());
		}
		pointMap.get(player).setPoints(set);
	}
	
	public static int getPoints(UUID player) {
		if(pointMap.containsKey(player)) {
			return pointMap.get(player).getPoints();
		}
		return 0;
	}
	
	public static PlayerScore getPlayerScore(UUID player) {
		if(!pointMap.containsKey(player)) {
			pointMap.put(player, new PlayerScore());
		}
		return pointMap.get(player);
	}
	
	public static Scoreboard createOrRefreshPlayerScore(UUID player) {
		if(!pointMap.containsKey(player)) {
			pointMap.put(player, new PlayerScore());
		}
		PlayerScore pscore = pointMap.get(player);
		//create scoreboard
		if(!playerScoreboard.containsKey(player)) {
			Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
			playerScoreboard.put(player, sb);
			Objective ob = sb.registerNewObjective("dvz_score", "dummy");
			ob.setDisplayName(ConfigManager.getLanguage().getString("score_stats_name", "Scores/Stats"));
			ob.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
		
		Scoreboard sb = playerScoreboard.get(player);
			sb.getObjective("dvz_score").getScore(ConfigManager.getLanguage().getString("score_stats_kills", "Kills")).setScore(pscore.getKills());
			sb.getObjective("dvz_score").getScore(ConfigManager.getLanguage().getString("score_stats_deaths", "Deaths")).setScore(pscore.getDeaths());
			sb.getObjective("dvz_score").getScore(ConfigManager.getLanguage().getString("score_stats_victories", "Victories")).setScore(pscore.getVictories());
			sb.getObjective("dvz_score").getScore(ConfigManager.getLanguage().getString("score_stats_losses", "Losses")).setScore(pscore.getLosses());
			sb.getObjective("dvz_score").getScore(ConfigManager.getLanguage().getString("score_stats_classpoints", "Class-Points")).setScore(pscore.getClasspoints());
			sb.getObjective("dvz_score").getScore(ConfigManager.getLanguage().getString("score_stats_score", "Score")).setScore(pscore.getCalculatedScore());
			sb.getObjective("dvz_score").getScore(ConfigManager.getLanguage().getString("score_stats_rank", "Rank")).setScore(getRank(pscore.getCalculatedScore()));
		return sb;
	}
	private static int getRank(int score) {
		ArrayList<Integer> scores = new ArrayList<Integer>();
		
		for(PlayerScore pscore : pointMap.values()) {
			scores.add(pscore.getCalculatedScore());
		}
		
		Collections.sort(scores);
		
		return scores.indexOf(score)+1;
	}
	
	public static HashMap<UUID, Integer> getPoints() {
		HashMap<UUID, Integer> points = new HashMap<UUID, Integer>();
		
		for(UUID player : pointMap.keySet()) {
			points.put(player, pointMap.get(player).getPoints());
		}
		
		return points;
	}
	
	//save and load highscore data
	public static void saveHighscore() {
		File file = new File(DvZ.instance.getDataFolder(), "highscore.yml");
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		YamlConfiguration rewardFile = YamlConfiguration.loadConfiguration(file);

		for(UUID player : pointMap.keySet()) {
			pointMap.get(player).save(rewardFile, player);
		}
		try {
			rewardFile.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void loadHighscore() {
		File file = new File(DvZ.instance.getDataFolder(), "highscore.yml");
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		YamlConfiguration rewardFile = YamlConfiguration.loadConfiguration(file);

		for (Entry<String, Object> m : rewardFile.getValues(false).entrySet()) {
			pointMap.put(UUID.fromString(m.getKey()), PlayerScore.load(UUID.fromString(m.getKey()), (MemorySection) m.getValue()));
		}
	}
}
