package me.andre111.dvz.monster.attack;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.Spellcontroller;
import me.andre111.dvz.monster.MonsterAttack;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class MonsterLay extends MonsterAttack {
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
	public void spellCast(Game game, Player player) {	
		castAt(game, player, player.getLocation());
	}
	
	@Override
	public void spellCastOnLocation(Game game, Player player, Location target) {
		castAt(game, player, target);
	}
	
	private void castAt(Game game, Player player, Location loc) {
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
		} else {
			player.sendMessage(DvZ.getLanguage().getString("string_need_egg","You need an Egg to Infect!"));
		}
	}
	
	@Override
	public int getType() {
		return 0;
	}
}
