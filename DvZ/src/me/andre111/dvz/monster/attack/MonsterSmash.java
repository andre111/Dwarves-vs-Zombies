package me.andre111.dvz.monster.attack;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.andre111.dvz.Game;
import me.andre111.dvz.monster.MonsterAttack;

public class MonsterSmash extends MonsterAttack {
	private boolean playSound = true;
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==1) playSound = (var==1);
	}

	@Override
	public void spellCast(Game game, Player player, Block block) {
		World w = block.getWorld();
		Location loc = block.getLocation();
		w.createExplosion(loc, 2);
		w.getBlockAt(loc).setTypeId(0);
		if(playSound)
			w.playSound(loc, Sound.IRONGOLEM_THROW, 1, 1);
	}
	
	@Override
	public void spellCastOnLocation(Game game, Player player, Location target) {
		World w = target.getWorld();

		w.createExplosion(target, 2);
		w.getBlockAt(target).setTypeId(0);
		if(playSound)
			w.playSound(target, Sound.IRONGOLEM_THROW, 1, 1);
	}
	
	@Override
	public int getType() {
		return 1;
	}
}
