package me.andre111.items.item.spell;

import java.util.Random;

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
	private int minDistance = 0;
	private int maxDistance = 0;
	
	private Random rand = new Random();
	
	@Override
	public void setCastVar(int id, String var) {
		if(id==0) location = var;
	}
	@Override
	public void setCastVar(int id, double var) {
		if(id==1) self = (var==1);
		if(id==2) minDistance = (int) Math.floor(var);
		if(id==3) maxDistance = (int) Math.floor(var);
	}
	
	@Override
	public boolean cast(Player player, Location loc, Player target, Block block) {
		Player pTarget = null;
		if(self) {
			pTarget = player;
		} else if(target!=null) {
			pTarget = target;
		}
		
		if(pTarget!=null) {
			return castIntern(pTarget);
		}
		
		return false;
	}
	
	private boolean castIntern(Player player) {
		Game game = DvZ.instance.getPlayerGame(player.getName());
		if(game==null) return false;
		
		if(game.monumentexists && location.equalsIgnoreCase("monument")) {
			Location loc = game.monument.clone();
			loc.add(minDistance+rand.nextInt(maxDistance-minDistance), minDistance+rand.nextInt(maxDistance-minDistance), minDistance+rand.nextInt(maxDistance-minDistance));
			loc = loc.getWorld().getHighestBlockAt(loc).getLocation();
			
			while(loc.getBlock().getType()!=Material.AIR && loc.getY()<256) {
				loc.setY(loc.getY()+1);
			}
			loc.setY(loc.getY()+1);
			player.teleport(loc);
			return true;
		}
		
		return false;
	}
}
