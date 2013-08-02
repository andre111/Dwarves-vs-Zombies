package me.andre111.dvz.item.enchant;

import java.util.List;
import java.util.Set;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.item.ItemManager;
import me.andre111.dvz.item.ItemSpell;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class DVZEnchantmentManager {
	private CustomEnchant[] enchants;
	private int enchantCounter;
	
	
	public void loadEnchants() {
		FileConfiguration df = ConfigManager.getItemFile();
		//items
		enchantCounter = 0;
		ConfigurationSection as = df.getConfigurationSection("enchantments");
		Set<String> strings2 = as.getKeys(false);
		String[] stK2 = strings2.toArray(new String[strings2.size()]);
		//load items
		enchants = new CustomEnchant[stK2.length];
		for(int i=0; i<stK2.length; i++) {
			loadEnchant(stK2[i]);
		}
	}
	
	private void loadEnchant(String en) {
		CustomEnchant enTemp = new CustomEnchant();
		
		enTemp.setInternalName(en);
		enTemp.setName(ConfigManager.getItemFile().getString("enchantments."+en+".name", ""));
		List<String> effects = ConfigManager.getItemFile().getStringList("enchantments."+en+".effects");
		if(effects.size()>0)
		for(String st : effects) {
			enTemp.addEffect(ItemManager.getItemEffect(st));
		}
		
		//Cast
		//right
		ConfigurationSection as = ConfigManager.getItemFile().getConfigurationSection("enchantments."+en+".casts");
		if(as!=null) {
			Set<String> strings2 = as.getKeys(false);
			if(strings2.size()>0) {
				String[] casts = strings2.toArray(new String[strings2.size()]);

				enTemp.setSize(casts.length);
				for(int i=0; i<casts.length; i++) {
					loadCast(enTemp, en, casts[i], i);
				}
			}
		}

		enchants[enchantCounter] = enTemp;
		enchantCounter++;
	}
	
	private void loadCast(CustomEnchant enTemp, String en, String name, int id) {
		String basename = "enchantments."+en+".casts."+name+".";

		//leftclick
		String cast = ConfigManager.getItemFile().getString(basename+"cast", "");
		try {
			if(!cast.contains("me.andre111.dvz.item.spell.")) {
				cast = "me.andre111.dvz.item.spell." + cast;
			}
			Class<?> c = Class.forName(cast);
			if(c.getSuperclass().equals(ItemSpell.class)) {
				enTemp.setCast((ItemSpell) c.newInstance(), id);
				enTemp.getCast(id).setItemName(en);
				enTemp.getCast(id).setAction(10);
				enTemp.getCast(id).setRequire(ConfigManager.getItemFile().getInt(basename+"require", -1));
				
				//new method, for loading more than 2 cast vars
				List<String> stList = ConfigManager.getItemFile().getStringList(basename+"castVars");
				ItemSpell itS = enTemp.getCast(id);

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
		if(enTemp.getCast(id)==null) enTemp.setCast(new ItemSpell(), id);
	}
	
	//cast an enchantment
	private void castOn(Player attacker, Player player, CustomEnchant ce) {
		Game game = DvZ.instance.getPlayerGame(attacker.getName());
		
		if(game!=null) {
			ce.cast(game, attacker, player);
		}
	}
	
	//get enchantments from item
	public void attackPlayerByPlayer(Player attacker, Player player, ItemStack it) {
		if(it.getItemMeta().getLore()==null) return;
		
		for(String st : it.getItemMeta().getLore()) {
			CustomEnchant ce = getEnchantmentByDisplayname(st);
			if(ce!=null) {
				castOn(attacker, player, ce);
			}
		}
	}
	
	//get enchantments from arrow
	public void attackPlayerByProjectile(Player attacker, Player player, Projectile a) {
		int pos = 0;
		while(!a.getMetadata("dvz_enchant_"+pos).isEmpty()) {
			String ench = a.getMetadata("dvz_enchant_"+pos).get(0).asString();
			CustomEnchant ce = getEnchantmentByName(ench);
			if(ce!=null) {
				castOn(attacker, player, ce);
			}
			
			pos++;
		}
	}
	
	//save enchants on arrow
	public void procectileShoot(ItemStack bow, Projectile a) {
		if(bow.getItemMeta().getLore()==null) return;
		
		int pos = 0;
		for(String st : bow.getItemMeta().getLore()) {
			CustomEnchant ce = getEnchantmentByDisplayname(st);
			if(ce!=null) {
				a.setMetadata("dvz_enchant_"+pos, new FixedMetadataValue(DvZ.instance, ce.getInternalName()));
				pos++;
			}
		}
	}
	
	public boolean isCustomEnchantment(String lore) {
		if(getEnchantmentByDisplayname(lore)!=null) return true;
		
		return false;
	}
	
	//IMPORTANT: This expects an level at the end of the string
	public CustomEnchant getEnchantmentByDisplayname(String name) {
		String[] split = name.split(" ");
		String putTogether = "";
		for(int i=0; i<split.length-1; i++) {
			if(putTogether.equals("")) putTogether = split[i];
			else putTogether = putTogether + " " + split[i];
		}
		putTogether = putTogether.replace(ChatColor.GRAY+"", "");
		
		for(int i=0; i<enchants.length; i++) {
			if(putTogether.equals(enchants[i].getName())) {
				return enchants[i];
			}
		}
		
		return null;
	}
	
	public CustomEnchant getEnchantmentByName(String name) {
		for(int i=0; i<enchants.length; i++) {
			if(name.equals(enchants[i].getInternalName())) {
				return enchants[i];
			}
		}
		
		return null;
	}
}
