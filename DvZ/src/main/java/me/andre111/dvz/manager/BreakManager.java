package me.andre111.dvz.manager;

import java.util.HashMap;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.disguise.DisguiseSystemHandler;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class BreakManager {

	private static HashMap<Location, Byte> locations = new HashMap<Location, Byte>();
	
	//all 10(this is currently used) - 20 seconds(in DvZ.java)
	//because minecraft removes them after 20 seconds
	public static void tick() {
		for(Location loc : locations.keySet()) {
			byte data = locations.get(loc);
			for(Player player : loc.getWorld().getPlayers()) {
				sendToPlayer(player, loc, data);
			}
		}
	}
	
	public static void set(Location loc, byte data) {
		locations.put(loc, data);
		
		for(Player player : loc.getWorld().getPlayers()) {
			sendToPlayer(player, loc, data);
		}
	}
	
	public static void reset(Location loc) {
		locations.remove(loc);
	}
	
	private static void sendToPlayer(Player player, Location loc, byte data) {
		PacketContainer fakeXPChange = DvZ.protocolManager.createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
		
		fakeXPChange.getIntegers().
			write(0, DisguiseSystemHandler.newEntityID()).
			write(1, loc.getBlockX()).
			write(2, loc.getBlockY()).
			write(3, loc.getBlockZ());
		fakeXPChange.getBytes().
			write(0, data);


		try {
			if(player.isOnline())
				DvZ.protocolManager.sendServerPacket(player, fakeXPChange);
		} catch (Exception e) {
		}
	}
}
