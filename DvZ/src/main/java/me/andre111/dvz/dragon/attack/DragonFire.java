package me.andre111.dvz.dragon.attack;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;

import me.andre111.dvz.dragon.DragonAttack;

public class DragonFire extends DragonAttack {
	private int duration;
	private int radius;

	@Override
	public void setCastVar(int id, double var) {
		if(id==0) duration = (int) Math.round(var);
		else if(id==1) radius = (int) Math.round(var);
	}
	
	@Override
	public void cast(Location loc) {
		Snowball a = (Snowball) loc.getWorld().spawnEntity(loc, EntityType.SNOWBALL);
		for(Entity e : a.getNearbyEntities(radius, radius, radius)) {
			if(e instanceof Player) {
				castOnPlayer((Player) e);
			}
		}
		a.remove();
	}
	
	@Override
	public void castOnPlayer(Player player) {
		player.setFireTicks(duration);
	}
}
