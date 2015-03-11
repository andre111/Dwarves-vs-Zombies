package me.andre111.dvz.dragon.attack;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.andre111.dvz.dragon.DragonAttack;
import me.andre111.dvz.volatileCode.DeprecatedMethods;
import me.andre111.dvz.volatileCode.DynamicClassFunctions;

public class DragonSpew extends DragonAttack {
	private int blockID = 51;
	private double power = 4;
	private boolean damage = false;
	private int hurt = 4;
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) blockID = (int) Math.round(var);
		else if(id==1) power = var;
		else if(id==2) damage = (var==1);
		else if(id==3) hurt = (int) Math.round(var);
	}

	
	@Override
	public void cast(Entity entity) {
		//entity.getWorld().playSound(entity.getLocation(), Sound.GHAST_FIREBALL, 1F, 1F);
		entity.getWorld().playSound(entity.getLocation(), Sound.NOTE_STICKS, 1F, 0.4F);
		
		//TODO - find a better way to launch block without needing Player
		if(entity instanceof Player) {
			Player player = (Player) entity;

			Vector velocity = player.getEyeLocation().getDirection();
			velocity.normalize().multiply(power);
			
			FallingBlock fb = DeprecatedMethods.spawnFallingBlock(entity.getLocation().clone().add(0, 1, 0), DeprecatedMethods.getMaterialByID(blockID), (byte)0);
			fb.setDropItem(false);
			fb.setVelocity(velocity);
			
			//make it do damage
			if(damage) {
				DynamicClassFunctions.setFallingBlockHurtEntities(fb, hurt, hurt);
			}
		}
	}
}
