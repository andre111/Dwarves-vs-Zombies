package me.andre111.dvz.update;

import java.util.logging.Level;

import me.andre111.dvz.DvZ;

import org.bukkit.ChatColor;
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
			String update = DvZUpdateChecker.getLatestVersion();
			try {
				if (Integer.parseInt(getVersion(plugin.descriptionFile.getVersion())) < Integer.parseInt(getVersion(update.split("for")[0]))) {
					DvZ.sendPlayerMessageFormated(player, ChatColor.BLUE + "There is a new update for Dwarves vs Zombies available: " + update);
				}
			} catch (NumberFormatException e) {
				DvZ.logger.log(Level.WARNING, "Could not parse version updates.");
			}
		}
	}
	
	private String getVersion(String update) {
		for(int i=0; i<update.length(); i++) {
			if(!versionChar(update.charAt(i))) {
				update = update.replace(update.charAt(i)+"", " ");
			}
		}
		
		update = update.replace(".", "");
		update = update.replace(" ", "");
		
		//check for when only to numbers exist (1.5 add 0 ->150)
		if(update.length()==2)
			update = update + "0";
		
		return update;
	}
	
	private boolean versionChar(char c) {
		if(c=='0') return true;
		if(c=='1') return true;
		if(c=='2') return true;
		if(c=='3') return true;
		if(c=='4') return true;
		if(c=='5') return true;
		if(c=='6') return true;
		if(c=='7') return true;
		if(c=='8') return true;
		if(c=='9') return true;
		
		return false;
	}
}