package me.andre111.dvz.utils;

import me.andre111.dvz.DvZ;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class MultiverseHandler {

	public static boolean deleteWorld(World w) {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		 
        if (plugin instanceof MultiverseCore) {
        	MultiverseCore mvc = (MultiverseCore) plugin;
        	
        	if(mvc.getMVWorldManager().isMVWorld(w)) {
        		DvZ.log("Unregistering world from Multiverse!");
        		//mvc.getMVWorldManager().removeWorldFromConfig(w.getName());
        		mvc.getMVWorldManager().unloadWorld(w.getName());
        	}
        	//mvc.getMVWorldManager().deleteWorld(w.getName(), true, true);
        	return true;
        }
        
        return false;
	}
}
