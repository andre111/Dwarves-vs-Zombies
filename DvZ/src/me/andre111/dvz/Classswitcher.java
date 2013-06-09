package me.andre111.dvz;

import java.util.ArrayList;
import java.util.List;

import me.andre111.dvz.utils.ItemHandler;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

public class Classswitcher {
	public static DvZ plugin;
	
	//Custom Dwarves
	public static void becomeCustomDwarf(Game game, Player player, int cd) {
		game.setPlayerState(player.getName(), 9+cd);
		game.resetCountdowns(player.getName());
		game.getManaManager().setMaxMana(player.getName(), DvZ.getClassFile().getInt("custom_d"+cd+"_mana_max", 0), true);
		game.getManaManager().setManaRegen(player.getName(), DvZ.getClassFile().getInt("custom_d"+cd+"_mana_regen", 0));
		player.sendMessage(DvZ.getLanguage().getString("string_have_become","You have become a -0-!").replace("-0-", DvZ.getClassFile().getString("custom_d"+cd+"_name","")));
		
		ItemHandler.clearInv(player);
		player.setTotalExperience(0);
		player.setLevel(0);
		player.setExp(0);
		
		PlayerInventory inv = player.getInventory();
		
		List<String> itemstrings = DvZ.getClassFile().getStringList("custom_d"+cd+"_items");
		for(int i=0; i<itemstrings.size(); i++) {
			ItemStack it = ItemHandler.decodeItem(itemstrings.get(i));
			
			if(it!=null) {
				if(i==0) {
					ItemMeta im = it.getItemMeta();
					im.setDisplayName(DvZ.getClassFile().getString("custom_d"+cd+"_spell_name",""));
					ArrayList<String> li = new ArrayList<String>();
					li.add(DvZ.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replace("-0-", ""+DvZ.getClassFile().getInt("custom_d"+cd+"_spell_time",30)));
					if(DvZ.getClassFile().getString("custom_d"+cd+"_spell_hasstring","false")=="true") {
						li.add(DvZ.getLanguage().getString("string_need","You need -0- to use this!").replace("-0-", ""+DvZ.getClassFile().getString("custom_d"+cd+"_spell_string","")));
					}
					im.setLore(li);
					it.setItemMeta(im);
				}
				inv.addItem(it);
			}
		}
		
		if(!plugin.getConfig().getString("crystal_storage","0").equals("0")) {
			ItemStack it = new ItemStack(388, 1);
			ItemMeta im = it.getItemMeta();
			im.setDisplayName(DvZ.getLanguage().getString("string_crystal_storage","Crystal Storage"));
			it.setItemMeta(im);
			inv.addItem(it);
		}
		
		DvZ.updateInventory(player);
	}
	
