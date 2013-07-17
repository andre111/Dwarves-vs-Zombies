package me.andre111.dvz.item.spell;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.andre111.dvz.Game;
import me.andre111.dvz.item.ItemSpell;


public class ItemExperience extends ItemSpell {
	private boolean self = true;
	private int amount = 3;

	@Override
	public void setCastVar(int id, double var) {
		if(id==0) self = var==1;
		if(id==1) amount = (int) Math.round(var);
	}
	
	
	@Override
	public boolean cast(Game game, Player player) {
		if(self) {
			player.giveExp(amount);
			return true;
		}
		return false;
	}
	@Override
	public boolean cast(Game game, Player player, Block block) {
		return cast(game, player);
	}
	@Override
	public boolean cast(Game game, Player player, Player target) {
		if(!self) {
			target.giveExp(amount);
			return true;
		}
		
		return cast(game, player);
	}
	@Override
	public boolean cast(Game game, Player player, Location loc) {
		return cast(game, player);
	}
}
