package me.andre111.dvz.event;

import me.andre111.dvz.Game;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DVZGameEndEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	
	private Game g;
	
    public DVZGameEndEvent(Game game) {
        super();
        g = game;
    }
    
    public Game getGame() {
    	return g;
    }

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {       
		return handlers;   
	}
}
