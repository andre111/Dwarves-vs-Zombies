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

	//handle strings
	@Override
	public String getString(String path) {
		return modifyString(super.getString(path));
	}
	
	@Override
	public String getString(String path, String def) {
		return modifyString(super.getString(path, def));
	}
	
	@Override
	public List<String> getStringList(String path) {
		List<String> list = new ArrayList<String>();
		
		for(String st : super.getStringList(path)) {
			list.add(modifyString(st));
		}
		
		return list;
	}
	
	private String modifyString(String st) {
		//Language support
		if(st.startsWith("lang:")) {
			String[] split = st.split(":");
			
			String key = split[1];
			st = ConfigManager.getLanguage().getString(key, ConfigManager.getDefaultLanguage().getString(key, ""));
			
			//replace -0-,-1-,...
			for(int i=2; i<split.length; i++) {
				st = st.replace("-"+(i-2)+"-", split[i]);
			}
		}
		
		//Replace colorcodes
		st = ChatColor.translateAlternateColorCodes('&', st);
		
		return st;
	}
	
	//loading the config as a DVZFileConfiguration
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
