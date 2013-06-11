package me.andre111.dvz.item.spell;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;

import me.andre111.dvz.Game;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.item.ItemSpell;

public class ItemRoar extends ItemSpell {
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
	public boolean cast(Game game, Player player) {	
		return castAtEntity(game, player, player);
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
	public boolean cast(Game game, Player player, Location target) {
		Arrow a = (Arrow) target.getWorld().spawnEntity(target, EntityType.ARROW);
		boolean success = castAtEntity(game, a, player);
		a.remove();
		
		return success;
	}
	
	private boolean castAtEntity(Game game, Entity ent, Player damage) {
		boolean success = false;
		List<Entity> entities = ent.getNearbyEntities(range, range, range);
        for (Entity e : entities) {
        	if (e instanceof Silverfish) {
        		((Silverfish)e).damage(0, damage);
        		success = true;
        	}
        }
		
        if(!message.equals(""))
        	game.broadcastMessage(ConfigManager.getLanguage().getString("string_brood_roar","A Broodmother roars!"));
        
        return success;
	}
}
