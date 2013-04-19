package me.andre111.dvz.monster.attack;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.andre111.dvz.Game;
import me.andre111.dvz.Spellcontroller;
import me.andre111.dvz.monster.MonsterAttack;

public class MonsterLeap extends MonsterAttack {
	private double forwardVelocity = 40 / 10D;
    private double upwardVelocity = 15 / 10D;

	@Override
	public void setCastVar(int id, double var) {
		if(id==0) forwardVelocity = var;
		else if(id==1) upwardVelocity = var;
	}

	@Override
	public void spellCast(Game game, Player player) {
		Vector v = player.getLocation().getDirection();
	    v.setY(0).normalize().multiply(forwardVelocity).setY(upwardVelocity);
	    player.setVelocity(v);
	    Spellcontroller.jumping.add(player);
	}
	
	@Override
	public int getType() {
		return 0;
	}
}
