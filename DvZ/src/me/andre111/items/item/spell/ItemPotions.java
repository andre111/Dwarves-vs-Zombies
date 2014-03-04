package me.andre111.items.item.spell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.items.ItemHandler;
import me.andre111.items.SpellItems;
import me.andre111.items.item.ItemSpell;

public class ItemPotions extends ItemSpell {
	//0=dwarves, 1=monsters
	/*private int target = 0;
	private int radius = 2;
	private ItemStack itemS;*/
	
	@Override
	public Varargs invoke(Varargs args) {
		if(args.narg()>=4) {
			LuaValue playerN = args.arg(1);
			LuaValue targetN = args.arg(2);
			LuaValue radiusN = args.arg(3);
			LuaValue itemSN = args.arg(4);
			
			if(playerN.isstring() && targetN.isnumber() && radiusN.isnumber() && itemSN.isstring()) {
				Player player = Bukkit.getPlayerExact(playerN.toString());
				int target = targetN.toint();
				int radius = radiusN.toint();
				ItemStack itemS = ItemHandler.decodeItem(itemSN.toString(), player);
				
				if(player!=null) {
					Game game = DvZ.instance.getPlayerGame(player.getName());
					if(game==null) return RETURN_FALSE;
					
					if(castAtEntity(game, player, target, radius, itemS))
						return RETURN_TRUE;
				}
			}
		} else {
			SpellItems.log("Missing Argument for "+getClass().getCanonicalName());
		}
		
		return RETURN_FALSE;
	}
	
	private boolean castAtEntity(Game game, Entity ent, int target, int radius, ItemStack itemS) {
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
					if(target==0 && game.isDwarf(p.getName(), false)) players.add(p);
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
