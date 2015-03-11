package me.andre111.dvz.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Language;
import me.andre111.dvz.utils.FileHandler;
import me.andre111.dvz.volatileCode.DeprecatedMethods;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
	private static String lang = "en_EN";
	private static FileConfiguration configfile;
	private static FileConfiguration langfile;
	private static FileConfiguration enlangfile;
	private static FileConfiguration dragonsfile;
	private static FileConfiguration classfile;
	private static FileConfiguration itemfile;
	private static FileConfiguration blockfile;
	private static FileConfiguration playerfile;
	private static FileConfiguration rewardfile;
	private static FileConfiguration setupfile;
	
	private static ArrayList<Material> disabledCrafts = new ArrayList<Material>();
	private static ArrayList<Material> disabledCraftsType2 = new ArrayList<Material>();
	
	public static void initConfig(DvZ plugin) {
		exportConfigs(plugin);
		
		if (!new File(plugin.getDataFolder(), "config.yml").exists()) {
			try {
				FileHandler.copyFolder(new File(plugin.getDataFolder(), "config/default/config.yml"), new File(plugin.getDataFolder(), "config.yml"));
			} catch (IOException e) {}
			DvZ.log("Generating default config.");
			//saveDefaultConfig();
		}
		//Classes and stuff
		if (!new File(plugin.getDataFolder(), "dragons.yml").exists()) {
			try {
				FileHandler.copyFolder(new File(plugin.getDataFolder(), "config/default/dragons.yml"), new File(plugin.getDataFolder(), "dragons.yml"));
			} catch (IOException e) {}
		}
		if (!new File(plugin.getDataFolder(), "classes.yml").exists()) {
			try {
				FileHandler.copyFolder(new File(plugin.getDataFolder(), "config/default/classes.yml"), new File(plugin.getDataFolder(), "classes.yml"));
			} catch (IOException e) {}
		}
		if (!new File(plugin.getDataFolder(), "items.yml").exists()) {
			try {
				FileHandler.copyFolder(new File(plugin.getDataFolder(), "config/default/items.yml"), new File(plugin.getDataFolder(), "items.yml"));
			} catch (IOException e) {}
		}
		if (!new File(plugin.getDataFolder(), "blocks.yml").exists()) {
			try {
				FileHandler.copyFolder(new File(plugin.getDataFolder(), "config/default/blocks.yml"), new File(plugin.getDataFolder(), "blocks.yml"));
			} catch (IOException e) {}
		}
		if (!new File(plugin.getDataFolder(), "players.yml").exists()) {
			try {
				FileHandler.copyFolder(new File(plugin.getDataFolder(), "config/default/players.yml"), new File(plugin.getDataFolder(), "players.yml"));
			} catch (IOException e) {}
		}
		if (!new File(plugin.getDataFolder(), "rewards.yml").exists()) {
			try {
				FileHandler.copyFolder(new File(plugin.getDataFolder(), "config/default/rewards.yml"), new File(plugin.getDataFolder(), "rewards.yml"));
			} catch (IOException e) {}
		}
		if (!new File(plugin.getDataFolder(), "setup.yml").exists()) {
			try {
				FileHandler.copyFolder(new File(plugin.getDataFolder(), "config/default/setup.yml"), new File(plugin.getDataFolder(), "setup.yml"));
			} catch (IOException e) {}
		}
		if (!new File(plugin.getDataFolder(), "spells.lua").exists()) {
			try {
				FileHandler.copyFolder(new File(plugin.getDataFolder(), "config/default/spells.lua"), new File(plugin.getDataFolder(), "spells.lua"));
			} catch (IOException e) {}
		}
		configfile = DVZFileConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
		
		lang = configfile.getString("language", "en_EN");
		langfile = DVZFileConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "lang/lang_"+lang+".yml"));
		enlangfile = DVZFileConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "lang/lang_en_EN.yml"));
		dragonsfile = DVZFileConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "dragons.yml"));
		classfile =  DVZFileConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "classes.yml"));
		itemfile = DVZFileConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "items.yml"));
		blockfile = DVZFileConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "blocks.yml"));
		playerfile = DVZFileConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "players.yml"));
		rewardfile = DVZFileConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "rewards.yml"));
		setupfile = DVZFileConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "setup.yml"));
		
		loadConfigs();
	}
	
	private static void exportConfigs(JavaPlugin plugin) {
		plugin.saveResource("config/default/config.yml", true);
		plugin.saveResource("config/default/dragons.yml", true);
		plugin.saveResource("config/default/classes.yml", true);
		plugin.saveResource("config/default/items.yml", true);
		plugin.saveResource("config/default/blocks.yml", true);
		plugin.saveResource("config/default/players.yml", true);
		plugin.saveResource("config/default/rewards.yml", true);
		plugin.saveResource("config/default/setup.yml", true);
		plugin.saveResource("config/default/spells.lua", true);
		//language
		for(String st : Language.getPossibleLanguages()) {
			if(plugin.getResource("lang/lang_"+st+".yml")!=null)
				plugin.saveResource("lang/lang_"+st+".yml", true);
			if(plugin.getResource("lang/unfinished/lang_"+st+".yml")!=null)
				plugin.saveResource("lang/unfinished/lang_"+st+".yml", true);
		}
	}
	
	private static void loadConfigs() {
		//disabled crafting recipies
		//-----------------------------
		for(String id : configfile.getStringList("disables_crafts")) {
			disabledCrafts.add(DeprecatedMethods.getMaterialFromInternalName(id)); 
		}
		//type 2(new dvz needs more crafts to disable)
		for(String id : configfile.getStringList("disables_crafts_type2")) {
			disabledCraftsType2.add(DeprecatedMethods.getMaterialFromInternalName(id));
		}
	}
	
	public static boolean isCraftDisabled(Material mat, int gameType) {
		if (gameType==1)
			return disabledCrafts.contains(mat);
		else if (gameType==2)
			return disabledCraftsType2.contains(mat);
		else
			return false;
	}
	
	public static FileConfiguration getLanguage() {
		return langfile;
	}
	public static FileConfiguration getDefaultLanguage() {
		return enlangfile;
	}
	public static FileConfiguration getDragonsFile() {
		return dragonsfile;
	}
	public static FileConfiguration getClassFile() {
		return classfile;
	}
	public static FileConfiguration getItemFile() {
		return itemfile;
	}
	public static FileConfiguration getBlockFile() {
		return blockfile;
	}
	public static FileConfiguration getPlayerFile() {
		return playerfile;
	}
	public static FileConfiguration getRewardFile() {
		return rewardfile;
	}
	public static FileConfiguration getSetupFile() {
		return setupfile;
	}
	public static FileConfiguration getStaticConfig() {
		return configfile;
	}
	
	public static void reloadConfig(String name) {
		if(name.equalsIgnoreCase("dwarves")) {
			classfile =  DVZFileConfiguration.loadConfiguration(new File(DvZ.instance.getDataFolder(), "classes.yml"));
		}
		if(name.equalsIgnoreCase("items")) {
			itemfile = DVZFileConfiguration.loadConfiguration(new File(DvZ.instance.getDataFolder(), "items.yml"));
		}
		if(name.equalsIgnoreCase("blocks")) {
			blockfile = DVZFileConfiguration.loadConfiguration(new File(DvZ.instance.getDataFolder(), "blocks.yml"));
		}
		if(name.equalsIgnoreCase("players")) {
			playerfile = DVZFileConfiguration.loadConfiguration(new File(DvZ.instance.getDataFolder(), "players.yml"));
		}
		if(name.equalsIgnoreCase("language")) {
			langfile = DVZFileConfiguration.loadConfiguration(new File(DvZ.instance.getDataFolder(), "lang/lang_"+lang+".yml"));
		}
		if(name.equalsIgnoreCase("rewards")) {
			rewardfile = DVZFileConfiguration.loadConfiguration(new File(DvZ.instance.getDataFolder(), "rewards.yml"));
		}
	}
}
