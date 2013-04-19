package me.andre111.dvz.item.spell;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.andre111.dvz.Game;
import me.andre111.dvz.Spellcontroller;
import me.andre111.dvz.item.ItemSpell;

public class ItemLeap extends ItemSpell {
	private double forward = 40 / 10D;
	private double upward = 15 / 10D;
	private float power = 1;
	private boolean disableDamage = true;
	
	private double range;
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) forward = var;
		else if(id==1) upward = var;
		else if(id==2) power = (float) var;
		else if(id==3) disableDamage = var==1;
		else if(id==4) range = var;
	}
	
	@Override
	public boolean cast(Game game, Player player) {
		Spellcontroller.spellLeap(player, forward, upward, power, disableDamage);
		return true;
	}
	
	@Override
	public boolean cast(Game game, Player player, Block target) {
		return cast(game, player);
	}
	@Override
	public boolean cast(Game game, Player player, Player target) {
		return cast(game, player);
	}
	
	@Override
	public boolean cast(Location loc) {
		ArrayList<Player> players = new ArrayList<Player>();
		for(Entity e : loc.getWorld().getEntities()) {
			if(e instanceof Player) {
				if(e.getLocation().distanceSquared(loc)<=range*range) {
					players.add((Player) e);
				}
			}
		}
		
		for(Player p : players) {
			Spellcontroller.spellLeap(p, forward, upward, power, disableDamage);
		}
		
		if(players.size()>0) {
			return true;
		}
		return false;
	}
}
