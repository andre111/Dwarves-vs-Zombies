package me.andre111.dvz.disguise;

import me.andre111.dvz.DvZ;

import org.bukkit.entity.Player;

public interface DSystem {
	public void initListeners(DvZ plugin);
	
	public void disguiseP(Player player, DvZDisguiseType disguise);
	public void disguiseP(Player player, String disguise);
	public void undisguiseP(Player player);
	public void redisguiseP(Player player);
	
	public int newEntityID();
}
