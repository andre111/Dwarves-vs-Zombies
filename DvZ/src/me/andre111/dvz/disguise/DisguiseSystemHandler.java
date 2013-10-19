package me.andre111.dvz.disguise;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.config.ConfigManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class DisguiseSystemHandler implements Listener {
	private static SupportedDisguises dsystem = SupportedDisguises.NOONE;
	private static DSystem disguisesystem = null;
	
	public static boolean init() {
		//Disguise-Plugin check
		if (!ConfigManager.getStaticConfig().getString("disable_dcraft_check", "false").equals("true")) {
			if (Bukkit.getPluginManager().isPluginEnabled("DisguiseCraft"))
			{
				dsystem = SupportedDisguises.DISGUISECRAFT;
			}
			
			if(dsystem==SupportedDisguises.NOONE) {
				Bukkit.getServer().getConsoleSender().sendMessage(DvZ.prefix+" "+ChatColor.RED+"No supported disguising Plugin could be found, disabling...");
				Bukkit.getPluginManager().disablePlugin(DvZ.instance);
				return false;
			}
		}
		
		//init disguisesystem
		Bukkit.getServer().getConsoleSender().sendMessage(DvZ.prefix+" Using "+dsystem.getName()+" to handle disguising Players");
		
		switch(dsystem) {
		case DISGUISECRAFT:
			disguisesystem = new DSystem_DisguiseCraft();
			break;
		}
		disguisesystem.initListeners(DvZ.instance);
		
		return true;
	}
	
	public static void disguiseP(Player player, String disguise) {
		disguisesystem.disguiseP(player, disguise);
	}
	
	public static void undisguiseP(Player player) {
		disguisesystem.undisguiseP(player);
	}
	
	public static void redisguiseP(Player player) {
		disguisesystem.redisguiseP(player);
	}
	
	public static int newEntityID() {
		return disguisesystem.newEntityID();
	}
}
