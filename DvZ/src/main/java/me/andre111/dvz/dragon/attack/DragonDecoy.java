package me.andre111.dvz.dragon.attack;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.dragon.DragonAttack;

import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class DragonDecoy extends DragonAttack {
	private int ammount = 5;
	private double hp = 1;

	@Override
	public void setCastVar(int id, double var) {
		if(id==0) ammount = (int) Math.round(var);
		else if(id==1) hp = var;
	}
	
	@Override
	public void cast(Location loc) {
		Location loc2 = loc;
		for(int i=0; i<ammount; i++) {
			loc2 = loc2.clone();
			loc2.setY(loc2.getY()+12);
			EnderDragon ed = (EnderDragon) loc.getWorld().spawnEntity(loc2, EntityType.ENDER_DRAGON);
			ed.setHealth(hp);
			ed.setMaxHealth(hp);
			ed.teleport(loc2);
			DvZ.dragonDeath.addDragon(ed);
		}
	}
	
	@Override
	public void cast(Entity entity) {
		cast(entity.getLocation());
	}
}
