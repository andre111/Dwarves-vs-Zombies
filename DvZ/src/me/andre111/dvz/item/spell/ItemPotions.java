package me.andre111.dvz.item.spell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import me.andre111.dvz.Game;
import me.andre111.dvz.item.ItemSpell;
import me.andre111.dvz.utils.ItemHandler;

public class ItemPotions extends ItemSpell {
	//0=dwarves, 1=monsters
	private int target = 0;
	private int radius = 2;
	private ItemStack itemS;


	@Override
	public void setCastVar(int id, String var) {
		if(id==0) itemS = ItemHandler.decodeItem(var);
	}

	@Override
	public void setCastVar(int id, double var) {
		if(id==1) target = (int) Math.round(var);
		else if(id==2) radius = (int) Math.round(var);
	}

	@Override
	public boolean cast(Game game, Player player) {	
		return castAtEntity(game, player);
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
		boolean success = castAtEntity(game, a);
		a.remove();
		
		return success;
	}

	private boolean castAtEntity(Game game, Entity ent) {
		if(itemS==null) return false;

		//get potioneffect from ItemStack(by spawning an entity and then removing it)
		Collection<PotionEffect> effects = new ArrayList<PotionEffect>();
		if(itemS.getItemMeta() instanceof PotionMeta) {
			effects = ((PotionMeta) itemS.getItemMeta()).getCustomEffects();
		}

		//get fitting players
		List<Entity> entities = ent.getNearbyEntities(radius, radius, radius);
		List<Player> players = new ArrayList<Player>();
		for(Entity e : entities) {
			if(e instanceof Player) {
				Player p = (Player) e;
				if(game.isPlayer(p.getName())) {
					if(target==0 && game.isDwarf(p.getName())) players.add(p);
					else if(target==1 && game.isMonster(p.getName())) players.add(p);
				}
			}
		}
		//add potioneffect
		for(Player p : players) {
			p.getWorld().playEffect(p.getLocation(), Effect.POTION_BREAK, itemS.getDurability());

			p.addPotionEffects(effects);
		}
		
		if(players.size()>0)
			return true;
		else
			return false;
	}
}
