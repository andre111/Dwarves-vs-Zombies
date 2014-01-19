package me.andre111.items.item.spell;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.andre111.dvz.manager.HighscoreManager;
import me.andre111.dvz.manager.PlayerScore;
import me.andre111.items.item.ItemSpell;

public class ItemDvZClassPoint extends ItemSpell {
	boolean self = true;
	int points = 0;
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) self = var==1;
		if(id==1) points = (int) Math.round(var);
	}
	
	@Override
	public boolean cast(Player player, Location loc, Player target, Block block) {
		Player pTarget = null;
		if(self) {
			pTarget = player;
		} else if(target!=null) {
			pTarget = target;
		}
		
		if(pTarget!=null) {
			PlayerScore pscore = HighscoreManager.getPlayerScore(pTarget.getName());
			pscore.setClasspoints(pscore.getClasspoints()+points);
		}
		
		return false;
	}
}
