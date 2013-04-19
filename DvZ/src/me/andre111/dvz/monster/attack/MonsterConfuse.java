package me.andre111.dvz.monster.attack;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.andre111.dvz.Game;
import me.andre111.dvz.monster.MonsterAttack;

public class MonsterConfuse extends MonsterAttack {
	private int duration = 300;
	private int level = 0;

	@Override
	public void setCastVar(int id, double var) {
		if(id==0) duration = (int) Math.round(var);
		else if(id==1) level = (int) Math.round(var);
	}
	
	@Override
	public void spellCast(Game game, Player player, Player target) {
		target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, duration, level), true);
		target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, level), true);
	}
	
	@Override
	public int getType() {
		return 2;
	}
}
