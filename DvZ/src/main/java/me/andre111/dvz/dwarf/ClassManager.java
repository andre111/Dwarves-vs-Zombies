package me.andre111.dvz.dwarf;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import me.andre111.dvz.Game;
import me.andre111.dvz.config.ConfigManager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class ClassManager {
	private CustomClass[] classes;
	private int classCounter;
	
	public void loadClasses() {
		FileConfiguration df = ConfigManager.getClassFile();
		//dwarves
		classCounter = 0;
		ConfigurationSection as = df.getConfigurationSection("classes");
		Set<String> strings2 = as.getKeys(false);
		String[] stK2 = strings2.toArray(new String[strings2.size()]);
		//change the limits for the dwarves/monsters
		int length = stK2.length;
		Game.classMax = Game.classMin+length;
		//Game.monsterMin = Game.classMax + 1;
		Game.dragonMin = Game.classMax + 1;
		//load monster
		classes = new CustomClass[length];
		for(int i=0; i<length; i++) {
			Permission perm = new Permission("dvz.classes."+i, PermissionDefault.TRUE);
			//perm.addParent("dvz.*", true); - broken?
			if(Bukkit.getPluginManager().getPermission("dvz.classes."+i)==null)
				Bukkit.getPluginManager().addPermission(perm);
			
			loadClass(stK2[i]);
		}
	}
	
	private void loadClass(String dw) {
		CustomClass dwTemp = new CustomClass();
		
		dwTemp.setId(classCounter);
		dwTemp.setGameId(ConfigManager.getClassFile().getInt("classes."+dw+".gameType", 0));
		dwTemp.setName(ConfigManager.getClassFile().getString("classes."+dw+".name", ""));
		dwTemp.setDisguise(ConfigManager.getClassFile().getString("classes."+dw+".disguise", ""));
		dwTemp.setPrefix(ConfigManager.getClassFile().getString("classes."+dw+".chatPrefix", ""));
		dwTemp.setSuffix(ConfigManager.getClassFile().getString("classes."+dw+".chatSuffix", ""));
		
		Material mat = Material.getMaterial(ConfigManager.getClassFile().getInt("classes."+dw+".classItem", 0));
		dwTemp.setClassItem(mat);
		dwTemp.setClassItemDamage(ConfigManager.getClassFile().getInt("classes."+dw+".classItemDamage", 0));
		dwTemp.setClassChance(ConfigManager.getClassFile().getInt("classes."+dw+".classChance", 100));
		//items
		List<String> items = ConfigManager.getClassFile().getStringList("classes."+dw+".items");
		dwTemp.setItems(items.toArray(new String[items.size()]));
		List<String> citems = ConfigManager.getClassFile().getStringList("classes."+dw+".crystalItems");
		dwTemp.setCrystalItems(citems.toArray(new String[citems.size()]));
		//effects
		List<String> effects = ConfigManager.getClassFile().getStringList("classes."+dw+".effects");
		dwTemp.setEffects(effects.toArray(new String[effects.size()]));
		//placing blocks
		dwTemp.setPlaceBlocks(ConfigManager.getClassFile().getBoolean("classes."+dw+".placeBlocks", true));
		//damagebuff
		dwTemp.setDamageBuff(ConfigManager.getClassFile().getDouble("classes."+dw+".damageBuff", 1));
		//disabled damage
		for(String d : ConfigManager.getClassFile().getStringList("classes."+dw+".disabledDamage")) {
			dwTemp.addDisabledDamage(d);
		}
		//mana
		dwTemp.setMaxMana(ConfigManager.getClassFile().getInt("classes."+dw+".manaMax", 0));
		dwTemp.setManaRegen(ConfigManager.getClassFile().getInt("classes."+dw+".manaRegen", 0));
		//start vars
		dwTemp.setStartHealth(ConfigManager.getClassFile().getInt("classes."+dw+".startHealth", 20));
		dwTemp.setStartHunger(ConfigManager.getClassFile().getInt("classes."+dw+".startHunger", 20));
		dwTemp.setStartSat((float) ConfigManager.getClassFile().getDouble("classes."+dw+".startSaturation", 20F));
		dwTemp.setStartMessage(ConfigManager.getClassFile().getString("classes."+dw+".startMessage", ""));
		
		//piston
		dwTemp.setPistonEnabled(ConfigManager.getClassFile().getBoolean("classes."+dw+".piston.enable", false));
		dwTemp.setPistonChange(ConfigManager.getClassFile().getStringList("classes."+dw+".piston.change"));
		
		//itemtransmute
		List<String> tSt = ConfigManager.getClassFile().getStringList("classes."+dw+".specialitems.transmuteRightClick");
		ArrayList<String> tiSt = new ArrayList<String>();
		tiSt.addAll(tSt);
		dwTemp.setTransmuteItems(tiSt);
		List<String> tSt2 = ConfigManager.getClassFile().getStringList("classes."+dw+".specialitems.transmuteBlockBreak");
		ArrayList<String> tiSt2 = new ArrayList<String>();
		tiSt2.addAll(tSt2);
		dwTemp.setTransmuteBreakItems(tiSt2);
		
		dwTemp.setRewardOnBlockPlace(ConfigManager.getClassFile().getBoolean("classes."+dw+".rewardOnBuild", false));
		
		classes[classCounter] = dwTemp;
		classCounter++;
	}
	
	public CustomClass getClass(int id) {
		if(id>=0 && id<classCounter) 
			return classes[id];
		else
			return null;
	}
	
	public int getCount() {
		return classCounter;
	}
	
	//reload this configsection/file
	public void reload() {
		loadClasses();
	}
}
