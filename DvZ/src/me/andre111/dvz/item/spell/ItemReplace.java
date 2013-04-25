package me.andre111.dvz.item.spell;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.andre111.dvz.Game;
import me.andre111.dvz.item.ItemSpell;

public class ItemReplace extends ItemSpell {
	private int range = 3;
	private int originalID = 1;
	private int originalDamage = 0;
	private int replaceID = 1;
	private int replaceDamage = 0;
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) range = (int) Math.round(var);
	}
	
	@Override
	public void setCastVar(int id, String var) {
		if(id==1) {
			String[] split = var.split(":");
			
			originalID = Integer.parseInt(split[0]);
			originalDamage = Integer.parseInt(split[1]);
		}
		else if(id==2) {
			String[] split = var.split(":");
			
			replaceID = Integer.parseInt(split[0]);
			replaceDamage = Integer.parseInt(split[1]);
		}
	}
	
	@Override
	public boolean cast(Game game, Player player) {
		return replaceNear(player.getLocation());
	}
	@Override
	public boolean cast(Game game, Player player, Block block) {
		return replaceNear(player.getLocation());
	}
	@Override
	public boolean cast(Game game, Player player, Player target) {
		return replaceNear(player.getLocation());
	}
	@Override
	//casted by another spell on that location
	public boolean cast(Game game, Player player, Location loc) {
		return replaceNear(loc);
	}
	
	private boolean replaceNear(Location loc) {
		boolean replaced = false;
		
		for(int xx=-range; xx<=range; xx++) {
			for(int yy=-range; yy<=range; yy++) {
				for(int zz=-range; zz<=range; zz++) {
					Block block = loc.getWorld().getBlockAt(loc.getBlockX()+xx, loc.getBlockY()+yy, loc.getBlockZ()+zz);
					
					if(block.getTypeId()==originalID && block.getData()==originalDamage) {
						block.setTypeIdAndData(replaceID, (byte) replaceDamage, false);
					}
				}
			}
		}
		
		return replaced;
	}
}
