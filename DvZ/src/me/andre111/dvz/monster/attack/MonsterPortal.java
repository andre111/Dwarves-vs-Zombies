package me.andre111.dvz.monster.attack;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.Spellcontroller;
import me.andre111.dvz.monster.MonsterAttack;

public class MonsterPortal extends MonsterAttack {
	@Override
	public void spellCast(Game game, Player player) {	
		if(game.enderPortal!=null) {
			if(!reinforePortal(game, player)) {
				game.setCountdown(player.getName(), getId(), 0);
				player.sendMessage(DvZ.getLanguage().getString("string_portal_exists","A Portal allready exists!"));
			}
		} else {
			createPortal(game, player);
		}
	}
	
	private void createPortal(Game game, Player player) {
		Location loc = player.getLocation();
		Location nloc = new Location(loc.getWorld(), loc.getBlockX()+0.5, loc.getBlockY()+10, loc.getBlockZ()+0.5);
		World w = loc.getWorld();
		
		Spellcontroller.createPortal(nloc);
		
		w.strikeLightningEffect(nloc);
		for(int i=0; i<10; i++) {
			ItemStack it = new ItemStack(121, 1);
			ItemMeta im = it.getItemMeta();
			im.setDisplayName(DvZ.getLanguage().getString("string_spell_disableportal","Disable Portal"));
			ArrayList<String> li = new ArrayList<String>();
			li.add(DvZ.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replaceAll("-0-", ""+DvZ.getStaticConfig().getInt("spelltime_disableportal",3)));
			im.setLore(li);
			it.setItemMeta(im);
			
			w.dropItem(nloc, it);
		}
		
		player.teleport(nloc);
		//TODO - temporary disabled so the portal can be kept
		//player.getInventory().clear();
		/*ItemStack it = new ItemStack(369, 1);
		ItemMeta im = it.getItemMeta();
		im.setDisplayName(DvZ.getLanguage().getString("string_spell_reinforce_portal","Reinforce Portal"));
		ArrayList<String> li = new ArrayList<String>();
		li.add(DvZ.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replaceAll("-0-", ""+plugin.getConfig().getInt("spelltime_reinforceenderportal",3)));
		im.setLore(li);
		it.setItemMeta(im);
		player.getInventory().addItem(it);*/
		player.getInventory().addItem(new ItemStack(320, 64));
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 95000, -20));
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 95000, 4));
		
		
		game.enderPortal = nloc;
		game.enderActive = true;
		game.enderMan = player.getName();
		
		game.broadcastMessage(DvZ.getLanguage().getString("string_portal_create","An Enderman has created a Portal!"));
	}
	
	private boolean reinforePortal(Game game, Player player) {
		if(player.getName().equals(game.enderMan)) {
			Location nloc = game.enderPortal;
			World w = nloc.getWorld();
			
			w.strikeLightningEffect(nloc);
			for(int i=0; i<10; i++) {
				ItemStack it = new ItemStack(121, 1);
				ItemMeta im = it.getItemMeta();
				im.setDisplayName(DvZ.getLanguage().getString("string_spell_disableportal","Disable Portal"));
				ArrayList<String> li = new ArrayList<String>();
				li.add(DvZ.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replaceAll("-0-", ""+DvZ.getStaticConfig().getInt("spelltime_disableportal",3)));
				im.setLore(li);
				it.setItemMeta(im);
				
				w.dropItem(nloc, it);
			}
			
			player.teleport(nloc);
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 95000, -20));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 95000, 4));
			
			game.enderActive = true;
			
			game.broadcastMessage(DvZ.getLanguage().getString("string_portal_reinforce","The Portal has been reinforced!"));
			
			return true;
		}
		return false;
	}
	
	@Override
	public int getType() {
		return 0;
	}
}
