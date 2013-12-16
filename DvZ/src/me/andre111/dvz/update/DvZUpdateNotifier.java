package me.andre111.dvz.update;

import me.andre111.dvz.DvZ;

import org.bukkit.entity.Player;

public class DvZUpdateNotifier implements Runnable {
	final DvZ plugin;
	Player player;
	
	public DvZUpdateNotifier(final DvZ plugin, Player player) {
		this.plugin = plugin;
		this.player = player;
	}
	
	@Override
	public void run() {
		if (player.isOnline()) {
			
		}
	}
}