package me.andre111.dvz.item.spell;

import me.andre111.dvz.Game;
import me.andre111.dvz.item.ItemSpell;
import me.andre111.dvz.utils.ItemHandler;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ItemArmorCheck extends ItemSpell {
	private boolean self = false;
	private boolean shouldHave = false;
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) self = (var==1);
		else if(id==1) shouldHave = (var==1);
	}
	
	@Override
	public boolean cast(Game game, Player player) {
		if(!self) return false;
		
		if(ItemHandler.isArmorEmpty(player)) {
			if(shouldHave) return false;
		} else {
			if(!shouldHave) return false;
		}
		
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
		
		if(ItemHandler.isArmorEmpty(p)) {
			if(shouldHave) return false;
		} else {
			if(!shouldHave) return false;
		}
		
		return true;
	}
	@Override
	//casted by another spell on that location
	public boolean cast(Game game, Player player, Location loc) {
		return cast(game, player);
	}
}
