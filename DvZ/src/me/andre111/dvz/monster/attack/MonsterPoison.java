package me.andre111.dvz.monster.attack;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.andre111.dvz.Game;
import me.andre111.dvz.monster.MonsterAttack;

public class MonsterPoison extends MonsterAttack {
	private int duration = 60;
	private int level = 4;

	@Override
	public void setCastVar(int id, double var) {
		if(id==0) duration = (int) Math.round(var);
		else if(id==1) level = (int) Math.round(var);
	}
	
	@Override
	public void spellCast(Game game, Player player, Player target) {
		target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, level), true);
	}
	
	@Override
	public int getType() {
		return 2;
	}
}
