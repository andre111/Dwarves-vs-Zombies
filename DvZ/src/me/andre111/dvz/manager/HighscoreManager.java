package me.andre111.dvz.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.config.ConfigManager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class HighscoreManager {
	private static HashMap<String, PlayerScore> pointMap = new HashMap<String, PlayerScore>();
	private static HashMap<String, Scoreboard> playerScoreboard = new HashMap<String, Scoreboard>();
	
	public static void addPoints(String player, int add) {
		if(pointMap.containsKey(player)) {
			setPoints(player, pointMap.get(player).getPoints()+add);
		} else {
			setPoints(player, add);
		}
	}
	public static void setPoints(String player, int set) {
		if(!pointMap.containsKey(player)) {
			pointMap.put(player, new PlayerScore());
		}
		pointMap.get(player).setPoints(set);
	}
	
	public static int getPoints(String player) {
		if(pointMap.containsKey(player)) {
			return pointMap.get(player).getPoints();
		}
		return 0;
	}
	
	public static PlayerScore getPlayerScore(String player) {
		return pointMap.get(player);
	}
	
	public static Scoreboard createOrRefreshPlayerScore(String player) {
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
			sb.getObjective("dvz_score").getScore(Bukkit.getOfflinePlayer(ConfigManager.getLanguage().getString("score_stats_kills", "Kills"))).setScore(pscore.getKills());
			sb.getObjective("dvz_score").getScore(Bukkit.getOfflinePlayer(ConfigManager.getLanguage().getString("score_stats_deaths", "Deaths"))).setScore(pscore.getDeaths());
			sb.getObjective("dvz_score").getScore(Bukkit.getOfflinePlayer(ConfigManager.getLanguage().getString("score_stats_victories", "Victories"))).setScore(pscore.getVictories());
			sb.getObjective("dvz_score").getScore(Bukkit.getOfflinePlayer(ConfigManager.getLanguage().getString("score_stats_losses", "Losses"))).setScore(pscore.getLosses());
			sb.getObjective("dvz_score").getScore(Bukkit.getOfflinePlayer(ConfigManager.getLanguage().getString("score_stats_classpoints", "Class-Points"))).setScore(pscore.getClasspoints());
			sb.getObjective("dvz_score").getScore(Bukkit.getOfflinePlayer(ConfigManager.getLanguage().getString("score_stats_score", "Score"))).setScore(pscore.getCalculatedScore());
			sb.getObjective("dvz_score").getScore(Bukkit.getOfflinePlayer(ConfigManager.getLanguage().getString("score_stats_rank", "Rank"))).setScore(getRank(pscore.getCalculatedScore()));
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
	
	public static HashMap<String, Integer> getPoints() {
		HashMap<String, Integer> points = new HashMap<String, Integer>();
		
		for(String player : pointMap.keySet()) {
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

		for(String player : pointMap.keySet()) {
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
			pointMap.put(m.getKey(), PlayerScore.load(m.getKey(), (MemorySection) m.getValue()));
		}
	}
}
