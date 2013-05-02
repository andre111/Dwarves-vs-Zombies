package me.andre111.dvz.item.spell;

import me.andre111.dvz.Game;
import me.andre111.dvz.item.ItemSpell;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ItemSmash extends ItemSpell {
	private boolean playSound = true;
	private boolean isReset = true;
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) playSound = (var==1);
		else if(id==1) isReset = (var==1);
	}

	@Override
	public boolean cast(Game game, Player player) {
		if(isReset) {
			resetCoolDown(game, player);
		}
		return false;
	}
	@Override
	public boolean cast(Game game, Player player, Player target) {
		if(isReset) {
			resetCoolDown(game, player);
		}
		return false;
	}
	@Override
	public boolean cast(Game game, Player player, Block block) {
		World w = block.getWorld();
		Location loc = block.getLocation();
		w.createExplosion(loc, 2);
		w.getBlockAt(loc).setTypeId(0);
		if(playSound)
			w.playSound(loc, Sound.IRONGOLEM_THROW, 1, 1);
		
		return true;
	}
	
	@Override
	public boolean cast(Game game, Player player, Location target) {
		World w = target.getWorld();

		w.createExplosion(target, 2);
		w.getBlockAt(target).setTypeId(0);
		if(playSound)
			w.playSound(target, Sound.IRONGOLEM_THROW, 1, 1);
		
		return true;
	}
}
