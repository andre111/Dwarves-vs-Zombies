package me.andre111.dvz.manager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.GameType;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.utils.FileHandler;
import me.andre111.dvz.utils.InventoryHandler;
import me.andre111.dvz.utils.MultiverseHandler;
import me.andre111.dvz.volatileCode.DynamicClassFunctions;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class WorldManager {
	private static ArrayList<String> worlds = new ArrayList<String>();
	private static ArrayList<String> type1_worlds = new ArrayList<String>();
	private static ArrayList<String> type2_worlds = new ArrayList<String>();
	
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
			DvZ.sendPlayerMessageFormated(sender, ConfigManager.getLanguage().getString("string_save_fail","Could not save the World!"));
			failed = true;
		}
		if(!failed) {
			DvZ.sendPlayerMessageFormated(sender, ConfigManager.getLanguage().getString("string_save_succes","Saved a Copy of the World!"));
		}
	}
	
	public static void createWorld(CommandSender sender, String name, String newName) {
		File world = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+name+"/");
		
		File cworld = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+ConfigManager.getStaticConfig().getString("world_prefix", "DvZ_")+"Worlds/"+newName+"/");
		if(cworld.exists()) {
			DvZ.sendPlayerMessageFormated(sender, ConfigManager.getLanguage().getString("string_save_exists","A world with that name allready exists!"));
			return;
		}
		
		boolean failed = false;
		try {
			FileHandler.copyFolder(world, cworld);
			File uidf = new File(cworld, "uid.dat");
			if(uidf.exists()) uidf.delete();
		} catch (IOException e) {
			DvZ.sendPlayerMessageFormated(sender, ConfigManager.getLanguage().getString("string_save_fail","Could not save the World!"));
			failed = true;
		}
		if(!failed) {
			DvZ.sendPlayerMessageFormated(sender, ConfigManager.getLanguage().getString("string_save_succes","Saved a Copy of the World!"));
		}
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
				DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_tp_reset", "World is resetting - You have been teleported to the Lobby"));
			}
			
			//Multiverse world deletion
			if (Bukkit.getPluginManager().isPluginEnabled("Multiverse-Core")) {
				MultiverseHandler.deleteWorld(w);
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
						
						if(!FileHandler.deleteFolder(wf)) {
							DvZ.log("ERROR - Could not delete world. This will cause issues!");
						}
					}
				}
			}, 40);
		}
	}
	
	private static void searchWorlds() {
		worlds.clear();
		type1_worlds.clear();
		type2_worlds.clear();
		
		File f = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+ConfigManager.getStaticConfig().getString("world_prefix", "DvZ_")+"Worlds/");
		if(f.listFiles()!=null)
		for(File file : f.listFiles()) {
			if(file.isDirectory() && !file.getName().equals("Type1") && !file.getName().equals("Type2")) {
				worlds.add(file.getName());
			}
		}
		f = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+ConfigManager.getStaticConfig().getString("world_prefix", "DvZ_")+"Worlds/Type1/");
		if(f.listFiles()!=null)
		for(File file : f.listFiles()) {
			if(file.isDirectory()) {
				worlds.add("Type1/"+file.getName());
			}
		}
		f = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+ConfigManager.getStaticConfig().getString("world_prefix", "DvZ_")+"Worlds/Type2/");
		if(f.listFiles()!=null)
		for(File file : f.listFiles()) {
			if(file.isDirectory()) {
				worlds.add("Type2/"+file.getName());
			}
		}
	}
	
	//TODO - Neue Main Welt generieren
	private static Random mapRandom = new Random();
	public static void newMainWorld(final int id) {
		int gameType = GameType.getDwarfAndMonsterTypes(DvZ.instance.getGame(id).getGameType());
		
		ArrayList<String> new_worlds = new ArrayList<String>();
		new_worlds.addAll(worlds);
		if(gameType==1) new_worlds.addAll(type1_worlds);
		if(gameType==2) new_worlds.addAll(type2_worlds);
		
		if(new_worlds.size()>0) {
			int pos = mapRandom.nextInt(new_worlds.size());
			
			File worldfile = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+ConfigManager.getStaticConfig().getString("world_prefix", "DvZ_")+"Worlds/"+new_worlds.get(pos)+"/");
			File mainfile = new File(Bukkit.getServer().getWorldContainer().getPath()+"/"+ConfigManager.getStaticConfig().getString("world_prefix", "DvZ_")+"Main"+id+"/");
			try {
				FileHandler.copyFolder(worldfile, mainfile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			DvZ.instance.getGame(id).loadGameInfo();
			Bukkit.getServer().createWorld(new WorldCreator(ConfigManager.getStaticConfig().getString("world_prefix", "DvZ_")+"Main"+id+""));
		} else {
			DvZ.instance.getGame(id).broadcastMessage("No saved DvZ world found! Cannot start the Game!");
			//Bukkit.getServer().createWorld(new WorldCreator(this.getConfig().getString("world_prefix", "DvZ_")+"Main"));
		}
	}
	
	public static void reload() {
		searchWorlds();
	}
}
