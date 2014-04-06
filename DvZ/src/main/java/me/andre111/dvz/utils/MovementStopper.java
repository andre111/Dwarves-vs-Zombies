package me.andre111.dvz.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.andre111.dvz.DvZ;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MovementStopper implements Listener {
	//TODO - use something else then the movement listener
	private List<UUID> entites;
	private DvZ plugin;
	
	public MovementStopper(DvZ plugin){
		this.plugin = plugin;

		entites = new ArrayList<UUID>();
	}
	
	public void addEntity(Player entity) {
		//only letting the listener run when it is needed
		if(entites.size()==0)
			plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
		entites.add(entity.getUniqueId());
	}
	
	public void removeEntity(Player entity) {
		entites.remove(entity.getUniqueId());
		
		//only letting the listener run when it is needed
		if(entites.size()==0)
			HandlerList.unregisterAll(this);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		UUID entityID = event.getPlayer().getUniqueId();
			
		for(int i=0; i<entites.size(); i++) {
			UUID did = entites.get(i);
			if(did.equals(entityID)) {
				Location to = event.getTo().clone();
				to.setX(event.getFrom().getX());
				to.setY(event.getFrom().getY());
				to.setZ(event.getFrom().getZ());
				
				event.getPlayer().teleport(to);
			}
		}
	}
}
