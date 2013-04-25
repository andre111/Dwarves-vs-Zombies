package me.andre111.dvz.item.spell;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.andre111.dvz.Game;
import me.andre111.dvz.item.ItemSpell;
import me.andre111.dvz.utils.ItemHandler;

public class ItemArmorSet extends ItemSpell {
	private boolean self = false;
	
	private String helmet = "";
	private String chest = "";
	private String leggins = "";
	private String boots = "";
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) self = (var==1);
	}
	
	@Override
	public void setCastVar(int id, String var) {
		if(id==1) helmet = var;
		else if(id==2) chest = var;
		else if(id==3) leggins = var;
		else if(id==4) boots = var;
	}
	
	@Override
	public boolean cast(Game game, Player player) {
		if(!self) return false;
		
		setArmor(player);
		
		return true;
	}
	@Override
	public boolean cast(Game game, Player player, Block block) {
		return cast(game, player);
	}
	@Override
	public boolean cast(Game game, Player player, Player target) {
		Player p = player;
		if(!self) p = target;
		
		setArmor(p);
		
		return true;
	}
	@Override
	//casted by another spell on that location
	public boolean cast(Game game, Player player, Location loc) {
		return cast(game, player);
	}
	
	private void setArmor(Player player) {
		ItemStack helmetIt = ItemHandler.decodeItem(helmet);
		if(helmetIt!=null) player.getInventory().setHelmet(helmetIt);
		
		ItemStack chestIt = ItemHandler.decodeItem(chest);
		if(chestIt!=null) player.getInventory().setChestplate(chestIt);
		
		ItemStack legginsIt = ItemHandler.decodeItem(leggins);
		if(legginsIt!=null) player.getInventory().setLeggings(legginsIt);
		
		ItemStack bootsIt = ItemHandler.decodeItem(boots);
		if(bootsIt!=null) player.getInventory().setBoots(bootsIt);
	}
}
