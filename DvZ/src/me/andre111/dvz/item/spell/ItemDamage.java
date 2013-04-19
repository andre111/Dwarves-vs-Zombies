package me.andre111.dvz.item.spell;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.andre111.dvz.Game;
import me.andre111.dvz.item.ItemSpell;

public class ItemDamage extends ItemSpell {
	private boolean self = true;
	private int damage = 4;
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) self = var==1;
		else if(id==1) damage = (int) Math.round(var);
	}
	
	@Override
	public boolean cast(Game game, Player player) {
		if(!self) return false;
		
		return castIntern(game, player, player);
	}
	@Override
	public boolean cast(Game game, Player player, Block target) {
		if(self)
			return castIntern(game, player, player);
		else
			return false;
	}
	@Override
	public boolean cast(Game game, Player player, Player target) {
		if(self)
			return castIntern(game, player, player);
		else
			return castIntern(game, target, player);
	}
	
	private boolean castIntern(Game game, Player player, Player source) {
		if(damage>0) {
			player.damage(damage, source);
		} else {
			int newHealth = player.getHealth() - damage;
			if(newHealth>player.getMaxHealth()) newHealth = player.getMaxHealth();
			
			player.setHealth(newHealth);
		}
		
		return true;
	}
}