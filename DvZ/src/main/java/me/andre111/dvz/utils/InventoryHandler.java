package me.andre111.dvz.utils;


import me.andre111.dvz.volatileCode.DeprecatedMethods;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryHandler {
	public static void clearInv(Player player, boolean enderChest) {
		PlayerInventory inv = player.getInventory();
		inv.clear();
		inv.clear(inv.getSize() + 0);
		inv.clear(inv.getSize() + 1);
		inv.clear(inv.getSize() + 2);
		inv.clear(inv.getSize() + 3);
		if(enderChest)
			player.getEnderChest().clear();
	}
	
	public static boolean isInvEmpty(Player player, boolean enderChest) {
		for(ItemStack item : player.getInventory().getContents())
		{
		    if(item != null)
		    if(item.getAmount()>0)
		      return false;
		}
		for(ItemStack item : player.getInventory().getArmorContents())
		{
		    if(item != null)
		    if(item.getAmount()>0)
		      return false;
		}
		if(enderChest) {
			for(ItemStack item : player.getEnderChest().getContents())
			{
			    if(item != null)
			    if(item.getAmount()>0)
			      return false;
			}
		}
		
		return true;
	}
	
	public static boolean isArmorEmpty(Player player) {
		for(ItemStack item : player.getInventory().getArmorContents())
		{
		    if(item != null)
		    if(item.getAmount()>0)
		      return false;
		}
		
		return true;
	}
	
	//###################################
	//Inventory Helpers
	//###################################
	public static int removeItems(Player player, Material type, int data, int remaining) {
		int itemsExchanged = 0;
		for (ItemStack i : player.getInventory()){
			if (i != null && i.getType() == type && DeprecatedMethods.getDatavalue(i.getData()) == data){
				if (i.getAmount() > remaining){
					i.setAmount(i.getAmount() - remaining);
					itemsExchanged += remaining;
					remaining = 0;
				}else{
					itemsExchanged += i.getAmount();
					remaining -= i.getAmount();
					player.getInventory().remove(i);
				}
				if(remaining==0) break;
			}
		}
		return itemsExchanged;
	}

	public static int countItems(Player player, Material type, int data) {
		int items = 0;
		for (ItemStack i : player.getInventory()){
			if (i != null && i.getType() == type && DeprecatedMethods.getDatavalue(i.getData()) == data){
				items += i.getAmount();
			}
		}
		return items;
	}
	
	//TODO - remove temporary workaround
	@SuppressWarnings("deprecation")
	public static void updateInventory(Player player) {
		player.updateInventory();
	}
}
