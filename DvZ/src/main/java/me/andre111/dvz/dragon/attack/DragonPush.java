package me.andre111.dvz.dragon.attack;

import java.util.List;

import me.andre111.dvz.dragon.DragonAttack;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public class DragonPush extends DragonAttack {
	private double range = 6;
	private double power = 4;
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) range = var;
		else if(id==1) power = var;
	}

	@Override
	public void cast(Location loc) {
		Arrow a = (Arrow) loc.getWorld().spawnEntity(loc, EntityType.ARROW);
		cast(a);
		a.remove();
	}
	
	@Override
	public void cast(Entity entity) {
		//entity.getWorld().playSound(entity.getLocation(), Sound.ENDERDRAGON_WINGS, 1F, 1F);
		//TODO . fix sound
		//entity.getWorld().playSound(entity.getLocation(), Sound.BREATH, 2F, 0.4F);
		
		List<Entity> allNearby = entity.getNearbyEntities(range, range, range);
		allNearby.remove(entity);
		
		for (Entity e : allNearby) {
			pushAwayEntity(e, entity.getLocation(), power);
		}
	}
	
	public void pushAwayEntity(Entity entity, Location loc, double speed) {
		// Get velocity unit vector:
		Vector unitVector = entity.getLocation().toVector().subtract(loc.toVector()).normalize();
		// Set speed and push entity:
		entity.setVelocity(unitVector.multiply(speed));
	}
}
