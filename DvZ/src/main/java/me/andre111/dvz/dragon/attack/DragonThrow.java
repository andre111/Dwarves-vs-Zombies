package me.andre111.dvz.dragon.attack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.andre111.dvz.dragon.DragonAttack;
import me.andre111.dvz.utils.Animation;
import me.andre111.dvz.volatileCode.DeprecatedMethods;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class DragonThrow extends DragonAttack {
	private double range = 3;
	private int effects = 40;
	
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
		else if(id==1) throwvelocity=var;
	}
	
	@Override
	public void castOnPlayer(Player player) {
		if (player == null) {
			return;
		}
		// animate
		if (throwvelocity > 0) {
			player.setVelocity(new Vector(0, throwvelocity, 0));
		}

		if (throwHeight > 0) {
			List<Entity> allNearby = player.getNearbyEntities(50, 50, 50);
			allNearby.add(player);
			List<Player> playersNearby = new ArrayList<Player>();
			for (Entity e : allNearby) {
				if (e instanceof Player) {
					playersNearby.add((Player)e);
				}
			}
			new ThrowAnimation(player.getLocation(), playersNearby);
		}
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
		loc.getWorld().createExplosion(loc, 0.0F);
		for(int i=0; i<effects; i++) {
			Location loc2 = loc.clone();
			loc2 = loc2.add((rand.nextDouble()-0.5)*2*range, 1, (rand.nextDouble()-0.5)*2*range);
			loc2.getWorld().playEffect(loc2, Effect.SMOKE, rand.nextInt(9), 32);
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
	
	private double throwvelocity = 2.75D;
	private static int throwHeight = 4;
	private static Material throwType = Material.STATIONARY_LAVA;
	
	private static class ThrowAnimation extends Animation {
		private Location start;
		private List<Player> nearby;

		public ThrowAnimation(Location start, List<Player> nearby) {
			super(0, 2, true);
			this.start = start;
			this.nearby = nearby;
		}

		@Override
		protected void onTick(int tick) {
			if (tick > throwHeight*2) {
				stop();
			} else if (tick < throwHeight) {
				Block block = start.clone().add(0,tick,0).getBlock();
				if (block.getType() == Material.AIR) {
					for (Player p : nearby) {
						DeprecatedMethods.sendBlockChange(p, block.getLocation(), throwType, (byte)0);
					}
				}
			} else {
				int n = throwHeight-(tick-throwHeight)-1;
				Block block = start.clone().add(0, n, 0).getBlock();
				for (Player p : nearby) {
					DeprecatedMethods.sendBlockChange(p, block.getLocation(), block.getType(), DeprecatedMethods.getBlockData(block));
				}
			}
		}
	}
}
