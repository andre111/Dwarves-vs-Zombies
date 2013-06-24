package me.andre111.dvz.monster;

import java.util.List;
import java.util.Set;

import me.andre111.dvz.Game;
import me.andre111.dvz.config.ConfigManager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

public class MonsterManager {
	private CustomMonster[] monsters;
	private int monsterCounter;
	
	public void loadMonsters() {
		FileConfiguration df = ConfigManager.getMonsterFile();
		//monsters
		monsterCounter = 0;
		ConfigurationSection as = df.getConfigurationSection("monsters");
		Set<String> strings2 = as.getKeys(false);
		String[] stK2 = strings2.toArray(new String[strings2.size()]);
		//change the limits for the dwarves/monsters
		int length = stK2.length;
		Game.monsterMax = Game.monsterMin + length;
		Game.dragonMin = Game.monsterMax + 1;
		//load monster
		monsters = new CustomMonster[length];
		for(int i=0; i<length; i++) {
			Permission perm = new Permission("dvz.monster."+i, PermissionDefault.TRUE);
			//perm.addParent("dvz.*", true); - broken?
			if(Bukkit.getPluginManager().getPermission("dvz.monster."+i)==null)
				Bukkit.getPluginManager().addPermission(perm);
			loadMonster(stK2[i]);
		}
		
	}

	private void loadMonster(String mo) {
		CustomMonster monTemp = new CustomMonster();
		
		monTemp.setId(monsterCounter);
		monTemp.setGameId(ConfigManager.getMonsterFile().getInt("monsters."+mo+".gameType", 0));
		monTemp.setName(ConfigManager.getMonsterFile().getString("monsters."+mo+".name", ""));
		monTemp.setDisguise(DisguiseType.fromString(ConfigManager.getMonsterFile().getString("monsters."+mo+".disguise", "")));
		monTemp.setPrefix(ConfigManager.getMonsterFile().getString("monsters."+mo+".chatPrefix", ""));
		monTemp.setSuffix(ConfigManager.getMonsterFile().getString("monsters."+mo+".chatSuffix", ""));
		monTemp.setClassItem(ConfigManager.getMonsterFile().getInt("monsters."+mo+".classItem", 0));
		monTemp.setClassItemDamage(ConfigManager.getMonsterFile().getInt("monsters."+mo+".classItemDamage", 0));
		monTemp.setClassChance(ConfigManager.getMonsterFile().getInt("monsters."+mo+".classChance", 100));
		//items
		List<String> items = ConfigManager.getMonsterFile().getStringList("monsters."+mo+".items");
		monTemp.setItems(items.toArray(new String[items.size()]));
		//effects
		List<String> effects = ConfigManager.getMonsterFile().getStringList("monsters."+mo+".effects");
		monTemp.setEffects(effects.toArray(new String[effects.size()]));
		//placing blocks
		monTemp.setPlaceBlocks(ConfigManager.getMonsterFile().getBoolean("monsters."+mo+".placeBlocks", false));
		//damagebuff
		monTemp.setDamageBuff(ConfigManager.getMonsterFile().getDouble("monsters."+mo+".damageBuff", 1));
		//disabled damage
		for(String d : ConfigManager.getMonsterFile().getStringList("monsters."+mo+".disabledDamage")) {
			monTemp.addDisabledDamage(d);
		}
		//mana
		monTemp.setMaxMana(ConfigManager.getMonsterFile().getInt("monsters."+mo+".manaMax", 0));
		monTemp.setManaRegen(ConfigManager.getMonsterFile().getInt("monsters."+mo+".manaRegen", 0));
		//start vars
		monTemp.setStartHealth(ConfigManager.getMonsterFile().getInt("monsters."+mo+".startHealth", 20));
		monTemp.setStartHunger(ConfigManager.getMonsterFile().getInt("monsters."+mo+".startHunger", 20));
		monTemp.setStartSat((float) ConfigManager.getMonsterFile().getDouble("monsters."+mo+".startSaturation", 20F));
		
		monsters[monsterCounter] = monTemp;
		monsterCounter++;
	}
	
	public CustomMonster getMonster(int id) {
		if(id>=0 && id<monsterCounter) 
			return monsters[id];
		else
			return null;
	}
	
	public int getCount() {
		return monsterCounter;
	}
	
	//reload this configsection/file
	public void reload() {
		loadMonsters();
	}
}
