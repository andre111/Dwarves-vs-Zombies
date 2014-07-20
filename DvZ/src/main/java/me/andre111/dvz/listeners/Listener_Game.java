package me.andre111.dvz.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.event.DVZGameEndEvent;
import me.andre111.dvz.event.DVZGameStartEvent;
import me.andre111.dvz.event.DVZJoinGameEvent;

public class Listener_Game implements Listener {
	public Listener_Game(DvZ plugin){
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onGameStart(DVZGameStartEvent event) {
		if(event.isCancelled()) return;
		
		for (String command : ConfigManager.getStaticConfig().getStringList("commands_onStart")){
			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onGameEnd(DVZGameEndEvent event) {
		for (String command : ConfigManager.getStaticConfig().getStringList("commands_onEnd")){
			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoinGame(DVZJoinGameEvent event) {
		for (String command : ConfigManager.getStaticConfig().getStringList("commands_onJoin")){
			command = command.replace("-0-", event.getPlayer().getName());
			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
		}
	}
}
