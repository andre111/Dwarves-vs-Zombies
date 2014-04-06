package me.andre111.dvz.event;

import me.andre111.dvz.Game;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DVZGameStartEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	
	private boolean isCancelled = false;
	
	private Game g;
	
    public DVZGameStartEvent(Game game) {
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

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean flag) {
		isCancelled = flag;
	}
}
