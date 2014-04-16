package me.andre111.dvz.manager;

import java.util.HashMap;

import me.andre111.dvz.volatileCode.DvZPackets;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BreakManager {

	private static HashMap<Location, Byte> locations = new HashMap<Location, Byte>();
	
	//all 10(this is currently used) - 20 seconds(in DvZ.java)
	//because minecraft removes them after 20 seconds
	public static void tick() {
		for(Location loc : locations.keySet()) {
			byte data = locations.get(loc);
			for(Player player : loc.getWorld().getPlayers()) {
				DvZPackets.sendBlockBreakAnimToPlayer(player, loc, data);
			}
		}
	}
	
	public static void set(Location loc, byte data) {
		locations.put(loc, data);
		
		for(Player player : loc.getWorld().getPlayers()) {
			DvZPackets.sendBlockBreakAnimToPlayer(player, loc, data);
		}
	}
	
	public static void reset(Location loc) {
		locations.remove(loc);
	}
}
