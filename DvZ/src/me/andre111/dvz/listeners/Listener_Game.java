package me.andre111.dvz.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.event.DVZGameEndEvent;
import me.andre111.dvz.event.DVZGameStartEvent;

public class Listener_Game implements Listener {
	private DvZ plugin;

	public Listener_Game(DvZ plugin){
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onGameStart(DVZGameStartEvent event) {
		if(event.isCancelled()) return;
		
		for (String command : plugin.getConfig().getStringList("commands_onStart")){
			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onGameEnd(DVZGameEndEvent event) {
		for (String command : plugin.getConfig().getStringList("commands_onEnd")){
			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
		}
	}
}
