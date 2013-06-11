package me.andre111.dvz;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.iface.IUpCounter;

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
	
	private static HashMap<String, IUpCounter> counters = new HashMap<String, IUpCounter>();
	private static HashMap<String, String> countervars = new HashMap<String, String>();
	private static HashMap<String, Integer> counterCurrent = new HashMap<String, Integer>();
	private static boolean running = false;
	
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
			
		try {
			player.setScoreboard(sc);
		} catch(Exception e) {
			return;
		}
		
		//xp-bar
		xpBarShown.put(player.getName(), true);
		if(xpBarLevel.containsKey(player.getName())) {
			sendFakeXP(player, xpBarLevel.get(player.getName()), xpBarXp.get(player.getName()));
		}
	}
	//Hide them
	public static void hide(Player player) {
		//don't hide when always shown s enabled
		if(!ConfigManager.getStaticConfig().getString("always_show_stats", "false").equals("true")) {
			player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			
			//xp-bar
			xpBarShown.put(player.getName(), false);
			sendRealXP(player);
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
	//set the xp level of the player
	public static void setXPBarStat(String player, int level, float xp) {
		xpBarLevel.put(player, level);
		xpBarXp.put(player, xp);
		
		//check if a countup is shown
		if(counters.containsKey(player)) return;
		
		//show stats, when they should always show
		if(ConfigManager.getStaticConfig().getString("always_show_stats", "false").equals("true")) {
			Player p = Bukkit.getPlayer(player);
			if(p!=null) show(p);
		}
		
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
		//check if a countup is shown
		if(counters.containsKey(player.getName())) return;
		
		//show stats, when they should always show
		if(ConfigManager.getStaticConfig().getString("always_show_stats", "false").equals("true")) {
			show(player);
		}

		if(xpBarShown.containsKey(player.getName())) {
			if(xpBarShown.get(player.getName())) {
				int level = xpBarLevel.get(player.getName());
				float xp = xpBarXp.get(player.getName());
				
				sendFakeXP(player, level, xp);
			}
		}
	}
	
	public static void onInventoryOpen(Player player) {
		if(ConfigManager.getStaticConfig().getString("always_show_stats", "false").equals("true")) {
			sendRealXP(player);
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
		
		xpBarXp.remove(player);
		xpBarLevel.remove(player);
		xpBarShown.remove(player);
	}
	
	private static Scoreboard newScoreboard() {
		Scoreboard sc = Bukkit.getScoreboardManager().getNewScoreboard();
		
		sc.registerNewObjective(objectiveName, "dummy");
		Objective ob = sc.getObjective("dvz_stats");
		ob.setDisplayName(ConfigManager.getLanguage().getString("scoreboard_stats", "Stats"));
		ob.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		return sc;
	}
	private static void sendFakeXP(final Player player, int level, float xp) {
		final PacketContainer fakeXPChange = DvZ.protocolManager.createPacket(Packets.Server.SET_EXPERIENCE);
		
		fakeXPChange.getFloat().
			write(0, xp);
		fakeXPChange.getIntegers().
			write(1, level);
		
		Bukkit.getScheduler().runTask(DvZ.instance, new Runnable() {
			public void run() {
				try {
					DvZ.protocolManager.sendServerPacket(player, fakeXPChange);
				} catch (InvocationTargetException e) {
				}
			}
		});
	}
	private static void sendRealXP(final Player player) {
		final PacketContainer fakeXPChange = DvZ.protocolManager.createPacket(Packets.Server.SET_EXPERIENCE);
		
		fakeXPChange.getFloat().
			write(0, player.getExp());
		fakeXPChange.getIntegers().
			write(0, player.getTotalExperience()).
			write(1, player.getLevel());
		
		Bukkit.getScheduler().runTask(DvZ.instance, new Runnable() {
			public void run() {
				try {
					DvZ.protocolManager.sendServerPacket(player, fakeXPChange);
				} catch (InvocationTargetException e) {
				}
			}
		});
	}
	
	//UpCounter
	public static void setCounter(String player, IUpCounter counter, String vars) {
		if(!running) {
			Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(DvZ.instance, new Runnable() {
				public void run() {
					updateCounters();
				}
			}, 20, 20);
			
			running = true;
		}
		
		if(counters.get(player)!=null)
		if(!counters.get(player).countUPOverridable()) return;
	
		interruptCounter(counters.get(player), player);
		
		counters.put(player, counter);
		countervars.put(player, vars);
		counterCurrent.put(player, 0);
	}
	public static void setCounterVars(String player, String vars) {
		countervars.put(player, vars);
	}
	public static void interruptMove(String player) {
		if(counters.containsKey(player)) {
			IUpCounter counter = counters.get(player);
			
			if(counter.countUPinterruptMove()) {
				interruptCounter(counter, player);
			}
		}
	}
	public static void interruptDamage(String player) {
		if(counters.containsKey(player)) {
			IUpCounter counter = counters.get(player);
			
			if(counter.countUPinterruptDamage()) {
				interruptCounter(counter, player);
			}
		}
	}
	public static void interruptItem(String player) {
		if(counters.containsKey(player)) {
			IUpCounter counter = counters.get(player);
			
			if(counter.countUPinterruptItemChange()) {
				interruptCounter(counter, player);
			}
		}
	}
	private static void interruptCounter(IUpCounter counter, String player) {
		if(counter!=null)
			counter.countUPinterrupt(countervars.get(player));
		
		counters.remove(player);
		countervars.remove(player);
		counterCurrent.remove(player);
		
		Player p = Bukkit.getServer().getPlayerExact(player);
		if(p!=null) {
			sendRealXP(p);
			
			//show stats, when they should always show
			if(ConfigManager.getStaticConfig().getString("always_show_stats", "false").equals("true")) {
				show(p);
			}
		}
	}
	private static void updateCounters() {
		ArrayList<String> remove = new ArrayList<String>();
		
		for(Map.Entry<String, IUpCounter> entry : counters.entrySet()) {
			String player = entry.getKey();
			IUpCounter counter = entry.getValue();
			int cu = counterCurrent.get(player);
			
			cu += counter.countUPperSecond();
			
			//remove
			if(cu>=counter.countUPgetMax()) {
				counter.countUPfinish(countervars.get(player));
				
				Player p = Bukkit.getServer().getPlayerExact(player);
				if(p!=null) {
					sendRealXP(p);
					
					//show stats, when they should always show
					if(ConfigManager.getStaticConfig().getString("always_show_stats", "false").equals("true")) {
						show(p);
					}
				}
				
				remove.add(player);
			}
			//send
			else {
				counterCurrent.put(player, cu);
				counter.countUPincrease(countervars.get(player));
				
				Player p = Bukkit.getServer().getPlayerExact(player);
				if(p!=null) {
					sendFakeXP(p, 0, ((float)cu)/counter.countUPgetMax());
				}
			}
		}
		//remove finished counters
		for(String player : remove) {
			counters.remove(player);
			countervars.remove(player);
			counterCurrent.remove(player);
		}
	}
}
