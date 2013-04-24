package me.andre111.dvz;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.events.PacketContainer;

public class StatManager {
	private static HashMap<String, Scoreboard> stats = new HashMap<String, Scoreboard>();
	private static String objectiveName = "dvz_stats";
	
	private static HashMap<String, Integer> xpBarLevel = new HashMap<String, Integer>();
	private static HashMap<String, Float> xpBarXp = new HashMap<String, Float>();
	private static HashMap<String, Boolean> xpBarShown = new HashMap<String, Boolean>();
	
	//show the Playerstats
	public static void show(Player player) {
		Scoreboard sc = stats.get(player.getName());
		if(sc==null) {
			sc = newScoreboard();
			stats.put(player.getName(), sc);
		}
			
		player.setScoreboard(sc);
		
		//xp-bar
		xpBarShown.put(player.getName(), true);
		if(xpBarLevel.containsKey(player.getName())) {
			sendFakeXP(player, xpBarLevel.get(player.getName()), xpBarXp.get(player.getName()));
		}
	}
	//Hide them
	public static void hide(Player player) {
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		
		//xp-bar
		xpBarShown.put(player.getName(), false);
		sendRealXP(player);
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
	//set the xp level of the player
	public static void setXPBarStat(String player, int level, float xp) {
		xpBarLevel.put(player, level);
		xpBarXp.put(player, xp);
		
		if(xpBarShown.containsKey(player)) {
			if(xpBarShown.get(player)) {
				Player p = Bukkit.getServer().getPlayerExact(player);
				
				if(p!=null) {
					sendFakeXP(p, level, xp);
				}
			}
		}
	}
	//called, when to real xp changes(to hide the change)
	public static void updateXPBarStat(Player player) {
		if(xpBarShown.containsKey(player.getName())) {
			if(xpBarShown.get(player.getName())) {
				int level = xpBarLevel.get(player.getName());
				float xp = xpBarXp.get(player.getName());
				
				sendFakeXP(player, level, xp);
			}
		}
	}
	//reset stats for a Player
	public static void resetPlayer(String player) {
		stats.remove(player);
		
		xpBarXp.remove(player);
		xpBarLevel.remove(player);
		xpBarShown.remove(player);
	}
	
	private static Scoreboard newScoreboard() {
		Scoreboard sc = Bukkit.getScoreboardManager().getNewScoreboard();
		
		sc.registerNewObjective(objectiveName, "dummy");
		Objective ob = sc.getObjective("dvz_stats");
		ob.setDisplayName(DvZ.getLanguage().getString("scoreboard_stats", "Stats"));
		ob.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		return sc;
	}
	private static void sendFakeXP(Player player, int level, float xp) {
		PacketContainer fakeXPChange = DvZ.protocolManager.createPacket(Packets.Server.SET_EXPERIENCE);
		
		fakeXPChange.getFloat().
			write(0, xp);
		fakeXPChange.getIntegers().
			write(1, level);
		
		try {
			DvZ.protocolManager.sendServerPacket(player, fakeXPChange);
		} catch (InvocationTargetException e) {
		}
	}
	private static void sendRealXP(Player player) {
		PacketContainer fakeXPChange = DvZ.protocolManager.createPacket(Packets.Server.SET_EXPERIENCE);
		
		fakeXPChange.getFloat().
			write(0, player.getExp());
		fakeXPChange.getIntegers().
			write(0, player.getTotalExperience()).
			write(1, player.getLevel());
		
		try {
			DvZ.protocolManager.sendServerPacket(player, fakeXPChange);
		} catch (InvocationTargetException e) {
		}
	}
}
