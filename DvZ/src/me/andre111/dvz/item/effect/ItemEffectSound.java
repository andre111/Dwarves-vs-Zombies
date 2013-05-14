package me.andre111.dvz.item.effect;

import org.bukkit.Location;
import org.bukkit.Sound;

import me.andre111.dvz.item.ItemEffect;

public class ItemEffectSound extends ItemEffect {
	private String sName = "";
	private float sVolume = 1;
	private float sPitch = 1;

	@Override
	public void setVars(String vars) {
		String[] split = vars.split(":");
		
		if(split.length>0) sName = split[0];
		if(split.length>1) sVolume = Float.parseFloat(split[1]);
		if(split.length>2) sPitch = Float.parseFloat(split[2]);
	}

	@Override
	public void play(Location loc) {
		if(!sName.equals("")) {
			loc.getWorld().playSound(loc, Sound.valueOf(sName), sVolume, sPitch);
		}
	}
}
