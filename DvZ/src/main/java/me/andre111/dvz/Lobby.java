package me.andre111.dvz;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerPortalEvent;

public class Lobby {
	private DvZ plugin;
	
	private Location[] portals = new Location[10]; 
	public HashMap<UUID, Integer> playerstate = new HashMap<UUID, Integer>();
	//-1: in Lobby
	//0-9: in Lobby Game world
	
	//TODO - LOBBY CLASS
	public Lobby(DvZ plugin) {
		this.plugin = plugin;
	}
	
	//TODO - Lobby teleportation
	public boolean portalEvent(PlayerPortalEvent event) {
		Location pos = event.getFrom();
		
		for (int i=0; i<portals.length; i++) {
			Location ppos = portals[i];
			if(ppos!=null) {
				if(ppos.distanceSquared(pos)<=4) {
					boolean found = false;
					
					UUID player = event.getPlayer().getUniqueId();
					if (playerstate.get(player)==-1) {
						playerstate.put(player, i);
						DvZ.sendPlayerMessageFormated(event.getPlayer(), "You joined the Lobby for Game ID "+i+"!");
						found = true;
					}
					else if (playerstate.get(player)==i) {
						playerstate.put(player, -1);
						DvZ.sendPlayerMessageFormated(event.getPlayer(), "You left the Lobby for Game ID "+i+"!");
						found = true;
					}
					
					if (found) {
						event.getPlayer().teleport(plugin.getServer().getWorld(plugin.getConfig().getString("world_prefix", "DvZ_")+"Lobby").getSpawnLocation());
					}
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void addPlayer(UUID player) {
		playerstate.put(player, -1);
	}
}
