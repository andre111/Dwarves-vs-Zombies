package me.andre111.dvz.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DVZJoinGameEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	
	private Player player;
	
	public DVZJoinGameEvent(Player p) {
		super();
		player = p;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {       
		return handlers;   
	}

}
