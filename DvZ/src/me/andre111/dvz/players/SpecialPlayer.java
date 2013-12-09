package me.andre111.dvz.players;

import me.andre111.dvz.Game;
import me.andre111.items.ItemHandler;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SpecialPlayer {
	private String name;
	private String[] crystalItems;
	
	private String prefix;
	private String suffix;
	
	public void addCrytalItems(Game game, Player player) {
		Inventory cinv = game.getCrystalChest(player.getName(), false);

		for(int i=0; i<crystalItems.length; i++) {
			ItemStack cit = ItemHandler.decodeItem(crystalItems[i]);
			if(cit!=null) {
				cinv.addItem(cit);
			}
		}
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String[] getCrystalItems() {
		return crystalItems;
	}
	public void setCrystalItems(String[] crystalItems) {
		this.crystalItems = crystalItems;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
}
