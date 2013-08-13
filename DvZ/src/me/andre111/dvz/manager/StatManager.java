package me.andre111.dvz.manager;

import java.util.HashMap;
import java.util.Map;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.config.ConfigManager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
			
		try {
			player.setScoreboard(sc);
		} catch(Exception e) {
			DvZ.log("Exception showing "+player.getName()+" a scoreboard: "+e);
			return;
		}
	}
	//Hide them
	public static void hide(Player player, boolean force) {
		//don't hide when always shown s enabled
		if(!ConfigManager.getStaticConfig().getString("always_show_stats", "false").equals("true") || force) {
			player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		}
	}
	
	//set a stat of a Player
	public static void setStat(String player, String stat, int value) {
		Scoreboard sc = stats.get(player);
		if(sc==null) {
			sc = newScoreboard();
			stats.put(player, sc);
		}
		
		sc.getObjective(objectiveName).getScore(Bukkit.getOfflinePlayer(stat)).setScore(value);
		
		//show stats, when they should always show
		if(ConfigManager.getStaticConfig().getString("always_show_stats", "false").equals("true")) {
			Player p = Bukkit.getPlayer(player);
			if(p!=null) show(p);
		}
	}
	//set a stat for all Players
	public static void setGlobalStat(String stat, int value) {
		for(Map.Entry<String, Scoreboard> mapE : stats.entrySet()) {
			mapE.getValue().getObjective(objectiveName).getScore(Bukkit.getOfflinePlayer(stat)).setScore(value);
		}
	}
	//Set a timerstat for all players
	public static void setTimeStat(String stat, int time) {
		if(!ConfigManager.getStaticConfig().getString("scoreboard_timer_seconds", "false").equals("true")) {
			int rminutes = (int) Math.floor(time/(double)60);
			int rseconds = time - rminutes*60;
			String rsec = "" + rseconds;
			if(rsec.length()<2) rsec = "0" + rsec;
			
			String rtime = rminutes+ ":" + rsec;
			String add = stat + " " + rtime;
			
			//remove old stat
			sendRemoveTimer(stat, add);
			
			//send new stat
			if(time>0)
				sendNewTimer(add);
		}
		else
		{
			for(Map.Entry<String, Scoreboard> mapE : stats.entrySet()) {
				if(time>0)
					mapE.getValue().getObjective(objectiveName).getScore(Bukkit.getOfflinePlayer(stat)).setScore(time);
				else
					mapE.getValue().resetScores(Bukkit.getOfflinePlayer(stat));
			}
		}
	}
	//removes a timer scoreboard that starts with this name
	private static void sendRemoveTimer(String name, String newT) {
		for(Map.Entry<String, Scoreboard> mapE : stats.entrySet()) {
			for(OfflinePlayer ofP : mapE.getValue().getPlayers()) {
				if(ofP.getName().startsWith(name) && !ofP.getName().equals(newT)) {
					mapE.getValue().resetScores(ofP);
				}
			}
		}
	}
	//adds a timer
	private static void sendNewTimer(String name) {
		for(Map.Entry<String, Scoreboard> mapE : stats.entrySet()) {
			mapE.getValue().getObjective(objectiveName).getScore(Bukkit.getOfflinePlayer(name)).setScore(1000);
		}
	}
	
	public static void onInventoryClose(final Player player) {
		if(ConfigManager.getStaticConfig().getString("always_show_stats", "false").equals("true")) {
			show(player);
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
		ob.setDisplayName(ConfigManager.getLanguage().getString("scoreboard_stats", "Stats"));
		ob.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		return sc;
	}
}