	//###################################
	//MONSTER
	//###################################
	public static void becomeMonster(final Game game, final Player player) {
		becomeMonster(game, player, true);
	}
	public static void becomeMonster(final Game game, final Player player, boolean buff) {
		if (buff) game.addMonsterBuff(player);
		game.resetCountdowns(player.getName());
		
		ItemHandler.clearInv(player);
		player.setTotalExperience(0);
		player.setLevel(0);
		player.setExp(0);
		
		if(!game.released) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(game.getPlugin(), new Runnable() {
				public void run() {
					game.waitm.open(player);
				}
			}, 2);
		}
	}
	
	public static void becomeZombie(Game game, Player player) {
		becomeMonster(game, player);
		game.setPlayerState(player.getName(), 30);
		
		player.sendMessage(DvZ.getLanguage().getString("string_have_become","You have become a -0-!").replace("-0-", DvZ.getLanguage().getString("string_zombie","Zombie")));
		DvZ.disguiseP(player, new Disguise(DvZ.api.newEntityID(), "", DisguiseType.Zombie));
		
		PlayerInventory inv = player.getInventory();
		
		ItemStack it = new ItemStack(267, 1);
		ItemMeta im = it.getItemMeta();
		im.addEnchant(Enchantment.DAMAGE_ALL, 2, true);
		it.setItemMeta(im);
		inv.addItem(it);
		inv.addItem(new ItemStack(373, 2, (short)16421));
		inv.addItem(new ItemStack(320, 64));
		inv.addItem(new ItemStack(306, 1));
		ItemStack ic = new ItemStack(307, 1);
		ItemMeta icm = ic.getItemMeta();
		icm.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 3, true);
		ic.setItemMeta(icm);
		inv.addItem(ic);
		inv.addItem(new ItemStack(308, 1));
		inv.addItem(new ItemStack(309, 1));
		
		DvZ.updateInventory(player);
	}
	
	public static void becomeSkeleton(Game game, Player player) {
		becomeMonster(game, player);
		game.setPlayerState(player.getName(), 31);

		player.sendMessage(DvZ.getLanguage().getString("string_have_become","You have become a -0-!").replace("-0-", DvZ.getLanguage().getString("string_skeleton","Skeleton")));
		DvZ.disguiseP(player, new Disguise(DvZ.api.newEntityID(), "", DisguiseType.Skeleton));
		
		
		PlayerInventory inv = player.getInventory();
		
		ItemStack it = new ItemStack(261, 1);
		ItemMeta im = it.getItemMeta();
		im.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
		im.addEnchant(Enchantment.ARROW_FIRE, 1, true);
		im.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
		im.addEnchant(Enchantment.ARROW_KNOCKBACK, 1, true);
		it.setItemMeta(im);
		inv.addItem(it);
		inv.addItem(new ItemStack(262, 64));
		inv.addItem(new ItemStack(320, 64));
		inv.addItem(new ItemStack(298, 1));
		ItemStack ic = new ItemStack(299, 1);
		ItemMeta icm = ic.getItemMeta();
		icm.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 3, true);
		ic.setItemMeta(icm);
		inv.addItem(ic);
		inv.addItem(new ItemStack(300, 1));
		inv.addItem(new ItemStack(301, 1));
		
		DvZ.updateInventory(player);
	}
	
	public static void becomeCreeper(Game game, Player player) {
		becomeMonster(game, player);
		game.setPlayerState(player.getName(), 32);

		player.sendMessage(DvZ.getLanguage().getString("string_have_become","You have become a -0-!").replace("-0-", DvZ.getLanguage().getString("string_creeper","Creeper")));
		DvZ.disguiseP(player, new Disguise(DvZ.api.newEntityID(), "", DisguiseType.Creeper));
		
		
		PlayerInventory inv = player.getInventory();
		
		ItemStack it = new ItemStack(289, 1);
		ItemMeta im = it.getItemMeta();
		im.setDisplayName(DvZ.getLanguage().getString("string_spell_explode","Explode"));
		ArrayList<String> li = new ArrayList<String>();
		li.add(DvZ.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replace("-0-", ""+plugin.getConfig().getInt("spelltime_creeper",10)));
		im.setLore(li);
		it.setItemMeta(im);
		inv.addItem(it);
		inv.addItem(new ItemStack(320, 64));
		inv.addItem(new ItemStack(298, 1));
		ItemStack ic = new ItemStack(299, 1);
		ItemMeta icm = ic.getItemMeta();
		icm.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 3, true);
		ic.setItemMeta(icm);
		inv.addItem(ic);
		inv.addItem(new ItemStack(300, 1));
		inv.addItem(new ItemStack(301, 1));
		
		DvZ.updateInventory(player);
	}
	
	public static void becomeWolf(Game game, Player player) {
		becomeMonster(game, player);
		game.setPlayerState(player.getName(), 33);
		
		player.sendMessage(DvZ.getLanguage().getString("string_have_become","You have become a -0-!").replace("-0-", DvZ.getLanguage().getString("string_wolf","Wolf")));
		DvZ.disguiseP(player, new Disguise(DvZ.api.newEntityID(), "", DisguiseType.Wolf));
		
		
		PlayerInventory inv = player.getInventory();
		
		ItemStack it = new ItemStack(276, 1);
		ItemMeta im = it.getItemMeta();
		im.addEnchant(Enchantment.DAMAGE_ALL, 2, true);
		it.setItemMeta(im);
		inv.addItem(it);
		it = new ItemStack(283, 1);
		im = it.getItemMeta();
		im.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
		im.addEnchant(Enchantment.KNOCKBACK, 1, true);
		it.setItemMeta(im);
		inv.addItem(it);
		inv.addItem(new ItemStack(320, 64));
		inv.addItem(new ItemStack(383, 5, (short) 95));
		inv.addItem(new ItemStack(352, 64));
		inv.addItem(new ItemStack(302, 1));
		ItemStack ic = new ItemStack(303, 1);
		ItemMeta icm = ic.getItemMeta();
		icm.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 3, true);
		ic.setItemMeta(icm);
		inv.addItem(ic);
		inv.addItem(new ItemStack(304, 1));
		inv.addItem(new ItemStack(305, 1));
		
		DvZ.updateInventory(player);
	}
	
	public static void becomeSpider(Game game, Player player) {
		becomeMonster(game, player);
		game.setPlayerState(player.getName(), 34);
		
		player.sendMessage(DvZ.getLanguage().getString("string_have_become","You have become a -0-!").replace("-0-", DvZ.getLanguage().getString("string_spider","Spider")));
		DvZ.disguiseP(player, new Disguise(DvZ.api.newEntityID(), "", DisguiseType.Spider));
		
		
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 95000, 4));
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 95000, 3));
		
		PlayerInventory inv = player.getInventory();
		
		ItemStack it = new ItemStack(375, 1);
		ItemMeta im = it.getItemMeta();
		im.setDisplayName(DvZ.getLanguage().getString("string_spell_confuse","Confuse"));
		ArrayList<String> li = new ArrayList<String>();
		li.add(DvZ.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replace("-0-", ""+plugin.getConfig().getInt("spelltime_spiderbite1",0)));
		im.setLore(li);
		it.setItemMeta(im);
		inv.addItem(it);
		inv.addItem(new ItemStack(373, 3, (short)16452));
		inv.addItem(new ItemStack(106, 64));
		inv.addItem(new ItemStack(320, 64));
		inv.addItem(new ItemStack(306, 1));
		ItemStack ic = new ItemStack(307, 1);
		ItemMeta icm = ic.getItemMeta();
		icm.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 3, true);
		ic.setItemMeta(icm);
		inv.addItem(ic);
		inv.addItem(new ItemStack(308, 1));
		it = new ItemStack(309, 1);
		im = it.getItemMeta();
		im.addEnchant(Enchantment.PROTECTION_FALL, 2, true);
		it.setItemMeta(im);
		inv.addItem(it);
		
		DvZ.updateInventory(player);
	}
	
	public static void becomeIronGolem(Game game, Player player) {
		becomeMonster(game, player, false);
		game.setPlayerState(player.getName(), 35);

		player.sendMessage(DvZ.getLanguage().getString("string_have_become","You have become a -0-!").replace("-0-", DvZ.getLanguage().getString("string_iron","IronGolem")));
		if(plugin.getConfig().getString("change_golem_to_blaze","false")=="true")
			DvZ.disguiseP(player, new Disguise(DvZ.api.newEntityID(), "", DisguiseType.Blaze));
		else
			DvZ.disguiseP(player, new Disguise(DvZ.api.newEntityID(), "", DisguiseType.IronGolem));
		
		
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 95000, 4));
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 95000, 10));
		player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 95000, 1));
		player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 95000, 4));
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 95000, -20));
		
		PlayerInventory inv = player.getInventory();
		
		ItemStack it = new ItemStack(265, 1);
		ItemMeta im = it.getItemMeta();
		im.setDisplayName(DvZ.getLanguage().getString("string_spell_smash","Smash Blocks"));
		ArrayList<String> li = new ArrayList<String>();
		li.add(DvZ.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replace("-0-", ""+plugin.getConfig().getInt("spelltime_ironsmash",1)));
		im.setLore(li);
		it.setItemMeta(im);
		inv.addItem(it);
		it = new ItemStack(341, 1);
		im = it.getItemMeta();
		im.setDisplayName(DvZ.getLanguage().getString("string_spell_leap","Leap"));
		ArrayList<String> li2 = new ArrayList<String>();
		li2.add(DvZ.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replace("-0-", ""+plugin.getConfig().getInt("spelltime_ironjump",30)));
		im.setLore(li2);
		it.setItemMeta(im);
		inv.addItem(it);
		inv.addItem(new ItemStack(320, 64));
		it = new ItemStack(298, 1);
		im = it.getItemMeta();
		im.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true);
		it.setItemMeta(im);
		inv.addItem(it);
		it = new ItemStack(299, 1);
		im = it.getItemMeta();
		im.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true);
		im.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 3, true);
		it.setItemMeta(im);
		inv.addItem(it);
		it = new ItemStack(300, 1);
		im = it.getItemMeta();
		im.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true);
		it.setItemMeta(im);
		inv.addItem(it);
		it = new ItemStack(301, 1);
		im = it.getItemMeta();
		im.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true);
		it.setItemMeta(im);
		inv.addItem(it);
		
		DvZ.updateInventory(player);
	}
	
	public static void becomeSnowGolem(Game game, Player player) {
		becomeMonster(game, player);
		game.setPlayerState(player.getName(), 36);

		player.sendMessage(DvZ.getLanguage().getString("string_have_become","You have become a -0-!").replace("-0-", DvZ.getLanguage().getString("string_snow","SnowGolem")));
		DvZ.disguiseP(player, new Disguise(DvZ.api.newEntityID(), "", DisguiseType.Snowman));
		
		
		PlayerInventory inv = player.getInventory();
		
		ItemStack it = new ItemStack(353, 1);
		ItemMeta im = it.getItemMeta();
		im.setDisplayName(DvZ.getLanguage().getString("string_spell_snow","Get Snow"));
		ArrayList<String> li = new ArrayList<String>();
		li.add(DvZ.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replace("-0-", ""+plugin.getConfig().getInt("spelltime_snowgolem",60)));
		im.setLore(li);
		it.setItemMeta(im);
		inv.addItem(it);
		inv.addItem(new ItemStack(320, 64));
		
		DvZ.updateInventory(player);
	}
	
	public static void becomeBroodmother(Game game, Player player) {
		becomeMonster(game, player);
		game.setPlayerState(player.getName(), 37);

		player.sendMessage(DvZ.getLanguage().getString("string_have_become","You have become a -0-!").replace("-0-", DvZ.getLanguage().getString("string_brood","Broodmother")));
		DvZ.disguiseP(player, new Disguise(DvZ.api.newEntityID(), "", DisguiseType.Silverfish));
		
		
		PlayerInventory inv = player.getInventory();
		
		ItemStack it = new ItemStack(350, 1);
		ItemMeta im = it.getItemMeta();
		im.setDisplayName(DvZ.getLanguage().getString("string_spell_roar","Roar"));
		ArrayList<String> li = new ArrayList<String>();
		li.add(DvZ.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replace("-0-", ""+plugin.getConfig().getInt("spelltime_broodroar",3)));
		im.setLore(li);
		it.setItemMeta(im);
		inv.addItem(it);
		it = new ItemStack(349, 1);
		im = it.getItemMeta();
		im.setDisplayName(DvZ.getLanguage().getString("string_spell_lay","Lay Eggs"));
		ArrayList<String> li2 = new ArrayList<String>();
		li2.add(DvZ.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replace("-0-", ""+plugin.getConfig().getInt("spelltime_broodlay",3)));
		li.add(DvZ.getLanguage().getString("string_need","You need -0- to use this!").replace("-0-", ""+DvZ.getLanguage().getString("string_egg","an Egg")));
		im.setLore(li2);
		it.setItemMeta(im);
		inv.addItem(it);
		inv.addItem(new ItemStack(256, 1));
		inv.addItem(new ItemStack(320, 64));
		inv.addItem(new ItemStack(383, 5, (short)60));
		inv.addItem(new ItemStack(383, 20, (short)0));
		
		DvZ.updateInventory(player);
	}
	
	public static void becomeEnderman(Game game, Player player) {
		becomeMonster(game, player);
		game.setPlayerState(player.getName(), 38);

		player.sendMessage(DvZ.getLanguage().getString("string_have_become","You have become a -0-!").replace("-0-", DvZ.getLanguage().getString("string_enderman","Enderman")));
		DvZ.disguiseP(player, new Disguise(DvZ.api.newEntityID(), "", DisguiseType.Enderman));
		
		
		PlayerInventory inv = player.getInventory();
		
		ItemStack it = new ItemStack(378, 1);
		ItemMeta im = it.getItemMeta();
		im.setDisplayName(DvZ.getLanguage().getString("string_spell_blink","Blink"));
		ArrayList<String> li = new ArrayList<String>();
		li.add(DvZ.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replace("-0-", ""+plugin.getConfig().getInt("spelltime_enderblink",18)));
		im.setLore(li);
		it.setItemMeta(im);
		inv.addItem(it);
		it = new ItemStack(90, 1);
		im = it.getItemMeta();
		im.setDisplayName(DvZ.getLanguage().getString("string_spell_portal","Create Portal"));
		ArrayList<String> li2 = new ArrayList<String>();
		li2.add(DvZ.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replace("-0-", ""+plugin.getConfig().getInt("spelltime_enderportal",10)));
		im.setLore(li2);
		it.setItemMeta(im);
		inv.addItem(it);
		inv.addItem(new ItemStack(320, 64));
		
		DvZ.updateInventory(player);
	}
	
	public static void becomeCat(Game game, Player player) {
		becomeMonster(game, player);
		game.setPlayerState(player.getName(), 39);

		player.sendMessage(DvZ.getLanguage().getString("string_have_become","You have become a -0-!").replace("-0-", DvZ.getLanguage().getString("string_cat","Cat")));
		DvZ.disguiseP(player, new Disguise(DvZ.api.newEntityID(), "", DisguiseType.Ocelot));
		
		
		PlayerInventory inv = player.getInventory();
		
		ItemStack it = new ItemStack(351, 1);
		ItemMeta im = it.getItemMeta();
		im.setDisplayName(DvZ.getLanguage().getString("string_drain_hunger","Drain Hunger"));
		ArrayList<String> li = new ArrayList<String>();
		li.add(DvZ.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replace("-0-", ""+plugin.getConfig().getInt("spelltime_drain_hunger",2)));
		im.setLore(li);
		it.setItemMeta(im);
		inv.addItem(it);
		it = new ItemStack(372, 1);
		im = it.getItemMeta();
		im.setDisplayName(DvZ.getLanguage().getString("string_steal_weapon","Steal Weapon"));
		ArrayList<String> li2 = new ArrayList<String>();
		li2.add(DvZ.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replace("-0-", ""+plugin.getConfig().getInt("spelltime_steal_weapon",2)));
		im.setLore(li2);
		it.setItemMeta(im);
		inv.addItem(it);
		inv.addItem(new ItemStack(106, 24));
		inv.addItem(new ItemStack(320, 64));
		
		DvZ.updateInventory(player);
	}
	
	public static void becomeHungryPig(Game game, Player player) {
		becomeMonster(game, player);
		game.setPlayerState(player.getName(), 40);
		
		player.sendMessage(DvZ.getLanguage().getString("string_have_become","You have become a -0-!").replace("-0-", DvZ.getLanguage().getString("string_hungrypig","Hungry Pig")));
		DvZ.disguiseP(player, new Disguise(DvZ.api.newEntityID(), "", DisguiseType.Pig));
		
		
		player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 95000, 3));
		
		PlayerInventory inv = player.getInventory();
		
		inv.addItem(new ItemStack(320, 64));
		inv.addItem(new ItemStack(298, 1));
		ItemStack ic = new ItemStack(299, 1);
		ItemMeta icm = ic.getItemMeta();
		icm.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 3, true);
		ic.setItemMeta(icm);
		inv.addItem(ic);
		inv.addItem(new ItemStack(300, 1));
		inv.addItem(new ItemStack(301, 1));
		
		DvZ.updateInventory(player);
	}
	
	public static void becomeHungryPig2(Game game, Player player) {
		game.setPlayerState(player.getName(), 41);
		game.resetCountdowns(player.getName());
		player.sendMessage(DvZ.getLanguage().getString("string_have_become","You have become a -0-!").replace("-0-", DvZ.getLanguage().getString("string_hungrypig","Hungry Pig")));
		DvZ.api.undisguisePlayer(player);
		DvZ.api.disguisePlayer(player, new Disguise(DvZ.api.newEntityID(), "", DisguiseType.Pig));
		
		player.removePotionEffect(PotionEffectType.FAST_DIGGING);
		player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 95000, 5));
		
		PlayerInventory inv = player.getInventory();
		
		ItemStack it = new ItemStack(272, 1);
		ItemMeta im = it.getItemMeta();
		im.addEnchant(Enchantment.DAMAGE_ALL, 3, true);
		it.setItemMeta(im);
		inv.addItem(it);
		
		DvZ.updateInventory(player);
	}
	
	public static void becomeHungryPig3(Game game, Player player) {
		game.setPlayerState(player.getName(), 42);
		game.resetCountdowns(player.getName());
		player.sendMessage(DvZ.getLanguage().getString("string_have_become","You have become a -0-!").replace("-0-", DvZ.getLanguage().getString("string_hungrypig","Hungry Pig")));
		DvZ.api.undisguisePlayer(player);
		DvZ.api.disguisePlayer(player, new Disguise(DvZ.api.newEntityID(), "", DisguiseType.PigZombie));
		
		player.removePotionEffect(PotionEffectType.FAST_DIGGING);
		
		PlayerInventory inv = player.getInventory();
		
		ItemStack it = new ItemStack(276, 1);
		ItemMeta im = it.getItemMeta();
		im.addEnchant(Enchantment.DAMAGE_ALL, 2, true);
		it.setItemMeta(im);
		inv.addItem(it);
		inv.addItem(new ItemStack(306, 1));
		ItemStack ic = new ItemStack(307, 1);
		ItemMeta icm = ic.getItemMeta();
		icm.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 3, true);
		ic.setItemMeta(icm);
		inv.addItem(ic);
		inv.addItem(new ItemStack(308, 1));
		inv.addItem(new ItemStack(309, 1));
		
		DvZ.updateInventory(player);
	}
}
