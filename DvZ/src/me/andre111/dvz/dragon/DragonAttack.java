package me.andre111.dvz.dragon;

import java.util.List;

import me.andre111.dvz.DvZ;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

public class DragonAttack implements Listener {
	public float identifier;
	
	public DragonAttack() {
		identifier = (float)Math.random() * 20F;
		if(getTyp()==3)
			Bukkit.getServer().getPluginManager().registerEvents(this, DvZ.instance);
	}
	
	private void unregister() {
		HandlerList.unregisterAll(this);
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if(event.getEntity() instanceof SmallFireball) {
			if(event.getEntity().getFallDistance()==identifier) {
				shotHit(event.getEntity());
				unregister();
			}
		}
	}
	
	//1: cast at Dragon
	//2: cast at Player
	//3: shoot at Player and cast on Hit
	public int getTyp() {
		return 1;
	}
	
	//manakosten
	public int getMana() {
		return 100;
	}
	
	//speed of projectile
	public double getSpeed() {
		return 0.5;
	}
	
	public void cast(Location loc) {
	}
	public void cast(Entity entity) {
	}
	
	public void castOnPlayer(Player player) {
		cast(player.getLocation());
	}
	
	public void castOnNearPlayer(Location loc) {
		Snowball a = (Snowball) loc.getWorld().spawnEntity(loc, EntityType.SNOWBALL);
		List<Entity> elist = a.getNearbyEntities(1, 1, 1);
		for(Entity e : elist) {
			if(e instanceof Player) {
				castOnPlayer((Player) e);
				return;
			}
		}
		a.remove();
	}
	
	//TODO - shoot Fireball(set velocity)
	public void castShot(Location pos, Location target) {		
		SmallFireball ball = (SmallFireball) pos.getWorld().spawnEntity(pos, EntityType.SMALL_FIREBALL);
		ball.setFallDistance(identifier);
		
		double xm = 0;
		double ym = 0;
		double zm = 0;
		
		if (target.getX()-pos.getX()>0) xm = 1; else if (target.getX()-pos.getX()<0) xm = -1;
		if (target.getY()-pos.getY()>0) ym = 1; else if (target.getY()-pos.getY()<0) ym = -1;
		if (target.getZ()-pos.getZ()>0) zm = 1; else if (target.getZ()-pos.getZ()<0) zm = -1;

		Vector launch = new Vector(xm*getSpeed(), ym*getSpeed(), zm*getSpeed());
		
		ball.setDirection(launch);
		ball.setVelocity(launch);
	}
	
	public void shotHit(Entity projectile) {
	}
	
	//set loaded vars from config
	public void setCastVar(int id, double var) {
	}
	public void setCastVar(int id, String var) {
	}
}
