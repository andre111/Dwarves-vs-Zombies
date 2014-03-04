package me.andre111.dvz.dwarf;

import java.util.ArrayList;
import java.util.List;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.manager.StatManager;
import me.andre111.dvz.utils.InventoryHandler;
import me.andre111.dvz.utils.PlayerHandler;
import me.andre111.items.ItemHandler;
import me.andre111.items.ManaManager;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
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
	private String[] crystalItems;
	private String[] effects;
	private double damageBuff;
	private ArrayList<String> disabledDamage;
	private int maxMana;
	private int manaRegen;
	private double startHealth;
	private int startHunger;
	private float startSat;
	private String startMessage;
	
	//piston
	private boolean pistonEnabled;
	private List<String> pistonChange;
	//rightclick
	private ArrayList<String> transmuteItems;
	private ArrayList<String> transmuteBreakItems;
	
	private boolean rewardOnBlockPlace;
	
	//become custom Dwarf
	public void becomeDwarf(Game game, final Player player) {
		game.setPlayerState(player.getName(), id+Game.dwarfMin);
		game.resetCountdowns(player.getName());
		ManaManager.setMaxMana(player.getName(), getMaxMana(), true);
		ManaManager.setManaRegen(player.getName(), getManaRegen());
		
		DvZ.sendPlayerMessageFormated(player, ConfigManager.getLanguage().getString("string_have_become","You have become a -0-!").replace("-0-", getName()));
		
		if(!startMessage.equals(""))
			DvZ.sendPlayerMessageFormated(player, startMessage);
		
		InventoryHandler.clearInv(player, false);
		PlayerHandler.resetPotionEffects(player);
		player.setTotalExperience(0);
		player.setLevel(0);
		player.setExp(0);
		
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
		
		
		//crystalchest items
		Inventory cinv = game.getCrystalChest(player.getName(), false);

		for(int i=0; i<crystalItems.length; i++) {
			ItemStack cit = ItemHandler.decodeItem(crystalItems[i], player);
			if(cit!=null) {
				cinv.addItem(cit);
			}
		}

		DvZ.updateInventory(player);
		
		//update stats
		Bukkit.getScheduler().runTaskLater(DvZ.instance, new Runnable() {
			public void run() {
				StatManager.show(player);
				StatManager.hide(player, false);
			}
		}, 2);
	}
	
	public boolean transmuteItemOnBlock(Game game, Player player, ItemStack item, Block block) {
		for(String st : getTransmuteItems()) {
			String[] split = st.split(";");
			
			//player block check
			if(player.getLocation().clone().subtract(0, 1, 0).getBlock().getTypeId()!=Integer.parseInt(split[0])) {
				continue;
			}
			if(player.getLocation().clone().subtract(0, -2, 0).getBlock().getTypeId()!=Integer.parseInt(split[1])) {
				continue;
			}
			
			//is it the right item?
			String[] itemSt = split[2].split(":");
			if(Integer.parseInt(itemSt[0])==item.getTypeId() && Integer.parseInt(itemSt[1])==item.getDurability()) {
				//right block clicked?
				String[] bSt = split[3].split(":");
				if(Integer.parseInt(bSt[0])==block.getTypeId() && (Integer.parseInt(bSt[1])==block.getData() || Integer.parseInt(bSt[1])==-1)) {
					//sound
					String[] sound = split[4].split(":");
					String sId = "-1";
					float volume = 1;
					float pitch = 1;

					sId = sound[0];
					if(sound.length>1) volume = Float.parseFloat(sound[1]);
					if(sound.length>2) pitch = Float.parseFloat(sound[2]);

					if(!sId.equals("-1")) {
						player.getWorld().playSound(player.getLocation(), Sound.valueOf(sId), volume, pitch);
					}

					//drop item
					ItemStack it = ItemHandler.decodeItem(split[5], player);
					if(it!=null) {
						player.getWorld().dropItemNaturally(player.getLocation(), it);
					}

					//substract item
					if(item.getAmount()-1<=0)
						item.setTypeId(0);
					else
						item.setAmount(item.getAmount()-1);

					player.setItemInHand(item);
					
					//update the invventory, because it is glitchy
					DvZ.updateInventory(player);

					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean transmuteItemOnBreak(Game game, Player player, Block block) {
		for(String st : getTransmuteBreakItems()) {
			String[] split = st.split(";");
			
			//player block check
			if(player.getLocation().clone().subtract(0, 1, 0).getBlock().getTypeId()!=Integer.parseInt(split[0])) {
				continue;
			}
			if(player.getLocation().clone().subtract(0, -2, 0).getBlock().getTypeId()!=Integer.parseInt(split[1])) {
				continue;
			}
			
			String[] itemSt = split[2].split(":");
			PlayerInventory inv = player.getInventory();
			//right block clicked?
			String[] bSt = split[3].split(":");
			if(Integer.parseInt(bSt[0])==block.getTypeId() && (Integer.parseInt(bSt[1])==block.getData() || Integer.parseInt(bSt[1])==-1))  {
				//whole inventory
				for(int i=0; i<inv.getSize(); i++) {
					ItemStack item  = inv.getItem(i);
					if(item!=null)
					if(Integer.parseInt(itemSt[0])==item.getTypeId() && Integer.parseInt(itemSt[1])==item.getDurability()) {
						//sound
						String[] sound = split[4].split(":");
						String sId = "-1";
						float volume = 1;
						float pitch = 1;
						
						sId = sound[0];
						if(sound.length>1) volume = Float.parseFloat(sound[1]);
						if(sound.length>2) pitch = Float.parseFloat(sound[2]);
						
						if(!sId.equals("-1")) {
							player.getWorld().playSound(player.getLocation(), Sound.valueOf(sId), volume, pitch);
						}
						
						//drop item
						ItemStack it = ItemHandler.decodeItem(split[5], player);
						if(it!=null) {
							player.getWorld().dropItemNaturally(player.getLocation(), it);
						}
						
						//substract item
						if(item.getAmount()-1<=0)
							item.setTypeId(0);
						else
							item.setAmount(item.getAmount()-1);
						inv.setItem(i, item);
						
						//update the invventory, because it is glitchy
						DvZ.updateInventory(player);
						
						//break for loop
						i = 10000;
					}
				}
			}
		}
		
		return false;
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
	public String[] getCrystalItems() {
		return crystalItems;
	}
	public void setCrystalItems(String[] crystalItems) {
		this.crystalItems = crystalItems;
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

	//pistons
	public boolean isPistonEnabled() {
		return pistonEnabled;
	}
	public void setPistonEnabled(boolean pistonEnabled) {
		this.pistonEnabled = pistonEnabled;
	}
	public List<String> getPistonChange() {
		return pistonChange;
	}
	public void setPistonChange(List<String> pistonChange) {
		this.pistonChange = pistonChange;
	}
	//items
	public ArrayList<String> getTransmuteItems() {
		return transmuteItems;
	}
	public void setTransmuteItems(ArrayList<String> transmuteItems) {
		this.transmuteItems = transmuteItems;
	}
	public ArrayList<String> getTransmuteBreakItems() {
		return transmuteBreakItems;
	}
	public void setTransmuteBreakItems(ArrayList<String> transmuteBreakItems) {
		this.transmuteBreakItems = transmuteBreakItems;
	}
	public boolean isRewardOnBlockPlace() {
		return rewardOnBlockPlace;
	}
	public void setRewardOnBlockPlace(boolean rewardOnBlockPlace) {
		this.rewardOnBlockPlace = rewardOnBlockPlace;
	}
}
