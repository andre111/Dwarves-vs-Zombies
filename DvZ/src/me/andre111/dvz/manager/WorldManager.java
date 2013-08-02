package me.andre111.dvz.manager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.GameType;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.utils.FileHandler;
import me.andre111.dvz.utils.InventoryHandler;
import me.andre111.dvz.volatileCode.DynamicClassFunctions;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldManager {

	
	public static void saveWorld(CommandSender sender, String name) {
		Date dt = new Date();
		SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd_HH-mm-ss" );
		String timer = df.format( dt );
		
		File world = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+name+"/");
		File cworld = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+name+"_sDvZ_"+timer+"/");
		boolean failed = false;
		try {
			FileHandler.copyFolder(world, cworld);
		} catch (IOException e) {
			sender.sendMessage(ConfigManager.getLanguage().getString("string_save_fail","Could not save the World!"));
			failed = true;
		}
		if(!failed) {
			sender.sendMessage(ConfigManager.getLanguage().getString("string_save_succes","Saved a Copy of the World!"));
		}
	}
	
	public static void createWorld(CommandSender sender, String name) {
		File world = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+name+"/");
		
		String free = getFreeWorld("");
		
		File cworld = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+ConfigManager.getStaticConfig().getString("world_prefix", "DvZ_")+"Worlds/"+free+"/");
		boolean failed = false;
		try {
			FileHandler.copyFolder(world, cworld);
			File uidf = new File(cworld, "uid.dat");
			if(uidf.exists()) uidf.delete();
		} catch (IOException e) {
			sender.sendMessage(ConfigManager.getLanguage().getString("string_save_fail","Could not save the World!"));
			failed = true;
		}
		if(!failed) {
			sender.sendMessage(ConfigManager.getLanguage().getString("string_save_succes","Saved a Copy of the World!"));
		}
		
		maxWorld = getFreeWorld("");
	}
	
	private static String getFreeWorld(String add) {
		int akt = 0;
		
		File f;
		do {
			akt++;
			f = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+ConfigManager.getStaticConfig().getString("world_prefix", "DvZ_")+"Worlds/"+add+""+akt+"/");
		} while(f.exists());
		
		return ""+akt;
	}
	
	public static void resetMainWorld(final int id) {
		final World w = Bukkit.getServer().getWorld(ConfigManager.getStaticConfig().getString("world_prefix", "DvZ_")+"Main"+id+"");
		if(w!=null) {
			final String wname = w.getName();
			w.setAutoSave(false);
			
			for ( Player player : w.getPlayers() ) {
				InventoryHandler.clearInv(player, false);
				if(ConfigManager.getStaticConfig().getString("use_lobby", "true").equals("true"))
					player.teleport(Bukkit.getServer().getWorld(ConfigManager.getStaticConfig().getString("world_prefix", "DvZ_")+"Lobby").getSpawnLocation());
				else
					player.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
				player.sendMessage(ConfigManager.getLanguage().getString("string_tp_reset", "World is resetting - You have been teleported to the Lobby"));
			}
		
			//do not register a task/delete the world on serverstop
			if(!Bukkit.getPluginManager().isPluginEnabled(DvZ.instance)) return;
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(DvZ.instance, new Runnable() {
				public void run() {
					if(w!=null) {
						File wf = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+ConfigManager.getStaticConfig().getString("world_prefix", "DvZ_")+"Main"+id+"/");
						
						DynamicClassFunctions.bindRegionFiles();
						DynamicClassFunctions.forceUnloadWorld(w);
						DynamicClassFunctions.clearWorldReference(wname);
						
						FileHandler.deleteFolder(wf);
						
					}
				}
			}, 40);
		}
	}
	
	//TODO - Neue Main Welt generieren
	private static Random mapRandom = new Random();
	private static String maxWorld = "";
	private static String maxWorldType1 = "";
	private static String maxWorldType2 = "";
	public static void newMainWorld(final int id) {
		int gameType = GameType.getDwarfAndMonsterTypes(DvZ.instance.getGame(id).getGameType());
		
		if(maxWorld.equals("")) {
			maxWorld = getFreeWorld("");
		}
		if(maxWorldType1.equals("")) {
			maxWorldType1 = getFreeWorld("Type1/");
		}
		if(maxWorldType2.equals("")) {
			maxWorldType2 = getFreeWorld("Type2/");
		}
		
		int extra = Integer.parseInt(maxWorldType1)-1;
		if(gameType==2) extra = Integer.parseInt(maxWorldType2)-1;
		
		int normal = Integer.parseInt(maxWorld)-1;
		int max = normal + extra;
		
		if(max>0) {
			int pos = mapRandom.nextInt(max)+1;
			String add = "";
			//if it excedes the normal number, use the special worlds
			if(pos>normal) {
				pos = pos - normal;
				add = "Type1/";
				if(gameType==2) add = "Type2/";
			}
			
			File worldfile = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+ConfigManager.getStaticConfig().getString("world_prefix", "DvZ_")+"Worlds/"+add+""+pos+"/");
			File mainfile = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+ConfigManager.getStaticConfig().getString("world_prefix", "DvZ_")+"Main"+id+"/");
			try {
				FileHandler.copyFolder(worldfile, mainfile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			DvZ.instance.getGame(id).loadGameInfo();
			Bukkit.getServer().createWorld(new WorldCreator(ConfigManager.getStaticConfig().getString("world_prefix", "DvZ_")+"Main"+id+""));
		} else {
			DvZ.instance.getGame(id).broadcastMessage("No saved DvZ world found! Cannot start the Game! Autogenerating a DvZ world will come in Version 1.6!");
			//Bukkit.getServer().createWorld(new WorldCreator(this.getConfig().getString("world_prefix", "DvZ_")+"Main"));
		}
	}
	
	public static void reload() {
		maxWorld = "";
		maxWorldType1 = "";
		maxWorldType2 = "";
	}
}
