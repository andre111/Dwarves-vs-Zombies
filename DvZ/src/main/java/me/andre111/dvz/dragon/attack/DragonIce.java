package me.andre111.dvz.dragon.attack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.andre111.dvz.dragon.DragonAttack;
import me.andre111.dvz.volatileCode.DeprecatedMethods;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DragonIce extends DragonAttack {
	private double range = 3;
	private int effects = 10;
	private int time = 20;
	
	@Override
	public int getTyp() {
		return 3;
	}
	
	@Override
	public int getMana() {
		return 100;
	}
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) range = var;
		else if(id==1) time = (int) Math.round(var);
	}
	
	@Override
	public void castOnPlayer(Player player) {
		if (player == null) {
			return;
		}
		// animate
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*time, 4), false);
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20*time, 4), false);
		player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, DeprecatedMethods.getMaterialID(Material.ICE));
	}
	
	@Override
	public void cast(Location loc) {
		Arrow a = (Arrow) loc.getWorld().spawnEntity(loc, EntityType.ARROW);
		shotHit(a);
		a.remove();
	}
	
	@Override
	public void shotHit(Entity proj) {
		//cast effects
		Location loc = proj.getLocation();
		Random rand = new Random();
		for(int i=0; i<effects; i++) {
			Location loc2 = loc.clone();
			loc2 = loc2.add((rand.nextDouble()-0.5)*2*range, 1, (rand.nextDouble()-0.5)*2*range);
			loc2.getWorld().playEffect(loc2, Effect.STEP_SOUND, DeprecatedMethods.getMaterialID(Material.ICE));
		}
		
		//get Players
		List<Entity> allNearby = proj.getNearbyEntities(range, range, range);
		allNearby.add(proj);
		List<Player> playersNearby = new ArrayList<Player>();
		for (Entity e : allNearby) {
			if (e instanceof Player) {
				playersNearby.add((Player)e);
			}
		}
		for (Player p : playersNearby) {
			castOnPlayer(p);
		}
	}
}
