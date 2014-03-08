package me.andre111.dvz.monster;

import java.util.ArrayList;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.disguise.DisguiseSystemHandler;
import me.andre111.dvz.manager.StatManager;
import me.andre111.dvz.utils.InventoryHandler;
import me.andre111.dvz.utils.PlayerHandler;
import me.andre111.items.ItemHandler;
import me.andre111.items.ManaManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CustomMonster {
	private int id;
	private int gameId;
	private String name;
	private String prefix;
	private String suffix;
	private String disguise;
	private int classItem;
	private int classItemDamage;
	private int classChance;
	private String[] items;
	private String[] effects;
	private boolean placeBlocks;
	private double damageBuff;
	private ArrayList<String> disabledDamage;
	private int maxMana;
	private int manaRegen;
	private double startHealth;
	private int startHunger;
	private float startSat;
	private String startMessage;
	
	//become custom Monster
	public void becomeMonster(Game game, final Player player) {
		PlayerHandler.resetPotionEffects(player);
		
		//old code from classswitcher
		//------------------------------
		game.resetCountdowns(player.getName());
		game.addMonsterBuff(player);
		
		InventoryHandler.clearInv(player, false);
		player.setTotalExperience(0);
		player.setLevel(0);
		player.setExp(0);
		
		if(!game.released) {
			/*Bukkit.getScheduler().scheduleSyncDelayedTask(game.getPlugin(), new Runnable() {
				public void run() {
					//TODO - diabled until I can fix it
					game.waitm.open(player);
				}
			}, 2);*/
		}
		//------------------------------
		game.setPlayerState(player.getName(), id+Game.monsterMin);
		ManaManager.setMaxMana(player.getUniqueId(), getMaxMana(), true);
		ManaManager.setManaRegen(player.getUniqueId(), getManaRegen());
		
		DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_have_become","You have become a -0-!").replace("-0-", getName()));
		DisguiseSystemHandler.disguiseP(player, getDisguise());
		
		if(!startMessage.equals(""))
			DvZ.sendPlayerMessageFormated(player, startMessage);
		
		player.setHealth(startHealth);
		player.setFoodLevel(startHunger);
		player.setSaturation(startSat);
		
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
			ItemStack it = ItemHandler.decodeItem(items[i], player);
			if(it!=null) {
				inv.addItem(it);
			}
		}
		//
		
		DvZ.updateInventory(player);
		
		//update stats
		Bukkit.getScheduler().runTaskLater(DvZ.instance, new Runnable() {
			public void run() {
				StatManager.show(player);
				StatManager.hide(player, false);
			}
		}, 2);
	}
	
	//ALL THEM GETTERS AND SETTERS
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
	public String getDisguise() {
		return disguise;
	}
	public void setDisguise(String disguise) {
		this.disguise = disguise;
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
	public boolean isPlaceBlocks() {
		return placeBlocks;
	}
	public void setPlaceBlocks(boolean placeBlocks) {
		this.placeBlocks = placeBlocks;
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
	public double getStartHealth() {
		return startHealth;
	}
	public void setStartHealth(double startHealth) {
		this.startHealth = startHealth;
	}
	public int getStartHunger() {
		return startHunger;
	}
	public void setStartHunger(int startHunger) {
		this.startHunger = startHunger;
	}
	public float getStartSat() {
		return startSat;
	}
	public void setStartSat(float startSat) {
		this.startSat = startSat;
	}

	public String getStartMessage() {
		return startMessage;
	}

	public void setStartMessage(String startMessage) {
		this.startMessage = startMessage;
	}
}
