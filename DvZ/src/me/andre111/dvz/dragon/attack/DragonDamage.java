package me.andre111.dvz.dragon.attack;

import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import me.andre111.dvz.dragon.DragonAttack;

public class DragonDamage extends DragonAttack {
	private int ammount = 5;
	private double range = 1;

	@Override
	public void setCastVar(int id, double var) {
		if(id==0) ammount = (int) Math.round(var);
		else if(id==1) range = var;
	}
	
	@Override
	public void cast(Location loc) {
		Arrow a = (Arrow) loc.getWorld().spawnEntity(loc, EntityType.ARROW);
		cast(a);
		a.remove();
	}
	
	@Override
	public void cast(Entity entity) {
		List<Entity> allNearby = entity.getNearbyEntities(range, range, range);
		allNearby.add(entity);
		
		for (Entity e : allNearby) {
			if(e instanceof LivingEntity) {
				LivingEntity le = (LivingEntity) e;
				
				if(ammount>0) {
					le.damage((double) ammount);
					//poison splash effect
					le.getWorld().playEffect(le.getLocation(), Effect.POTION_BREAK, 16396);
				} else {
					double newh = le.getHealth() + Math.abs(ammount);
					if(newh>le.getMaxHealth()) newh = le.getMaxHealth();
					le.setHealth(newh);
					le.getM
					le.setFireTicks(0);
					//regeneration splash effect
					le.getWorld().playEffect(le.getLocation(), Effect.POTION_BREAK, 16385);
				}
			}
		}
	}
}
