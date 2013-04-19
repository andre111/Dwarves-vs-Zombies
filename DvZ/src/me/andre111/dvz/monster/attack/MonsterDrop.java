package me.andre111.dvz.monster.attack;

import me.andre111.dvz.Game;
import me.andre111.dvz.monster.MonsterAttack;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MonsterDrop extends MonsterAttack {
	
	@Override
	public void spellCast(Game game, Player player, Player target) {
		ItemStack held = target.getItemInHand();
		target.setItemInHand(null);
		if(held.getTypeId()!=0) {
			target.getWorld().dropItemNaturally(target.getLocation(), held);
		}
	}
	
	@Override
	public int getType() {
		return 2;
	}
}
