package me.andre111.dvz.monster.attack;

import org.bukkit.entity.Player;

import me.andre111.dvz.Game;
import me.andre111.dvz.monster.MonsterAttack;

public class MonsterHunger extends MonsterAttack {
	private int ammount = 2;
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) ammount = (int) Math.round(var);
	}

	@Override
	public void spellCast(Game game, Player player, Player target) {
		int newfood = target.getFoodLevel()-ammount;
		if(newfood<0) newfood = 0;
		
		target.setFoodLevel(newfood);
		
		//über 50 - alles entfernen
		if(ammount>50) {
			target.setFoodLevel(0);
			target.setSaturation(0);
		}
	}
	
	@Override
	public int getType() {
		return 2;
	}
}
