package me.andre111.dvz.dwarf;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.utils.ItemHandler;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CustomDwarf {
	private int id;
	private int gameId;
	private String name;
	private String prefix;
	private String suffix;
	private int classItem;
	private int classItemDamage;
	private int classChance;
	private String[] items;
	private String[] effects;
	private double damageBuff;
	private ArrayList<String> disabledDamage;
	private int maxMana;
	private int manaRegen;
	
	//spells
	private boolean spellEnabled;
	private int spellTime;
	private int spellItem;
	private String spellName;
	private int spellNeedId;
	private int spellNeedData;
	private int spellNeedCount;
	private String spellNeed;
	private String spellFail;
	private boolean spellInv;
	private int spellExp;
	private String[] spellItems;
	//piston
	private boolean pistonEnabled;
	private int pistonBlockAbove;
	private int pistonBlockBelow;
	private LinkedHashMap<Integer, Integer> pistonChange;
	
	//become custom Monster
	public void becomeDwarf(Game game, Player player) {
		game.setPlayerState(player.getName(), id+Game.dwarfMin);
		game.resetCountdowns(player.getName());
		game.getManaManager().setMaxMana(player.getName(), getMaxMana(), true);
		game.getManaManager().setManaRegen(player.getName(), getManaRegen());
		
		player.sendMessage(DvZ.getLanguage().getString("string_have_become","You have become a -0-!").replaceAll("-0-", getName()));
		
		ItemHandler.clearInv(player);
		player.setTotalExperience(0);
		player.setLevel(0);
		player.setExp(0);
		
		//Effects
		for(int i=0; i<effects.length; i++) {
			String str = effects[i];
			while(str.startsWith(" ")) {
				str = str.substring(1);
			}
			while(str.endsWith(" ")) {
				str = str.substring(0, str.length()-1);
			}
			
			int id = -1;
			int level = 0;
			int duration = 95000;
			String[] strs = str.split(" ");
			if(strs.length>0) id = Integer.parseInt(strs[0]);
			if(strs.length>1) level = Integer.parseInt(strs[1]);
			if(strs.length>2) duration = Integer.parseInt(strs[2]);
			
			if(id!=-1) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.getById(id), duration, level), true);
			}
		}
		//
		
		PlayerInventory inv = player.getInventory();
		
		//items
		for(int i=0; i<items.length; i++) {
			ItemStack it = ItemHandler.decodeItem(items[i]);
			if(it!=null) {
				if(i == 0) {
					ItemMeta im = it.getItemMeta();
						im.setDisplayName(getSpellName());
						ArrayList<String> li = new ArrayList<String>();
						li.add(DvZ.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replaceAll("-0-", ""+getSpellTime()));
						if(!getSpellNeed().equals("")) {
							li.add(DvZ.getLanguage().getString("string_need","You need -0- to use this!").replaceAll("-0-", getSpellNeed()));
						}
						im.setLore(li);
					it.setItemMeta(im);
				}
				inv.addItem(it);
			}
		}
		//
		
		if(!DvZ.getStaticConfig().getString("crystal_storage","0").equals("0")) {
			ItemStack it = new ItemStack(388, 1);
			ItemMeta im = it.getItemMeta();
			im.setDisplayName(DvZ.getLanguage().getString("string_crystal_storage","Crystal Storage"));
			it.setItemMeta(im);
			inv.addItem(it);
		}
		
		DvZ.updateInventory(player);
	}
	
	public void spell(Game game, Player player) {
		if(game.getCountdown(player.getName(), 1)==0) {
			int id = getSpellNeedId();
			int data = getSpellNeedData();
			int count = getSpellNeedCount();
			
			if(ItemHandler.countItems(player, id, data)>=count) {
				ItemHandler.removeItems(player, id, data, count);
				game.setCountdown(player.getName(), 1, getSpellTime());
				
				player.giveExp(getSpellExp());
				
				World w = player.getWorld();
				Location loc = player.getLocation();
				//Random rand = new Random();
				PlayerInventory inv = player.getInventory();
				
				String[] itemstrings = getSpellItems();
				for(String its : itemstrings) {
					ItemStack it = ItemHandler.decodeItem(its);
					
					if(it!=null) {
						if(isSpellInv()) {
							inv.addItem(it);
						} else {
							w.dropItem(loc, it);
						}
					}
				}
				
				DvZ.updateInventory(player);
			} else {
				if(!getSpellFail().equals(""))
					player.sendMessage(getSpellFail());
			}
		} else {
			player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replaceAll("-0-", ""+game.getCountdown(player.getName(), 1)));
		}
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getGameId() {
		return gameId;
	}
	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public int getClassItem() {
		return classItem;
	}
	public void setClassItem(int classItem) {
		this.classItem = classItem;
	}
	public int getClassItemDamage() {
		return classItemDamage;
	}
	public void setClassItemDamage(int classItemDamage) {
		this.classItemDamage = classItemDamage;
	}
	public int getClassChance() {
		return classChance;
	}
	public void setClassChance(int classChance) {
		this.classChance = classChance;
	}
	public String[] getItems() {
		return items;
	}
	public void setItems(String[] items) {
		this.items = items;
	}
	public String[] getEffects() {
		return effects;
	}
	public void setEffects(String[] effects) {
		this.effects = effects;
	}
	public double getDamageBuff() {
		return damageBuff;
	}
	public void setDamageBuff(double damageBuff) {
		this.damageBuff = damageBuff;
	}
	public void addDisabledDamage(String damage) {
		if(disabledDamage==null) disabledDamage = new ArrayList<String>();
		disabledDamage.add(damage);
	}
	public boolean isDamageDisabled(String damage) {
		if(disabledDamage==null) return false;
		return disabledDamage.contains(damage);
	}
	public int getMaxMana() {
		return maxMana;
	}
	public void setMaxMana(int maxMana) {
		this.maxMana = maxMana;
	}
	public int getManaRegen() {
		return manaRegen;
	}
	public void setManaRegen(int manaRegen) {
		this.manaRegen = manaRegen;
	}
	//Spells
	public boolean isSpellEnabled() {
		return spellEnabled;
	}
	public void setSpellEnabled(boolean spellEnabled) {
		this.spellEnabled = spellEnabled;
	}
	public int getSpellTime() {
		return spellTime;
	}
	public void setSpellTime(int spellTime) {
		this.spellTime = spellTime;
	}
	public int getSpellItem() {
		return spellItem;
	}
	public void setSpellItem(int spellItem) {
		this.spellItem = spellItem;
	}
	public String getSpellName() {
		return spellName;
	}
	public void setSpellName(String spellName) {
		this.spellName = spellName;
	}
	public int getSpellNeedId() {
		return spellNeedId;
	}
	public void setSpellNeedId(int spellNeedId) {
		this.spellNeedId = spellNeedId;
	}
	public int getSpellNeedData() {
		return spellNeedData;
	}
	public void setSpellNeedData(int spellNeedData) {
		this.spellNeedData = spellNeedData;
	}
	public int getSpellNeedCount() {
		return spellNeedCount;
	}
	public void setSpellNeedCount(int spellNeedCount) {
		this.spellNeedCount = spellNeedCount;
	}
	public String getSpellNeed() {
		return spellNeed;
	}
	public void setSpellNeed(String spellNeed) {
		this.spellNeed = spellNeed;
	}
	public String getSpellFail() {
		return spellFail;
	}
	public void setSpellFail(String spellFail) {
		this.spellFail = spellFail;
	}
	public boolean isSpellInv() {
		return spellInv;
	}
	public void setSpellInv(boolean spellInv) {
		this.spellInv = spellInv;
	}
	public int getSpellExp() {
		return spellExp;
	}
	public void setSpellExp(int spellExp) {
		this.spellExp = spellExp;
	}
	public String[] getSpellItems() {
		return spellItems;
	}
	public void setSpellItems(String[] spellItems) {
		this.spellItems = spellItems;
	}
	//pistons
	public boolean isPistonEnabled() {
		return pistonEnabled;
	}
	public void setPistonEnabled(boolean pistonEnabled) {
		this.pistonEnabled = pistonEnabled;
	}

	public int getPistonBlockAbove() {
		return pistonBlockAbove;
	}

	public void setPistonBlockAbove(int pistonBlockAbove) {
		this.pistonBlockAbove = pistonBlockAbove;
	}

	public int getPistonBlockBelow() {
		return pistonBlockBelow;
	}

	public void setPistonBlockBelow(int pistonBlockBelow) {
		this.pistonBlockBelow = pistonBlockBelow;
	}

	public LinkedHashMap<Integer, Integer> getPistonChange() {
		return pistonChange;
	}

	public void setPistonChange(LinkedHashMap<Integer, Integer> pistonChange) {
		this.pistonChange = pistonChange;
	}
}
