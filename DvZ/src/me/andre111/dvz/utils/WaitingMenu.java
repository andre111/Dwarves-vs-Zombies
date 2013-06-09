package me.andre111.dvz.utils;

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
	
	public WaitingMenu(DvZ plugin, String add) {
		this.plugin = plugin;
		this.closed = false;
		this.name = DvZ.getLanguage().getString("string_wait_release", "Waiting for release..."+add);
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getInventory().getTitle().equals(name)) {
			if(!closed) {
				final Player p = (Player) event.getPlayer();
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						open(p);
					}
				}, 1);
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
	}
}
