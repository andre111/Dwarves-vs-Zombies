package me.andre111.items.item.spell;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.items.item.ItemSpell;

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
	public boolean cast(Player player) {
		if(self)
			return checkClass(player);
		else
			return false;
	}
	@Override
	public boolean cast(Player player, Block block) {
		return cast(player);
	}
	@Override
	public boolean cast(Player player, Player target) {
		if(self) return cast(player);
		
		return checkClass(target);
	}
	@Override
	public boolean cast(Player player, Location loc) {
		return cast(player);
	}
	
	//Check for playerclass
	private boolean checkClass(Player player) {
		Game game = DvZ.instance.getPlayerGame(player.getName());
		if(game==null) return false;
		
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
