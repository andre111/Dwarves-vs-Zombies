package me.andre111.dvz.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class DVZFileConfiguration extends YamlConfiguration {

	//Replace colorcodes
	@Override
	public String getString(String path) {
		return ChatColor.translateAlternateColorCodes('&', super.getString(path));
	}
	
	@Override
	public String getString(String path, String def) {
		return ChatColor.translateAlternateColorCodes('&', super.getString(path, def));
	}
	
	@Override
	public List<String> getStringList(String path) {
		List<String> list = new ArrayList<String>();
		
		for(String st : super.getStringList(path)) {
			list.add(ChatColor.translateAlternateColorCodes('&', st));
		}
		
		return list;
	}
	
	public static DVZFileConfiguration loadConfiguration(File file) {
		DVZFileConfiguration instance = new DVZFileConfiguration();
		try {
			instance.load(file);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} catch (InvalidConfigurationException e) {
		}
		
		return instance;
	}
}
