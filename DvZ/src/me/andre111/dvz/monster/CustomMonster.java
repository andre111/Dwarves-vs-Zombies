package me.andre111.dvz.monster;

import java.util.ArrayList;
import java.util.List;

import me.andre111.dvz.Classswitcher;
import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.utils.ItemHandler;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

public class CustomMonster {
	private int id;
	private String name;
	private String prefix;
	private String suffix;
	private DisguiseType disguise;
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
	
	private CustomMonsterItem item1;
	private CustomMonsterItem item2;
	
	//Cast check if it is the right item and right type and cooldown managment then cast
	//###################################
	public void spellCast(Game game, ItemStack item, Player player) {	
		if(item==null) return;
		
		//normal cast
		if(item1!=null)
		if(item1.getCast()!=null)
		if(item.getTypeId()==ItemHandler.decodeItemId(item1.getItem()) && item1.getCast().getType()==0) {
			if(game.getCountdown(player.getName(), item1.getId())==0) {
				game.setCountdown(player.getName(), item1.getId(), item1.getTime());
				//cast
				item1.getCast().spellCast(game, player);
			} else {
				player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replaceAll("-0-", ""+game.getCountdown(player.getName(), item1.getId())));
			}
		}
		if(item2!=null)
		if(item2.getCast()!=null)
		if(item.getTypeId()==ItemHandler.decodeItemId(item2.getItem()) && item2.getCast().getType()==0) {
			if(game.getCountdown(player.getName(), item2.getId())==0) {
				game.setCountdown(player.getName(), item2.getId(), item2.getTime());
				//cast
				item2.getCast().spellCast(game, player);
			} else {
				player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replaceAll("-0-", ""+game.getCountdown(player.getName(), item2.getId())));
			}
		}
		//far away cast
		spellCastFarTargeted(game, item, player);
	}
	public void spellCast(Game game, ItemStack item, Player player, Block target) {	
		if(item==null) return;
		
		if(item1!=null)
		if(item1.getCast()!=null)
		if(item.getTypeId()==ItemHandler.decodeItemId(item1.getItem()) && (item1.getCast().getType()==1 /*TYPE 0 CAN ALSO BE ALL OTHER TYPES*/ || item1.getCast().getType()==0)) {
			if(game.getCountdown(player.getName(), item1.getId())==0) {
				game.setCountdown(player.getName(), item1.getId(), item1.getTime());
				//cast
				item1.getCast().spellCast(game, player, target);
			} else {
				player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replaceAll("-0-", ""+game.getCountdown(player.getName(), item1.getId())));
			}
		}
		if(item2!=null)
		if(item2.getCast()!=null)
		if(item.getTypeId()==ItemHandler.decodeItemId(item2.getItem()) && (item2.getCast().getType()==1 /*TYPE 0 CAN ALSO BE ALL OTHER TYPES*/ || item2.getCast().getType()==0)) {
			if(game.getCountdown(player.getName(), item2.getId())==0) {
				game.setCountdown(player.getName(), item2.getId(), item2.getTime());
				//cast
				item2.getCast().spellCast(game, player, target);
			} else {
				player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replaceAll("-0-", ""+game.getCountdown(player.getName(), item2.getId())));
			}
		}
	}
	public void spellCast(Game game, ItemStack item, Player player, Player target) {
		if(item==null) return;
		
		if(item1!=null)
		if(item1.getCast()!=null)
		if(item.getTypeId()==ItemHandler.decodeItemId(item1.getItem()) && (item1.getCast().getType()==2 /*TYPE 0 CAN ALSO BE ALL OTHER TYPES*/ || item1.getCast().getType()==0)) {
			if(game.getCountdown(player.getName(), item1.getId())==0) {
				game.setCountdown(player.getName(), item1.getId(), item1.getTime());
				//cast
				item1.getCast().spellCast(game, player, target);
			} else {
				player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replaceAll("-0-", ""+game.getCountdown(player.getName(), item1.getId())));
			}
		}
		if(item2!=null)
		if(item2.getCast()!=null)
		if(item.getTypeId()==ItemHandler.decodeItemId(item2.getItem()) && (item2.getCast().getType()==2 /*TYPE 0 CAN ALSO BE ALL OTHER TYPES*/ || item2.getCast().getType()==0)) {
			if(game.getCountdown(player.getName(), item2.getId())==0) {
				game.setCountdown(player.getName(), item2.getId(), item2.getTime());
				//cast
				item2.getCast().spellCast(game, player, target);
			} else {
				player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replaceAll("-0-", ""+game.getCountdown(player.getName(), item2.getId())));
			}
		}
	}
	//"Targeted far away attack"
	private void spellCastFarTargeted(Game game, ItemStack item, Player player) {	
		MonsterAttack att = null;
		int id = -1;
		//default cooldown stuff
		if(item.getTypeId()==ItemHandler.decodeItemId(item1.getItem()) && item1.getCast().getType()==3) {
			if(game.getCountdown(player.getName(), item1.getId())==0) {
				game.setCountdown(player.getName(), item1.getId(), item1.getTime());
				
				att = item1.getCast();
				id = item1.getId();
			} else {
				player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replaceAll("-0-", ""+game.getCountdown(player.getName(), item1.getId())));
			}
		} else if(item.getTypeId()==ItemHandler.decodeItemId(item2.getItem()) && item2.getCast().getType()==3) {
			if(game.getCountdown(player.getName(), item2.getId())==0) {
				game.setCountdown(player.getName(), item2.getId(), item2.getTime());
				
				att = item2.getCast();
				id = item2.getId();
			} else {
				player.sendMessage(DvZ.getLanguage().getString("string_wait","You have to wait -0- Seconds!").replaceAll("-0-", ""+game.getCountdown(player.getName(), item2.getId())));
			}
		}
		
		if(att!=null) {
			//get block
			int distance = att.getRange();
			List<Block> blocks = player.getLineOfSight(DvZ.transparent, distance);
			Block furthest = blocks.get(0);
			Location ploc = player.getLocation();
			double maxDistance = 0;
			for(int i=0; i<blocks.size(); i++) {
				double dist = blocks.get(i).getLocation().distanceSquared(ploc);
				if (dist>maxDistance) {
					maxDistance = dist;
					furthest = blocks.get(i);
				}
			}
			//nothing reached
			if (DvZ.isPathable(furthest.getType()))
			{
				game.setCountdown(player.getName(), id, 0);
			} else {
				att.spellCastFarTargeted(game, player, furthest);
			}
		}
	}
	//###################################
	
	//become custom Monster
	public void becomeMonster(Game game, Player player) {
		Classswitcher.becomeMonster(game, player, true);
		game.setPlayerState(player.getName(), id+Game.monsterMin);
		game.getManaManager().setMaxMana(player.getName(), getMaxMana(), true);
		game.getManaManager().setManaRegen(player.getName(), getManaRegen());
		
		player.sendMessage(DvZ.getLanguage().getString("string_have_become","You have become a -0-!").replaceAll("-0-", getName()));
		DvZ.disguiseP(player, new Disguise(DvZ.api.newEntityID(), "", getDisguise()));
		
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
		//spellitems
		ItemStack spell1 = ItemHandler.decodeItem(getItem1().getItem());
		if(spell1!=null && getItem1().getAtSpawn()) {
			ItemMeta spell1meta = spell1.getItemMeta();
			spell1meta.setDisplayName(getItem1().getName());
			ArrayList<String> li = new ArrayList<String>();
			li.add(DvZ.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replaceAll("-0-", ""+getItem1().getTime()));
			spell1meta.setLore(li);
			spell1.setItemMeta(spell1meta);
			inv.addItem(spell1);
		}
		ItemStack spell2 = ItemHandler.decodeItem(getItem2().getItem());
		if(spell2!=null && getItem2().getAtSpawn()) {
			ItemMeta spell2meta = spell2.getItemMeta();
			spell2meta.setDisplayName(getItem2().getName());
			ArrayList<String> li = new ArrayList<String>();
			li.add(DvZ.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replaceAll("-0-", ""+getItem2().getTime()));
			spell2meta.setLore(li);
			spell2.setItemMeta(spell2meta);
			inv.addItem(spell2);
		}
		//
		
		//items
		for(int i=0; i<items.length; i++) {
			ItemStack it = ItemHandler.decodeItem(items[i]);
			if(it!=null) {
				inv.addItem(it);
			}
		}
		//
		
		DvZ.updateInventory(player);
	}
	
	//ALL THEM GETTERS AND SETTERS
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public DisguiseType getDisguise() {
		return disguise;
	}
	public void setDisguise(DisguiseType disguise) {
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
	public CustomMonsterItem getItem1() {
		return item1;
	}
	public void setItem1(CustomMonsterItem item1) {
		this.item1 = item1;
	}
	public CustomMonsterItem getItem2() {
		return item2;
	}
	public void setItem2(CustomMonsterItem item2) {
		this.item2 = item2;
	}
	
}
