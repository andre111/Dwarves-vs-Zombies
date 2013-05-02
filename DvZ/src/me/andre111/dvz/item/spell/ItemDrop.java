package me.andre111.dvz.item.spell;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.andre111.dvz.Game;
import me.andre111.dvz.item.ItemSpell;

public class ItemDrop extends ItemSpell {
	
	@Override
	public boolean cast(Game game, Player player, Player target) {
		ItemStack held = target.getItemInHand();
		target.setItemInHand(null);
		if(held.getTypeId()!=0) {
			target.getWorld().dropItemNaturally(target.getLocation(), held);
			
			return true;
		}
		
		return false;
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
