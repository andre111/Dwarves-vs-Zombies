package me.andre111.dvz.item.effect;

import org.bukkit.Effect;
import org.bukkit.Location;

import me.andre111.dvz.item.ItemEffect;

public class ItemEffectNormal extends ItemEffect {
	private String eName = "";
	private int eData = 0;

	@Override
	public void setVars(String vars) {
		String[] split = vars.split(":");
		
		if(split.length>0) eName = split[0];
		if(split.length>1) eData = Integer.parseInt(split[1]);
	}

	@Override
	public void play(Location loc) {
		if(!eName.equals("")) {
			loc.getWorld().playEffect(loc, Effect.valueOf(eName), eData);
		}
	}
}
