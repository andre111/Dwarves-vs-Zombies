package me.andre111.items.item.spell;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.andre111.items.item.ItemSpell;
import me.andre111.items.item.SpellVariable;

public class ItemDVZTeleport extends ItemSpell {
	private Location location = null;
	private String playername = "";
	private int minDistance = 0;
	private int maxDistance = 0;
	
	private Random rand = new Random();
	
	@Override
	public void setCastVar(int id, String var) {
		if(id==1) playername = var;
	}
	@Override
	public void setCastVar(int id, double var) {
		if(id==2) minDistance = (int) Math.floor(var);
		else if(id==3) maxDistance = (int) Math.floor(var);
	}
	
	@Override
	public void setCastVar(int id, SpellVariable var) {
		if(id==0) location = var.getAsLocation();
		else if(id==1) playername = var.getAsString();
		else if(id==2) minDistance = var.getAsInt();
		else if(id==3) maxDistance = var.getAsInt();
	}
	
	@Override
	public boolean cast(Player player, Location loc, Player target, Block block) {
		Player pTarget = Bukkit.getPlayerExact(playername);
		if(playername.equals("")) {
			pTarget = player;
		}
		
		if(pTarget!=null) {
			return castIntern(pTarget);
		}
		
		return false;
	}
	
	private boolean castIntern(Player player) {
		if(location!=null) {
			Location loc = location.clone();
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
