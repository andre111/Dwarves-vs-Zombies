package me.andre111.dvz.item.effect;

import org.bukkit.Location;

import me.andre111.dvz.item.ItemEffect;

public class ItemEffectLightning extends ItemEffect {
	
	@Override
	public void play(Location loc) {
		loc.getWorld().strikeLightningEffect(loc);
	}
}
