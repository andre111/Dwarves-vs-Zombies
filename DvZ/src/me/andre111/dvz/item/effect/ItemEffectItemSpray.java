package me.andre111.dvz.item.effect;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.item.ItemEffect;
import me.andre111.dvz.utils.ItemHandler;

public class ItemEffectItemSpray extends ItemEffect {
	private int number = 1;
	private int duration = 6;
	private float force = 1.0F; 
	private String item;
	
	@Override
	public void setVars(String vars) {
		String[] split = vars.split(";");
		
		if(split.length>0) number = Integer.parseInt(split[0]);
		if(split.length>1) duration = Integer.parseInt(split[1]);
		if(split.length>2) force = Float.parseFloat(split[2]);
		if(split.length>3) item = split[3];
	}

	@Override
	public void play(Location location) {
		// spawn items
		Random rand = new Random();
		Location loc = location.clone().add(0, 1, 0);
		final Item[] items = new Item[number];
		for (int i = 0; i < number; i++) {
			items[i] = loc.getWorld().dropItem(loc, ItemHandler.decodeItem(item));
			items[i].setVelocity(new Vector((rand.nextDouble()-.5) * force, (rand.nextDouble()-.5) * force, (rand.nextDouble()-.5) * force));
			items[i].setPickupDelay(duration * 2);
		}

		// schedule item deletion
		Bukkit.getScheduler().scheduleSyncDelayedTask(DvZ.instance, new Runnable() {
			public void run() {
				for (int i = 0; i < items.length; i++) {
					items[i].remove();
				}
			}
		}, duration);
	}
}
