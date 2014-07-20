package me.andre111.dvz.utils;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class IconMenuHandler implements Listener {
	public static IconMenuHandler instance;
	
	private ArrayList<IconMenu> menus;
	private ArrayList<IconMenu> unregister;
	private boolean editing;
	
	public IconMenuHandler(JavaPlugin plugin) {
		menus = new ArrayList<IconMenu>();
		unregister = new ArrayList<IconMenu>();
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	
		Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			public void run() {
				if(editing) return;
				
				for(IconMenu menu : unregister) {
					menus.remove(menu);
				}
				unregister.clear();
			}
		}, 1, 1);
	}
	
	public void register(IconMenu menu) {
		if(!menus.contains(menu))
			menus.add(menu);
	}
	
	public void unregister(IconMenu menu) {
		unregister.add(menu);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onInventoryClose(final InventoryCloseEvent event) {
		editing = true;
		for(IconMenu m : menus) {
			m.onInventoryClose(event);
		}
		editing = false;
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onInventoryClick(InventoryClickEvent event) {
		editing = true;
		for(IconMenu m : menus) {
			m.onInventoryClick(event);
		}
		editing = false;
	}
}
