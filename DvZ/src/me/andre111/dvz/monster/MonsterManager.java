package me.andre111.dvz.monster;

import java.util.List;
import java.util.Set;

import me.andre111.dvz.DvZ;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

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
		//meax of 20 monsters
		int length = stK2.length;
		if(length>20) length = 20;
		//load monster
		monsters = new CustomMonster[length];
		for(int i=0; i<length; i++) {
			loadMonster(stK2[i]);
		}
		
	}

	private void loadMonster(String mo) {
		CustomMonster monTemp = new CustomMonster();
		
		monTemp.setId(monsterCounter);
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
		//items
		loadItem(mo, monTemp, 0);
		loadItem(mo, monTemp, 1);
		
		monsters[monsterCounter] = monTemp;
		monsterCounter++;
	}
	
	private void loadItem(String mo, CustomMonster monTemp, int id) {
		CustomMonsterItem itTemp = new CustomMonsterItem();
		
		itTemp.setId(id+1);
		itTemp.setItem(DvZ.getMonsterFile().getString("monsters."+mo+".spellitem"+id+".item", ""));
		itTemp.setName(DvZ.getMonsterFile().getString("monsters."+mo+".spellitem"+id+".name", ""));
		itTemp.setGetAtSpawn(DvZ.getMonsterFile().getBoolean("monsters."+mo+".spellitem"+id+".getAtSpawn", false));
		itTemp.setTime(DvZ.getMonsterFile().getInt("monsters."+mo+".spellitem"+id+".time", 30));
		//Cast
		String attack = DvZ.getMonsterFile().getString("monsters."+mo+".spellitem"+id+".cast", "");
		try {
			if(!attack.contains("me.andre111.dvz.monster.attack.")) {
				attack = "me.andre111.dvz.monster.attack." + attack;
			}
			Class<?> c = Class.forName(attack);
			if(c.getSuperclass().equals(MonsterAttack.class)) {
				itTemp.setCast((MonsterAttack) c.newInstance());
				//double
				itTemp.getCast().setCastVar(0, DvZ.getMonsterFile().getDouble("monsters."+mo+".spellitem"+id+".castVar0", 0));
				itTemp.getCast().setCastVar(1, DvZ.getMonsterFile().getDouble("monsters."+mo+".spellitem"+id+".castVar1", 0));
				//string
				itTemp.getCast().setCastVar(0, DvZ.getMonsterFile().getString("monsters."+mo+".spellitem"+id+".castVar0", ""));
				itTemp.getCast().setCastVar(1, DvZ.getMonsterFile().getString("monsters."+mo+".spellitem"+id+".castVar1", ""));
				//new method, for loading more than 2 cast vars
				List<String> stList = DvZ.getMonsterFile().getStringList("monsters."+mo+".spellitem"+id+".castVars");
				for(int i=0; i<stList.size(); i++) {
					itTemp.getCast().setCastVar(i, stList.get(i));
					try {
						double d = Double.parseDouble(stList.get(i));
						itTemp.getCast().setCastVar(i, d);
					} catch (NumberFormatException  e) {
					}
				}
				//changed to string reader, because doublelist skips string
				//-> numbers get messed up
				//id
				itTemp.getCast().setId(itTemp.getId());
			}
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		
		if(id==0) monTemp.setItem1(itTemp);
		else if(id==1) monTemp.setItem2(itTemp);
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
