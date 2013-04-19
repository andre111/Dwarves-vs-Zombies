package me.andre111.dvz.dragon.attack;

import me.andre111.dvz.dragon.DragonAttack;

import org.bukkit.Location;

public class DragonExplode extends DragonAttack {
	private float power = 7F;
	private boolean fire = true;

	@Override
	public void setCastVar(int id, double var) {
		if(id==0) power = (float) var;
		else if(id==1) fire = (var==1);
	}
	
	@Override
	public void cast(Location loc) {
		loc.setY(loc.getY()+1);
		loc.getWorld().createExplosion(loc, power, fire);
	}
}
