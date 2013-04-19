package me.andre111.dvz.monster.attack;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import me.andre111.dvz.Game;
import me.andre111.dvz.monster.MonsterAttack;

public class MonsterExplode extends MonsterAttack {
	private float power = 6F;
	private boolean kill = true;
	
	public void setCastVar(int id, double var) {
		if(id==0) power = (float) var;
		else if(id==1) kill = (var==1);
	}
	
	
	public void spellCast(Game game, Player player) {	
		World w = player.getWorld();
		Location loc = player.getLocation();
		
		if(kill) {
			player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
			player.damage(10000);
		}
		
		w.createExplosion(loc, power);
		w.createExplosion(loc, power);
	}
	
	@Override
	public void spellCastOnLocation(Game game, Player player, Location target) {
		World w = target.getWorld();
		
		if(kill) {
			player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
			player.damage(10000);
		}
		
		w.createExplosion(target, power);
		w.createExplosion(target, power);
	}
	
	@Override
	public int getType() {
		return 0;
	}
}
