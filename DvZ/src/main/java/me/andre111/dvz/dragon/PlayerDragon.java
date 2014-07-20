package me.andre111.dvz.dragon;

import java.util.UUID;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.disguise.DisguiseSystemHandler;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerDragon implements Dragon {
	private UUID player;
	
	private int dID;
	private int mana;
	
	public PlayerDragon(Player pl) {
		this.player = pl.getUniqueId();
	}

	@Override
	public Entity getEntity() {
		return Bukkit.getServer().getPlayer(player);
	}

	@Override
	public int getMana() {
		return mana;
	}

	@Override
	public void setMana(int manaNew) {
		this.mana = manaNew;
		if(this.mana<0) this.mana = 0;
		
		getPlayer().setLevel(this.mana);
	}

	@Override
	public int getID() {
		return dID;
	}

	@Override
	public void setID(int id) {
		dID = id;
	}

	@Override
	public void init() {
		Player player2 = getPlayer();
		DragonCustom dc = DvZ.dragonAtManager.getDragon(dID);
		if(dc != null) {
			
			DisguiseSystemHandler.disguiseP(player2,"EnderDragon");
			
			DvZ.dragonAtManager.addCastItems(player2, dID);
			setMana(dc.getMana());
			
			player2.setAllowFlight(true);
			player2.setFlying(true);
			player2.setFlySpeed(dc.getFlyingSpeed());
			player2.setMaxHealth(dc.getHealth());
			player2.setHealth(dc.getHealth());
			
			player2.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 190000, 0), true);
		}
	}

	private Player getPlayer() {
		return Bukkit.getServer().getPlayer(player);
	}

	public void playerRC(ItemStack item, Block block) {
		int itemID = item.getTypeId();
		
		int attackID = DvZ.dragonAtManager.getAttack(itemID);
		if(attackID != -1) {
			DvZ.dragonAtManager.castFromPlayerDragon(this, attackID);
		}
	}
}
