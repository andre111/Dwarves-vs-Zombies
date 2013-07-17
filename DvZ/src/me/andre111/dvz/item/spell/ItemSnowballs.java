package me.andre111.dvz.item.spell;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.util.Vector;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.Spellcontroller;
import me.andre111.dvz.item.ItemSpell;
import me.andre111.dvz.utils.ItemHandler;

public class ItemSnowballs extends ItemSpell {
	private int needed = 96;
	private String needS = "You need 96 Snowballs!";
	private boolean isReset = true;
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) needed = (int) Math.round(var);
		else if(id==2) isReset = (var==1);
	}
	@Override
	public void setCastVar(int id, String var) {
		if(id==1) needS = var;
	}
	
	@Override
	public boolean cast(Game game, Player player) {	
		if(ItemHandler.countItems(player, 332, 0)>=needed) {
			ItemHandler.removeItems(player, 332, 0, needed);

			Random rand = new Random();
			Vector mod;
			for (int i = 0; i < 250; i++) {
				Snowball snowball = player.launchProjectile(Snowball.class);
				snowball.setFallDistance(Spellcontroller.identifier); // tag the snowballs
				mod = new Vector((rand.nextDouble() - .5) * 15 / 10.0, (rand.nextDouble() - .5) * 5 / 10.0, (rand.nextDouble() - .5) * 15 / 10.0);
				snowball.setVelocity(snowball.getVelocity().add(mod));
			}

			DvZ.updateInventory(player);
			
			return true;
		} else {
			player.sendMessage(needS);
			if(isReset) {
				resetCoolDown(game, player);
			}
			return false;
		}
	}
	
	@Override
	public boolean cast(Game game, Player player, Block block) {
		return cast(game, player);
	}
	@Override
	public boolean cast(Game game, Player player, Player target) {
		return cast(game, player);
	}
	@Override
	public boolean cast(Game game, Player player, Location loc) {
		return cast(game, player);
	}
}
