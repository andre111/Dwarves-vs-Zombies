package me.andre111.dvz.dwarf;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import me.andre111.dvz.DvZ;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class DwarfManager {
	private CustomDwarf[] dwarves;
	private int dwarfCounter;
	
	public void loadDwarfes() {
		FileConfiguration df = DvZ.getClassFile();
		//dwarves
		dwarfCounter = 0;
		ConfigurationSection as = df.getConfigurationSection("dwarves");
		Set<String> strings2 = as.getKeys(false);
		String[] stK2 = strings2.toArray(new String[strings2.size()]);
		//meax of 20 dwarves
		int length = stK2.length;
		if(length>20) length = 20;
		//load monster
		dwarves = new CustomDwarf[length];
		for(int i=0; i<length; i++) {
			loadDwarf(stK2[i]);
		}
	}
	
	private void loadDwarf(String dw) {
		CustomDwarf dwTemp = new CustomDwarf();
		
		dwTemp.setId(dwarfCounter);
		dwTemp.setGameId(DvZ.getClassFile().getInt("dwarves."+dw+".gameType", 0));
		dwTemp.setName(DvZ.getClassFile().getString("dwarves."+dw+".name", ""));
		dwTemp.setPrefix(DvZ.getClassFile().getString("dwarves."+dw+".chatPrefix", ""));
		dwTemp.setSuffix(DvZ.getClassFile().getString("dwarves."+dw+".chatSuffix", ""));
		dwTemp.setClassItem(DvZ.getClassFile().getInt("dwarves."+dw+".classItem", 0));
		dwTemp.setClassItemDamage(DvZ.getClassFile().getInt("dwarves."+dw+".classItemDamage", 0));
		dwTemp.setClassChance(DvZ.getClassFile().getInt("dwarves."+dw+".classChance", 100));
		//items
		List<String> items = DvZ.getClassFile().getStringList("dwarves."+dw+".items");
		dwTemp.setItems(items.toArray(new String[items.size()]));
		List<String> citems = DvZ.getClassFile().getStringList("dwarves."+dw+".crystalItems");
		dwTemp.setCrystalItems(citems.toArray(new String[citems.size()]));
		//effects
		List<String> effects = DvZ.getClassFile().getStringList("dwarves."+dw+".effects");
		dwTemp.setEffects(effects.toArray(new String[effects.size()]));
		//damagebuff
		dwTemp.setDamageBuff(DvZ.getClassFile().getDouble("dwarves."+dw+".damageBuff", 1));
		//disabled damage
		for(String d : DvZ.getClassFile().getStringList("dwarves."+dw+".disabledDamage")) {
			dwTemp.addDisabledDamage(d);
		}
		//mana
		dwTemp.setMaxMana(DvZ.getClassFile().getInt("dwarves."+dw+".manaMax", 0));
		dwTemp.setManaRegen(DvZ.getClassFile().getInt("dwarves."+dw+".manaRegen", 0));
		
		//spell
		dwTemp.setSpellEnabled(DvZ.getClassFile().getBoolean("dwarves."+dw+".spell.enable", false));
		dwTemp.setSpellTime(DvZ.getClassFile().getInt("dwarves."+dw+".spell.time", 0));
		dwTemp.setSpellItem(DvZ.getClassFile().getInt("dwarves."+dw+".spell.item", 0));
		dwTemp.setSpellName(DvZ.getClassFile().getString("dwarves."+dw+".spell.name", ""));
		dwTemp.setSpellNeedId(DvZ.getClassFile().getInt("dwarves."+dw+".spell.need.id", 0));
		dwTemp.setSpellNeedData(DvZ.getClassFile().getInt("dwarves."+dw+".spell.need.data", 0));
		dwTemp.setSpellNeedCount(DvZ.getClassFile().getInt("dwarves."+dw+".spell.need.count", 0));
		dwTemp.setSpellNeed(DvZ.getClassFile().getString("dwarves."+dw+".spell.needString", ""));
		dwTemp.setSpellFail(DvZ.getClassFile().getString("dwarves."+dw+".spell.failString", ""));
		dwTemp.setSpellInv(DvZ.getClassFile().getBoolean("dwarves."+dw+".spell.inventory", false));
		dwTemp.setSpellExp(DvZ.getClassFile().getInt("dwarves."+dw+".spell.exp", 0));
		List<String> spellItems = DvZ.getClassFile().getStringList("dwarves."+dw+".spell.items");
		dwTemp.setSpellItems(spellItems.toArray(new String[spellItems.size()]));
		
		//piston
		dwTemp.setPistonEnabled(DvZ.getClassFile().getBoolean("dwarves."+dw+".piston.enable", false));
		dwTemp.setPistonBlockAbove(DvZ.getClassFile().getInt("dwarves."+dw+".piston.aboveID", 0));
		dwTemp.setPistonBlockBelow(DvZ.getClassFile().getInt("dwarves."+dw+".piston.belowID", 0));
		dwTemp.setPistonChange(DvZ.getClassFile().getStringList("dwarves."+dw+".piston.change"));
		
		//itemtransmute
		dwTemp.setItemBlockAbove(DvZ.getClassFile().getInt("dwarves."+dw+".specialitems.aboveID", 0));
		dwTemp.setItemBlockBelow(DvZ.getClassFile().getInt("dwarves."+dw+".specialitems.belowID", 0));
		List<String> tSt = DvZ.getClassFile().getStringList("dwarves."+dw+".specialitems.transmuteRightClick");
		ArrayList<String> tiSt = new ArrayList<String>();
		tiSt.addAll(tSt);
		dwTemp.setTransmuteItems(tiSt);
		List<String> tSt2 = DvZ.getClassFile().getStringList("dwarves."+dw+".specialitems.transmuteBlockBreak");
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
}
