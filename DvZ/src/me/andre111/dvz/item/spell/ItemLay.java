package me.andre111.dvz.item.spell;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.Spellcontroller;
import me.andre111.dvz.item.ItemSpell;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ItemLay extends ItemSpell {
	private int radius;
	private String message = "";
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) radius = (int) Math.round(var);
	}
	
	@Override
	public void setCastVar(int id, String var) {
		if(id==1) message = var;
	}
	
	@Override
	public boolean cast(Game game, Player player) {	
		return castAt(game, player, player.getLocation());
	}
	@Override
	public boolean cast(Game game, Player player, Block block) {	
		return castAt(game, player, player.getLocation());
	}
	@Override
	public boolean cast(Game game, Player player, Player target) {	
		return castAt(game, player, player.getLocation());
	}
	
	@Override
	public boolean cast(Game game, Player player, Location target) {
		return castAt(game, player, target);
	}
	
	private boolean castAt(Game game, Player player, Location loc) {
		if(Spellcontroller.countItems(player, 383, 0)>=1) {
			Spellcontroller.removeItems(player, 383, 0, 1);
			
			World w = loc.getWorld();
			int x = loc.getBlockX();
			int y = loc.getBlockY();
			int z = loc.getBlockZ();
			for(int xx=-radius; xx<=radius; xx++) {
				for(int yy=-radius; yy<=radius; yy++) {
					for(int zz=-radius; zz<=radius; zz++) {
						Block block = w.getBlockAt(x+xx, y+yy, z+zz);
						int bid = block.getTypeId();
						if(bid==1 || bid==4 || bid==98) {
							block.setTypeId(97);
						}
					}
				}
			}
			
			if(!message.equals(""))
				game.broadcastMessage(message);
			
			return true;
		} else {
			player.sendMessage(DvZ.getLanguage().getString("string_need_egg","You need an Egg to Infect!"));
			
			return false;
		}
	}
}
