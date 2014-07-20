package me.andre111.dvz.players;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import me.andre111.dvz.config.ConfigManager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class SpecialPlayerManager {
	private SpecialPlayer[] players;
	private int playerCounter;
	
	public void loadPlayers() {
		FileConfiguration df = ConfigManager.getPlayerFile();
		//dwarves
		playerCounter = 0;
		ConfigurationSection as = df.getConfigurationSection("players");
		Set<String> strings2 = as.getKeys(false);
		String[] stK2 = strings2.toArray(new String[strings2.size()]);
		//change the limits for the dwarves/monsters
		int length = stK2.length;
		//load monster
		players = new SpecialPlayer[length];
		for(int i=0; i<length; i++) {
			loadPlayer(stK2[i]);
		}
	}
	
	private void loadPlayer(String player) {
		SpecialPlayer spTemp = new SpecialPlayer();
		
		spTemp.setUUID(player);
		List<String> citems = ConfigManager.getPlayerFile().getStringList("players."+player+".crystalItems");
		spTemp.setCrystalItems(citems.toArray(new String[citems.size()]));
		
		spTemp.setPrefix(ConfigManager.getPlayerFile().getString("players."+player+".prefix", ""));
		spTemp.setSuffix(ConfigManager.getPlayerFile().getString("players."+player+".suffix", ""));
		
		players[playerCounter] = spTemp;
		playerCounter++;
	}
	
	public SpecialPlayer getPlayer(UUID uuid) {
		for(int i=0; i<players.length; i++) {
			if(players[i].getUUID().equals(uuid.toString())) {
				return players[i];
			}
		}
		
		return null;
	}
	
	//reload this configsection/file
	public void reload() {
		loadPlayers();
	}
}
