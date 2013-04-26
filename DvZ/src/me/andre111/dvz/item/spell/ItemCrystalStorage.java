package me.andre111.dvz.item.spell;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.andre111.dvz.Game;
import me.andre111.dvz.item.ItemSpell;

public class ItemCrystalStorage extends ItemSpell {
	private boolean global = false;
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) global = (var==1);
	}
	
	@Override
	public boolean cast(Game game, Player player) {
		player.openInventory(game.getCrystalChest(player.getName(), global));
		return true;
	}
	
	@Override
	public boolean cast(Game game, Player player, Block block) {
		return cast(game, player);
	}
	@Override
	public boolean cast(Game game, Player player, Player target) {
		return cast(game, player);
	}
	@Override
	//casted by another spell on that location
	public boolean cast(Game game, Player player, Location loc) {
		return cast(game, player);
	}
}
