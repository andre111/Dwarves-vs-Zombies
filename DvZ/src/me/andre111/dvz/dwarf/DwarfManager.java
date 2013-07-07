package me.andre111.dvz.dwarf;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import me.andre111.dvz.Game;
import me.andre111.dvz.config.ConfigManager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class DwarfManager {
	private CustomDwarf[] dwarves;
	private int dwarfCounter;
	
	public void loadDwarfes() {
		FileConfiguration df = ConfigManager.getClassFile();
		//dwarves
		dwarfCounter = 0;
		ConfigurationSection as = df.getConfigurationSection("dwarves");
		Set<String> strings2 = as.getKeys(false);
		String[] stK2 = strings2.toArray(new String[strings2.size()]);
		//change the limits for the dwarves/monsters
		int length = stK2.length;
		Game.dwarfMax = Game.dwarfMin+length;
		Game.monsterMin = Game.dwarfMax + 1;
		//load monster
		dwarves = new CustomDwarf[length];
		for(int i=0; i<length; i++) {
			Permission perm = new Permission("dvz.dwarves."+i, PermissionDefault.TRUE);
			//perm.addParent("dvz.*", true); - broken?
			if(Bukkit.getPluginManager().getPermission("dvz.dwarves."+i)==null)
				Bukkit.getPluginManager().addPermission(perm);
			
			loadDwarf(stK2[i]);
		}
	}
	
	private void loadDwarf(String dw) {
		CustomDwarf dwTemp = new CustomDwarf();
		
		dwTemp.setId(dwarfCounter);
		dwTemp.setGameId(ConfigManager.getClassFile().getInt("dwarves."+dw+".gameType", 0));
		dwTemp.setName(ConfigManager.getClassFile().getString("dwarves."+dw+".name", ""));
		dwTemp.setPrefix(ConfigManager.getClassFile().getString("dwarves."+dw+".chatPrefix", ""));
		dwTemp.setSuffix(ConfigManager.getClassFile().getString("dwarves."+dw+".chatSuffix", ""));
		dwTemp.setClassItem(ConfigManager.getClassFile().getInt("dwarves."+dw+".classItem", 0));
		dwTemp.setClassItemDamage(ConfigManager.getClassFile().getInt("dwarves."+dw+".classItemDamage", 0));
		dwTemp.setClassChance(ConfigManager.getClassFile().getInt("dwarves."+dw+".classChance", 100));
		//items
		List<String> items = ConfigManager.getClassFile().getStringList("dwarves."+dw+".items");
		dwTemp.setItems(items.toArray(new String[items.size()]));
		List<String> citems = ConfigManager.getClassFile().getStringList("dwarves."+dw+".crystalItems");
		dwTemp.setCrystalItems(citems.toArray(new String[citems.size()]));
		//effects
		List<String> effects = ConfigManager.getClassFile().getStringList("dwarves."+dw+".effects");
		dwTemp.setEffects(effects.toArray(new String[effects.size()]));
		//damagebuff
		dwTemp.setDamageBuff(ConfigManager.getClassFile().getDouble("dwarves."+dw+".damageBuff", 1));
		//disabled damage
		for(String d : ConfigManager.getClassFile().getStringList("dwarves."+dw+".disabledDamage")) {
			dwTemp.addDisabledDamage(d);
		}
		//mana
		dwTemp.setMaxMana(ConfigManager.getClassFile().getInt("dwarves."+dw+".manaMax", 0));
		dwTemp.setManaRegen(ConfigManager.getClassFile().getInt("dwarves."+dw+".manaRegen", 0));
		//start vars
		dwTemp.setStartHealth(ConfigManager.getClassFile().getInt("dwarves."+dw+".startHealth", 20));
		dwTemp.setStartHunger(ConfigManager.getClassFile().getInt("dwarves."+dw+".startHunger", 20));
		dwTemp.setStartSat((float) ConfigManager.getClassFile().getDouble("dwarves."+dw+".startSaturation", 20F));
		dwTemp.setStartMessage(ConfigManager.getClassFile().getString("dwarves."+dw+".startMessage", ""));
		
		//spell
		dwTemp.setSpellEnabled(ConfigManager.getClassFile().getBoolean("dwarves."+dw+".spell.enable", false));
		dwTemp.setSpellTime(ConfigManager.getClassFile().getInt("dwarves."+dw+".spell.time", 0));
		dwTemp.setSpellItem(ConfigManager.getClassFile().getInt("dwarves."+dw+".spell.item", 0));
		dwTemp.setSpellName(ConfigManager.getClassFile().getString("dwarves."+dw+".spell.name", ""));
		dwTemp.setSpellNeedId(ConfigManager.getClassFile().getInt("dwarves."+dw+".spell.need.id", 0));
		dwTemp.setSpellNeedData(ConfigManager.getClassFile().getInt("dwarves."+dw+".spell.need.data", 0));
		dwTemp.setSpellNeedCount(ConfigManager.getClassFile().getInt("dwarves."+dw+".spell.need.count", 0));
		dwTemp.setSpellNeed(ConfigManager.getClassFile().getString("dwarves."+dw+".spell.needString", ""));
		dwTemp.setSpellFail(ConfigManager.getClassFile().getString("dwarves."+dw+".spell.failString", ""));
		dwTemp.setSpellInv(ConfigManager.getClassFile().getBoolean("dwarves."+dw+".spell.inventory", false));
		dwTemp.setSpellExp(ConfigManager.getClassFile().getInt("dwarves."+dw+".spell.exp", 0));
		List<String> spellItems = ConfigManager.getClassFile().getStringList("dwarves."+dw+".spell.items");
		dwTemp.setSpellItems(spellItems.toArray(new String[spellItems.size()]));
		
		//piston
		dwTemp.setPistonEnabled(ConfigManager.getClassFile().getBoolean("dwarves."+dw+".piston.enable", false));
		dwTemp.setPistonChange(ConfigManager.getClassFile().getStringList("dwarves."+dw+".piston.change"));
		
		//itemtransmute
		List<String> tSt = ConfigManager.getClassFile().getStringList("dwarves."+dw+".specialitems.transmuteRightClick");
		ArrayList<String> tiSt = new ArrayList<String>();
		tiSt.addAll(tSt);
		dwTemp.setTransmuteItems(tiSt);
		List<String> tSt2 = ConfigManager.getClassFile().getStringList("dwarves."+dw+".specialitems.transmuteBlockBreak");
		ArrayList<String> tiSt2 = new ArrayList<String>();
		tiSt2.addAll(tSt2);
		dwTemp.setTransmuteBreakItems(tiSt2);
		
		dwarves[dwarfCounter] = dwTemp;
		dwarfCounter++;
	}
	
	public CustomDwarf getDwarf(int id) {
		if(id>=0 && id<dwarfCounter) 
			return dwarves[id];
		else
			return null;
	}
	
	public int getCount() {
		return dwarfCounter;
	}
	
	//reload this configsection/file
	public void reload() {
		loadDwarfes();
	}
}
