package me.andre111.dvz.item.spell;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.andre111.dvz.Game;
import me.andre111.dvz.item.ItemSpell;

public class ItemClassCheck extends ItemSpell {
	boolean self = true;
	String type = "dwarf";
	int classid = 0;
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) self = var==1;
		if(id==2) classid = (int) Math.round(var);
	}
	
	@Override
	public void setCastVar(int id, String var) {
		if(id==1) type = var;
	}
	
	
	@Override
	public boolean cast(Game game, Player player) {
		if(self)
			return checkClass(game, player);
		else
			return false;
	}
	@Override
	public boolean cast(Game game, Player player, Block block) {
		return cast(game, player);
	}
	@Override
	public boolean cast(Game game, Player player, Player target) {
		if(self) return cast(game, player);
		
		return checkClass(game, target);
	}
	@Override
	public boolean cast(Game game, Player player, Location loc) {
		return cast(game, player);
	}
	
	//Check for playerclass
	private boolean checkClass(Game game, Player player) {
		//dwarves
		if(type.equals("dwarf") || type.equals("dwarves")) {
			if(game.isDwarf(player.getName(), true)) {
				int dId = game.getPlayerState(player.getName())-Game.dwarfMin;
				
				if(dId==classid) return true;
			}
		}
		//monsters
		if(type.equals("monster") || type.equals("monsters")) {
			if(game.isMonster(player.getName())) {
				int mId = game.getPlayerState(player.getName())-Game.monsterMin;
				
				if(mId==classid) return true;
			}
		}
		
		return false;
	}
}
