package me.andre111.dvz.volatileCode;

import java.lang.reflect.Field;
import java.util.HashMap;

import me.andre111.dvz.DvZ;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public abstract class VolatileCodeHandler {
	
	public static VolatileCodeHandler createCorrectVersion(Plugin plugin)
	{
        // Get full package string of CraftServer class, then extract the version name from that 
        // org.bukkit.craftbukkit.versionstring (or for pre-refactor, just org.bukkit.craftbukkit
		String packageName = plugin.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);
        
        if ( version.equals("v1_4_7") || version.equals("v1_4_6") || version.equals("v1_4_R1") )
        	return new V1_4_6(plugin);
        if ( version.equals("v1_5_R1") )
        	return new V1_5(plugin);
        if ( version.equals("v1_5_R2") )
        	return new V1_5_2(plugin);
        
        /*try {
       	 	Class.forName( "org.bukkit.craftbukkit.v1_5_R1.CraftServer" );
       	 	return new V1_5(plugin);
        } catch( ClassNotFoundException e ) {
        }
        try {
      	 	Class.forName( "org.bukkit.craftbukkit.v1_5_R2.CraftServer" );
      	 	return new V1_5_2(plugin);
        } catch( ClassNotFoundException e ) {
        }*/
        
        if ( version.equals("craftbukkit"))
        	Bukkit.getServer().getConsoleSender().sendMessage(DvZ.prefix+" "+ChatColor.RED+" DvZ requires at least CraftBukkit 1.4.6-R1.0 to function. Sorry.");
        else
        	Bukkit.getServer().getConsoleSender().sendMessage(DvZ.prefix+" "+ChatColor.RED+" This version of DvZ is not compatible with your server's version of CraftBukkit! (" + version + ") Please download a newer version of DvZ.");
        Bukkit.getPluginManager().disablePlugin(plugin);
        return null;
	}
	
	protected VolatileCodeHandler(Plugin plugin) { this.plugin = plugin; }
	protected Plugin plugin;
	
	public abstract void changeSeed(World w, long newSeed);
	
	@SuppressWarnings("rawtypes") 
	protected HashMap regionfiles;
	protected Field rafField;
	
	public abstract void bindRegionFiles();

	public abstract void unbindRegionFiles();

	public abstract boolean clearWorldReference(String worldName);

	public abstract void forceUnloadWorld(World world);

}
