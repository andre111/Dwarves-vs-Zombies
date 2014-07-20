package me.andre111.dvz.dragon;

import java.util.ArrayList;
import java.util.List;

import me.andre111.dvz.DvZ;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class DragonAttackListener implements Listener {
	private List<Fireball> entites;
	
	public DragonAttackListener(DvZ plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		entites = new ArrayList<Fireball>();
	}
	
	public void addFireball(Fireball e) {
		entites.add(e);
	}
	
	@EventHandler
	public void stopDragonDamage(EntityExplodeEvent event)
	{
		Entity e = event.getEntity();
		if(e instanceof Fireball) {
			int entityID = event.getEntity().getEntityId();
			
			for(int i=0; i<entites.size(); i++) {
				int did = entites.get(i).getEntityId();
				if(did==entityID) {
					event.setCancelled(true);
					e.remove();
				}
			}
		}
	}
	
	@EventHandler
	public void onExplosionPrime(ExplosionPrimeEvent event) {
		event.setFire(false); //Only really needed for fireballs
		
		Entity e = event.getEntity();
		if (e instanceof Fireball) {
			int entityID = event.getEntity().getEntityId();
			
			for(int i=0; i<entites.size(); i++) {
				int did = entites.get(i).getEntityId();
				if(did==entityID) {
					event.setCancelled(true);
					e.remove();
				}
			}
		}	
	}
}
