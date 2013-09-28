package me.andre111.items.item.spell;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.items.item.ItemSpell;

public class ItemDVZTeleport extends ItemSpell {
	private String location = "";
	private boolean self = true;
	
	@Override
	public void setCastVar(int id, String var) {
		if(id==0) location = var;
	}
	@Override
	public void setCastVar(int id, double var) {
		if(id==1) self = (var==1);
	}
	
	@Override
	public boolean cast(Player player) {
		if(!self) return false;
		
		return castIntern(player);
	}
	
	@Override
	public boolean cast(Player player, Block block) {
		if(!self) return false;
		
		return castIntern(player);
	}
	@Override
	public boolean cast(Player player, Player target) {
		if(self)
			return castIntern(player);
		else
			return castIntern(target);
	}
	@Override
	//casted by another spell on that location
	public boolean cast(Player player, Location loc) {
		if(!self) return false;
		
		return castIntern(player);
	}
	
	private boolean castIntern(Player player) {
		Game game = DvZ.instance.getPlayerGame(player.getName());
		if(game==null) return false;
		
		if(game.monumentexists && location.equalsIgnoreCase("monument")) {
			Location loc = game.monument.clone();
			while(loc.getBlock().getType()!=Material.AIR && loc.getY()<loc.getWorld().getMaxHeight()) {
				loc.setY(loc.getY()+1);
			}
			player.teleport(loc);
			return true;
		}
		
		return false;
	}
}
