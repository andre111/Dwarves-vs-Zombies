package me.andre111.dvz.manager;

import java.io.File;
import java.io.IOException;
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
	private static Scoreboard sb;
	private static Objective ob;
	private static String objectiveName = "dvz_score";
	
	public static void init() {
		sb = Bukkit.getScoreboardManager().getNewScoreboard();
		ob = sb.registerNewObjective(objectiveName, "dummy");
		ob.setDisplayName(ConfigManager.getLanguage().getString("highscore_name", "Highscore"));
		ob.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
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
		ob.getScore(Bukkit.getOfflinePlayer(player)).setScore(set);
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
	
	public static Scoreboard getScoreboard() {
		return sb;
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
