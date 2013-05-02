package me.andre111.dvz.item.spell;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.andre111.dvz.Game;
import me.andre111.dvz.item.ItemSpell;

public class ItemHunger extends ItemSpell {
private int ammount = 2;
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) ammount = (int) Math.round(var);
	}

	@Override
	public boolean cast(Game game, Player player, Player target) {
		int newfood = target.getFoodLevel()-ammount;
		if(newfood<0) newfood = 0;
		
		target.setFoodLevel(newfood);
		
		//über 50 - alles entfernen
		if(ammount>50) {
			target.setFoodLevel(0);
			target.setSaturation(0);
		}
		
		return true;
	}
	
	@Override
	public boolean cast(Game game, Player player) {
		resetCoolDown(game, player);
		return false;
	}
	@Override
	public boolean cast(Game game, Player player, Block block) {
		resetCoolDown(game, player);
		return false;
	}
	@Override
	public boolean cast(Game game, Player player, Location loc) {
		resetCoolDown(game, player);
		return false;
	}
}
