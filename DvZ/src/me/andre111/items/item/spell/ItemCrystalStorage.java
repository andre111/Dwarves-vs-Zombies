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
	public boolean cast(Player player, Location loc, Player target, Block block) {
		if(player==null) return false;
		
		Game game = DvZ.instance.getPlayerGame(player.getName());
		if(game==null) return false;
		
		player.openInventory(game.getCrystalChest(player.getName(), global));
		return true;
	}
}
