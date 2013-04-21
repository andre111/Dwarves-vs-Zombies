package me.andre111.dvz;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class StatManager {
	private static HashMap<String, Scoreboard> stats = new HashMap<String, Scoreboard>();
	private static String objectiveName = "dvz_stats";
	
	//show the Playerstats
	public static void show(Player player) {
		Scoreboard sc = stats.get(player.getName());
		if(sc==null) {
			sc = newScoreboard();
			stats.put(player.getName(), sc);
		}
			
		player.setScoreboard(sc);
	}
	//Hide them
	public static void hide(Player player) {
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}
	
	//set a stat of a Player
	public static void setStat(String player, String stat, int value) {
		Scoreboard sc = stats.get(player);
		if(sc==null) {
			sc = newScoreboard();
			stats.put(player, sc);
		}
		
		sc.getObjective(objectiveName).getScore(Bukkit.getOfflinePlayer(stat)).setScore(value);
	}
	//set a stat for all Players
	public static void setGlobalStat(String stat, int value) {
		for(Map.Entry<String, Scoreboard> mapE : stats.entrySet()) {
			mapE.getValue().getObjective(objectiveName).getScore(Bukkit.getOfflinePlayer(stat)).setScore(value);
		}
	}
	//reset stats for a Player
	public static void resetPlayer(String player) {
		stats.remove(player);
	}
	
	private static Scoreboard newScoreboard() {
		Scoreboard sc = Bukkit.getScoreboardManager().getNewScoreboard();
		
		sc.registerNewObjective(objectiveName, "dummy");
		Objective ob = sc.getObjective("dvz_stats");
		ob.setDisplayName(DvZ.getLanguage().getString("scoreboard_stats", "Stats"));
		ob.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		return sc;
	}
}
