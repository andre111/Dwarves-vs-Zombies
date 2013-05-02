package me.andre111.dvz.monster;

import java.util.List;
import java.util.Set;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;

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
		FileConfiguration df = DvZ.getMonsterFile();
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
			Bukkit.getPluginManager().addPermission(perm);
			loadMonster(stK2[i]);
		}
		
	}

	private void loadMonster(String mo) {
		CustomMonster monTemp = new CustomMonster();
		
		monTemp.setId(monsterCounter);
		monTemp.setGameId(DvZ.getMonsterFile().getInt("monsters."+mo+".gameType", 0));
		monTemp.setName(DvZ.getMonsterFile().getString("monsters."+mo+".name", ""));
		monTemp.setDisguise(DisguiseType.fromString(DvZ.getMonsterFile().getString("monsters."+mo+".disguise", "")));
		monTemp.setPrefix(DvZ.getMonsterFile().getString("monsters."+mo+".chatPrefix", ""));
		monTemp.setSuffix(DvZ.getMonsterFile().getString("monsters."+mo+".chatSuffix", ""));
		monTemp.setClassItem(DvZ.getMonsterFile().getInt("monsters."+mo+".classItem", 0));
		monTemp.setClassItemDamage(DvZ.getMonsterFile().getInt("monsters."+mo+".classItemDamage", 0));
		monTemp.setClassChance(DvZ.getMonsterFile().getInt("monsters."+mo+".classChance", 100));
		//items
		List<String> items = DvZ.getMonsterFile().getStringList("monsters."+mo+".items");
		monTemp.setItems(items.toArray(new String[items.size()]));
		//effects
		List<String> effects = DvZ.getMonsterFile().getStringList("monsters."+mo+".effects");
		monTemp.setEffects(effects.toArray(new String[effects.size()]));
		//placing blocks
		monTemp.setPlaceBlocks(DvZ.getMonsterFile().getBoolean("monsters."+mo+".placeBlocks", false));
		//damagebuff
		monTemp.setDamageBuff(DvZ.getMonsterFile().getDouble("monsters."+mo+".damageBuff", 1));
		//disabled damage
		for(String d : DvZ.getMonsterFile().getStringList("monsters."+mo+".disabledDamage")) {
			monTemp.addDisabledDamage(d);
		}
		//mana
		monTemp.setMaxMana(DvZ.getMonsterFile().getInt("monsters."+mo+".manaMax", 0));
		monTemp.setManaRegen(DvZ.getMonsterFile().getInt("monsters."+mo+".manaRegen", 0));
		
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
}
