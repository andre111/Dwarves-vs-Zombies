package me.andre111.dvz.item.spell;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.andre111.dvz.Game;
import me.andre111.dvz.item.ItemSpell;

public class ItemSetDamage extends ItemSpell {
	private int damage = 0;
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) damage = (int) Math.round(var);
	}
	
	@Override
	public boolean cast(Game game, Player player) {
		ItemStack it = player.getItemInHand();
		
		it.setDurability((short) damage);
		
		player.setItemInHand(it);
		
		return true;
	}
	
	@Override
	public boolean cast(Game game, Player player, Block target) {
		return cast(game, player);
	}
	@Override
	public boolean cast(Game game, Player player, Player target) {
		return cast(game, player);
	}
	
	@Override
	public boolean cast(Game game, Player player, Location loc) {
		return false;
	}
}
