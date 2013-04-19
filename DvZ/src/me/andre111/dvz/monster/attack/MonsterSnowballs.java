package me.andre111.dvz.monster.attack;

import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.util.Vector;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.Spellcontroller;
import me.andre111.dvz.monster.MonsterAttack;

public class MonsterSnowballs extends MonsterAttack {
	private int needed = 96;
	private String needS = "You need 96 Snowballs!";
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) needed = (int) Math.round(var);
	}
	@Override
	public void setCastVar(int id, String var) {
		if(id==1) needS = var;
	}
	
	@Override
	public void spellCast(Game game, Player player) {	
		if(Spellcontroller.countItems(player, 332, 0)>=needed) {
			Spellcontroller.removeItems(player, 332, 0, needed);

			Random rand = new Random();
			Vector mod;
			for (int i = 0; i < 250; i++) {
				Snowball snowball = player.launchProjectile(Snowball.class);
				snowball.setFallDistance(Spellcontroller.identifier); // tag the snowballs
				mod = new Vector((rand.nextDouble() - .5) * 15 / 10.0, (rand.nextDouble() - .5) * 5 / 10.0, (rand.nextDouble() - .5) * 15 / 10.0);
				snowball.setVelocity(snowball.getVelocity().add(mod));
			}

			DvZ.updateInventory(player);
		} else {
			player.sendMessage(needS);
			game.setCountdown(player.getName(), getId(), 0); //reset cooldown
		}
	}
	
	@Override
	public int getType() {
		return 0;
	}
}
