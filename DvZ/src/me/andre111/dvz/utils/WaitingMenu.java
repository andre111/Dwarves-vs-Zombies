package me.andre111.dvz.utils;

import java.util.HashMap;

import me.andre111.dvz.DvZ;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class WaitingMenu implements Listener {
	public boolean closed;
	private String name;
	private DvZ plugin;
	
	private static int DELAY = 1;
	
	public WaitingMenu(DvZ plugin, String add) {
		this.plugin = plugin;
		this.closed = false;
		this.name = DvZ.getLanguage().getString("string_wait_release", "Waiting for release..."+add);
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	private HashMap<String, Boolean> selfOverride = new HashMap<String, Boolean>();
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getInventory().getTitle().equals(name)) {
			final Player p = (Player) event.getPlayer();
			
			//safety for selfoveriding and infinite loops
			if(selfOverride.containsKey(p.getName())) {
				if(selfOverride.get(p.getName())) {
					selfOverride.put(p.getName(), false);
					return;
				}
			}
			
			if(!closed) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						selfOverride.put(p.getName(), true);
						open(p);
					}
				}, DELAY);
			}
		}
	}
	
	public void close() {
		closed = false;
	}
	
	public void open(Player player) {
		Inventory inventory = Bukkit.createInventory(player, 0, name);

		player.openInventory(inventory);
	}
	
	public void release(final Player player) {
		closed = true;
		if(player.getOpenInventory().getTitle().equals(name)) {
			//Delay
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                public void run() {
                	player.closeInventory();
                	//player.openInventory(Bukkit.createInventory(player, 0, "Start..."));
                	//player.openInventory(player.getEnderChest());
                }
            }, 1);
			//player.sendMessage("WARNING: If your Inventory keeps reverting, please relog - thats a Bug in Bukkit with closeInventory()");
		}
	}
	
	//WARNING: Only for server reload, else use release(Player player)
	public void releaseAll() {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			closed = true;
			if(p.getOpenInventory().getTitle().equals(name)) {
				p.closeInventory();
			}
		}
		
		selfOverride.clear();
	}
}
