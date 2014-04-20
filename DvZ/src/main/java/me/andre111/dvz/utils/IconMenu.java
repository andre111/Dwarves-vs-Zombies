package me.andre111.dvz.utils;

import me.andre111.dvz.DvZ;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
 
public class IconMenu {
	private static long DELAY = 5L;
	
	private String name;
    private int size;
    private OptionClickEventHandler handler;
    private Plugin plugin;
    private Player player;
 
    private String[] optionNames;
    private ItemStack[] optionIcons;
	
	private boolean closed;
 
    public IconMenu(String name, int size, OptionClickEventHandler handler, Plugin plugin) {
        this.name = name;
		if(this.name.length()>31) this.name = this.name.substring(0, 31);
        this.size = size;
        this.handler = handler;
        this.plugin = plugin;
        this.optionNames = new String[size];
        this.optionIcons = new ItemStack[size];
        this.closed = false;
		
		IconMenuHandler.instance.register(this);
    }
 
    public IconMenu setOption(int position, ItemStack icon, String name, String... info) {
        optionNames[position] = name;
        optionIcons[position] = setItemNameAndLore(icon, name, info);
        return this;
    }
	
	public IconMenu setOption(int position, ItemStack icon) {
		String name = icon.getItemMeta().getDisplayName();
		optionNames[position] = name;
		optionIcons[position] = icon;
		return this;
	}
 
    public void setSpecificTo(Player player) {
        this.player = player;
    }
 
    public boolean isSpecific() {
        return player != null;
    }
 
    public void open(Player player) {
		if(player==null) return;
		if(optionIcons==null) return;
	
        Inventory inventory = Bukkit.createInventory(player, size, name);
        for (int i = 0; i < optionIcons.length; i++) {
            if (optionIcons[i] != null) {
                inventory.setItem(i, optionIcons[i]);
            }
        }
        player.openInventory(inventory);
    }
 
    public void destroy() {
        IconMenuHandler.instance.unregister(this);
        handler = null;
        plugin = null;
        optionNames = null;
        optionIcons = null;
    }
 
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getInventory().getTitle().equals(name)  && (player == null || event.getPlayer() == player)) {
			if(!closed) {
				final Player p = (Player) event.getPlayer();
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						open(p);
					}
				}, DELAY);
			} else {
				destroy();
			}
		}
	}
 
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(name) && (player == null || event.getWhoClicked() == player)) {
            event.setCancelled(true);
            if (event.getClick() != ClickType.LEFT)
                return;
            int slot = event.getRawSlot();
            if (slot >= 0 && slot < size && optionNames[slot] != null) {
                Plugin plugin = this.plugin;
                OptionClickEvent e = new OptionClickEvent((Player) event.getWhoClicked(), slot, optionNames[slot], optionIcons[slot]);
                handler.onOptionClick(e);
                DvZ.updateInventory((Player) event.getWhoClicked());

                if (e.willClose()) {
                    final Player p = (Player) event.getWhoClicked();
					closed = true;
					
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        public void run() {
                            p.closeInventory();
                        }
                    }, DELAY);
                }
                if (e.willDestroy()) {
                    destroy();
                }
            }
        }
    }
 
    public interface OptionClickEventHandler {
        public void onOptionClick(OptionClickEvent event);
    }
 
    public static class OptionClickEvent {
        private Player player;
        private int position;
        private String name;
        private boolean close;
        private boolean destroy;
        private ItemStack item;
 
        public OptionClickEvent(Player player, int position, String name, ItemStack item) {
            this.player = player;
            this.position = position;
            this.name = name;
            this.close = true;
            this.destroy = false;
            this.item = item;
        }
 
        public Player getPlayer() {
            return player;
        }
 
        public int getPosition() {
            return position;
        }
 
        public String getName() {
            return name;
        }
 
        public boolean willClose() {
            return close;
        }
 
        public boolean willDestroy() {
            return destroy;
        }
 
        public void setWillClose(boolean close) {
            this.close = close;
        }
 
        public void setWillDestroy(boolean destroy) {
            this.destroy = destroy;
        }
 
        public ItemStack getItem() {
            return item;
        }
    }
 
    private ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore) {
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        im.setLore(Arrays.asList(lore));
        item.setItemMeta(im);
        return item;
    }
 
}