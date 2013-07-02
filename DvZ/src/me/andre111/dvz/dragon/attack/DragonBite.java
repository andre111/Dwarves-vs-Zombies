package me.andre111.dvz.dragon.attack;

import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.andre111.dvz.dragon.DragonAttack;

public class DragonBite extends DragonAttack {
	private double damage = 3;
	private double chance = 15;
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) damage = var;
		else if(id==1) chance = var;
	}
	
	@Override
	public void castOnPlayer(Player player) {
		player.damage(damage);
		
		Random rand = new Random();
		
		if(rand.nextDouble()*100<chance) {
			player.setFireTicks(20*5);
		}
		if(rand.nextDouble()*100<chance) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*15, 0));
		}
		if(rand.nextDouble()*100<chance) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*15, 0));
		}
		if(rand.nextDouble()*100<chance) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*15, 0));
		}
	}
}
