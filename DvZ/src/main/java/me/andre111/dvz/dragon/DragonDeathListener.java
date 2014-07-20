package me.andre111.dvz.dragon;

import java.util.ArrayList;
import java.util.List;

import me.andre111.dvz.DvZ;

import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class DragonDeathListener implements Listener {
	private List<EnderDragon> dragons;
	
	public DragonDeathListener(DvZ plugin){
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		dragons = new ArrayList<EnderDragon>();
	}
	
	public void addDragon(EnderDragon dragon) {
		dragons.add(dragon);
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if(event.getEntityType() == EntityType.ENDER_DRAGON) {
			
			int entityID = event.getEntity().getEntityId();
			
			for(int i=0; i<dragons.size(); i++) {
				int did = dragons.get(i).getEntityId();
				if(did==entityID) {
					dragons.remove(i);
					event.getEntity().remove();
				}
			}
		}
	}
	
	@EventHandler
	public void stopDragonDamage(EntityExplodeEvent event)
	{
		Entity e = event.getEntity();
		if(e instanceof EnderDragon) {
			int entityID = event.getEntity().getEntityId();
			
			for(int i=0; i<dragons.size(); i++) {
				int did = dragons.get(i).getEntityId();
				if(did==entityID) {
					event.setCancelled(true);
				}
			}
		}
	}
}
