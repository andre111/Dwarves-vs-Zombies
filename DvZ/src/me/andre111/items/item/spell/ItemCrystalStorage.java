package me.andre111.items.item.spell;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.items.item.ItemSpell;

public class ItemCrystalStorage extends ItemSpell {
	private boolean global = false;
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) global = (var==1);
	}
	
	@Override
	public boolean cast(Player player) {
		Game game = DvZ.instance.getPlayerGame(player.getName());
		if(game==null) return false;
		
		player.openInventory(game.getCrystalChest(player.getName(), global));
		return true;
	}
	
	@Override
	public boolean cast(Player player, Block block) {
		return cast(player);
	}
	@Override
	public boolean cast(Player player, Player target) {
		return cast(player);
	}
	@Override
	//casted by another spell on that location
	public boolean cast(Player player, Location loc) {
		return cast(player);
	}
}
