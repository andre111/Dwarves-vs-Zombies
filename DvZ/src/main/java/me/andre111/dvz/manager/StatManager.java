package me.andre111.dvz.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.config.ConfigManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class StatManager {
	private static HashMap<UUID, Scoreboard> stats = new HashMap<UUID, Scoreboard>();
	private static String objectiveName = "dvz_stats";
	
	//show the Playerstats
	public static void show(Player player) {
		Scoreboard sc = stats.get(player.getUniqueId());
		if(sc==null) {
			sc = newScoreboard();
			stats.put(player.getUniqueId(), sc);
		}
			
		try {
			if(player.isValid())
				player.setScoreboard(sc);
		} catch(Exception e) {
			DvZ.log("Exception showing "+player.getName()+" a scoreboard: "+e);
			return;
		}
	}
	//Hide them
	public static void hide(Player player, boolean force) {
		//don't hide when always shown s enabled
		if(!ConfigManager.getStaticConfig().getBoolean("always_show_stats", false) || force) {
			player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		}
	}
	
	//set a stat of a Player
	public static void setStat(UUID player, String stat, int value) {
		Scoreboard sc = stats.get(player);
		if(sc==null) {
			sc = newScoreboard();
			stats.put(player, sc);
		}
		
		sc.getObjective(objectiveName).getScore(stat).setScore(value);
		
		//show stats, when they should always show
		if(ConfigManager.getStaticConfig().getBoolean("always_show_stats", false)) {
			Player p = Bukkit.getPlayer(player);
			if(p!=null) show(p);
		}
	}
	//set a stat for all Players
	public static void setGlobalStat(String stat, int value, boolean hideIfZero) {
		for(Map.Entry<UUID, Scoreboard> mapE : stats.entrySet()) {
			mapE.getValue().getObjective(objectiveName).getScore(stat).setScore(value);
			if(value==0 && hideIfZero) {
				mapE.getValue().resetScores(stat);
			}
		}
	}
	//Set a timerstat for all players
	public static void setTimeStat(String stat, int time) {
		if(!ConfigManager.getStaticConfig().getBoolean("scoreboard_timer_seconds", false)) {
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
		} else {
			for(Map.Entry<UUID, Scoreboard> mapE : stats.entrySet()) {
				if(time>0)
					mapE.getValue().getObjective(objectiveName).getScore(stat).setScore(time);
				else
					mapE.getValue().resetScores(stat);
			}
		}
	}
	//removes a timer scoreboard that starts with this name
	private static void sendRemoveTimer(String name, String newT) {
		for(Map.Entry<UUID, Scoreboard> mapE : stats.entrySet()) {
			for(String ofP : mapE.getValue().getEntries()) {
				if(ofP.startsWith(name) && !ofP.equals(newT)) {
					mapE.getValue().resetScores(ofP);
				}
			}
		}
	}
	//adds a timer
	private static void sendNewTimer(String name) {
		for(Map.Entry<UUID, Scoreboard> mapE : stats.entrySet()) {
			mapE.getValue().getObjective(objectiveName).getScore(name).setScore(1000);
		}
	}
	
	public static void onInventoryClose(final Player player) {
		if(ConfigManager.getStaticConfig().getBoolean("always_show_stats", false)) {
			show(player);
		}
	}
	
	//reset stats for a Player
	public static void resetPlayer(UUID player) {
		stats.remove(player);
	}
	
	private static Scoreboard newScoreboard() {
		Scoreboard sc = Bukkit.getScoreboardManager().getNewScoreboard();
		
		sc.registerNewObjective(objectiveName, "dummy");
		Objective ob = sc.getObjective(objectiveName);
		ob.setDisplayName(ConfigManager.getLanguage().getString("scoreboard_stats", "Stats"));
		ob.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		return sc;
	}
}
