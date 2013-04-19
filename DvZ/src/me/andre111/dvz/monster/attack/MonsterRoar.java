package me.andre111.dvz.monster.attack;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.monster.MonsterAttack;

public class MonsterRoar extends MonsterAttack {
	private double range;
	private String message = "";
	
	@Override
	public void setCastVar(int id, double var) {
		if(id==0) range = var;
	}
	
	@Override
	public void setCastVar(int id, String var) {
		if(id==1) message = var;
	}
	
	@Override
	public void spellCast(Game game, Player player) {	
		castAtEntity(game, player, player);
	}
	
	@Override
	public void spellCastOnLocation(Game game, Player player, Location target) {
		Arrow a = (Arrow) target.getWorld().spawnEntity(target, EntityType.ARROW);
		castAtEntity(game, a, player);
		a.remove();
	}
	
	private void castAtEntity(Game game, Entity ent, Player damage) {
		List<Entity> entities = ent.getNearbyEntities(range, range, range);
        for (Entity e : entities) {
        	if (e instanceof Silverfish) {
        		((Silverfish)e).damage(0, damage);
        	}
        }
		
        if(!message.equals(""))
        	game.broadcastMessage(DvZ.getLanguage().getString("string_brood_roar","A Broodmother roars!"));
	}
	
	@Override
	public int getType() {
		return 0;
	}
}
