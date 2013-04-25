package me.andre111.dvz.item;

import java.util.List;
import java.util.Set;

import me.andre111.dvz.DvZ;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class ItemManager {
	private CustomItem[] items;
	private int itemCounter;
	
	public void loadItems() {
		FileConfiguration df = DvZ.getItemFile();
		//items
		itemCounter = 0;
		ConfigurationSection as = df.getConfigurationSection("items");
		Set<String> strings2 = as.getKeys(false);
		String[] stK2 = strings2.toArray(new String[strings2.size()]);
		//load items
		items = new CustomItem[stK2.length];
		for(int i=0; i<stK2.length; i++) {
			loadItem(stK2[i]);
		}
		
	}

	private void loadItem(String it) {
		CustomItem itTemp = new CustomItem();
		itTemp.setInternalName(it);
		itTemp.setName(DvZ.getItemFile().getString("items."+it+".name", ""));
		List<String> lores = DvZ.getItemFile().getStringList("items."+it+".lore");
		for(String st : lores) {
			itTemp.addLore(st);
		}
		itTemp.setID(DvZ.getItemFile().getInt("items."+it+".id", 0));
		itTemp.setDamage(DvZ.getItemFile().getInt("items."+it+".data", 0));
		itTemp.setUse(DvZ.getItemFile().getBoolean("items."+it+".useUp", false));
		
		//Rightclick
		List<String> effects = DvZ.getItemFile().getStringList("items."+it+".rightclick.effects");
		if(effects.size()>0)
		for(String st : effects) {
			itTemp.addEffectR(st);
		}
		List<String> sounds = DvZ.getItemFile().getStringList("items."+it+".rightclick.sounds");
		if(sounds.size()>0)
		for(String st : sounds) {
			itTemp.addSoundR(st);
		}
		itTemp.setCooldownR(DvZ.getItemFile().getInt("items."+it+".rightclick.cooldown", 0));
		itTemp.setManaCostR(DvZ.getItemFile().getInt("items."+it+".rightclick.mana.cost", 0));
		//leftclick
		List<String> effectsl = DvZ.getItemFile().getStringList("items."+it+".leftclick.effects");
		if(effectsl.size()>0)
		for(String st : effectsl) {
			itTemp.addEffectL(st);
		}
		List<String> soundsl = DvZ.getItemFile().getStringList("items."+it+".leftclick.sounds");
		if(soundsl.size()>0)
		for(String st : soundsl) {
			itTemp.addSoundL(st);
		}
		itTemp.setCooldownL(DvZ.getItemFile().getInt("items."+it+".leftclick.cooldown", 0));
		itTemp.setManaCostL(DvZ.getItemFile().getInt("items."+it+".leftclick.mana.cost", 0));
		
		//Cast
		//right
		ConfigurationSection as = DvZ.getItemFile().getConfigurationSection("items."+it+".rightclick.casts");
		if(as!=null) {
			Set<String> strings2 = as.getKeys(false);
			if(strings2.size()>0) {
				String[] castsR = strings2.toArray(new String[strings2.size()]);
				
				itTemp.setSizeR(castsR.length);
				for(int i=0; i<castsR.length; i++) {
					loadCast(itTemp, it, castsR[i], false, i);
				}
			}
		}
		
		//left
		ConfigurationSection asl = DvZ.getItemFile().getConfigurationSection("items."+it+".leftclick.casts");
		if(asl!=null) {
			Set<String> strings = asl.getKeys(false);
			if(strings.size()>0) {
				String[] castsL = strings.toArray(new String[strings.size()]);
				
				itTemp.setSizeL(castsL.length);
				for(int i=0; i<castsL.length; i++) {
					loadCast(itTemp, it, castsL[i], true, i);
				}
			}
		}
	
		items[itemCounter] = itTemp;
		itemCounter++;
	}
	
	private void loadCast(CustomItem itTemp, String it, String name, boolean left, int id) {
		String click = "rightclick";
		if(left) click = "leftclick";
		String basename = "items."+it+"."+click+".casts."+name+".";
		
		//leftclick
		String cast = DvZ.getItemFile().getString(basename+"cast", "");
		try {
			if(!cast.contains("me.andre111.dvz.item.spell.")) {
				cast = "me.andre111.dvz.item.spell." + cast;
			}
			Class<?> c = Class.forName(cast);
			if(c.getSuperclass().equals(ItemSpell.class)) {
				if(left) {
					itTemp.setCastL((ItemSpell) c.newInstance(), id);
					itTemp.getCastL(id).setItemName(it);
					itTemp.getCastL(id).setLeft(true);
					itTemp.getCastL(id).setRequire(DvZ.getItemFile().getInt(basename+"require", -1));
				} else {
					itTemp.setCastR((ItemSpell) c.newInstance(), id);
					itTemp.getCastR(id).setItemName(it);
					itTemp.getCastR(id).setLeft(true);
					itTemp.getCastR(id).setRequire(DvZ.getItemFile().getInt(basename+"require", -1));
				}
				//new method, for loading more than 2 cast vars
				List<String> stList = DvZ.getItemFile().getStringList(basename+"castVars");
				ItemSpell itS;
				if(left) itS = itTemp.getCastL(id);
				else itS = itTemp.getCastR(id);
				
				for(int i=0; i<stList.size(); i++) {
					itS.setCastVar(i, stList.get(i));
					try {
						double d = Double.parseDouble(stList.get(i));
						itS.setCastVar(i, d);
					} catch (NumberFormatException  e) {
					}
				}
				//changed to string reader, because doublelist skips string
				//-> numbers get messed up
			}
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		if(left) {
			if(itTemp.getCastL(id)==null) itTemp.setCastL(new ItemSpell(), id);
		} else {
			if(itTemp.getCastR(id)==null) itTemp.setCastR(new ItemSpell(), id);
		}
	}
	
	public ItemStack getItemStackByName(String name) {
		for(int i=0; i<items.length; i++) {
			if(name.equals(items[i].getInternalName())) {
				return items[i].getItemStack();
			}
		}
		
		return null;
	}
	public CustomItem getItemByDisplayName(String name) {
		for(int i=0; i<items.length; i++) {
			if(name.equals(items[i].getName())) {
				return items[i];
			}
		}
		
		return null;
	}
	public CustomItem getItemByName(String name) {
		for(int i=0; i<items.length; i++) {
			if(name.equals(items[i].getInternalName())) {
				return items[i];
			}
		}
		
		return null;
	}
}
