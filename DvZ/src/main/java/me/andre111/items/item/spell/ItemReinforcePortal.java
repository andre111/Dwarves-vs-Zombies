package me.andre111.items.item.spell;

import java.util.ArrayList;
import java.util.UUID;

import me.andre111.dvz.DvZ;
import me.andre111.dvz.Game;
import me.andre111.dvz.config.ConfigManager;
import me.andre111.dvz.utils.PlayerHandler;
import me.andre111.items.SpellItems;
import me.andre111.items.item.ItemSpell;
import me.andre111.items.lua.LUAHelper;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class ItemReinforcePortal extends ItemSpell {
	@Override
	public Varargs invoke(Varargs args) {
		if(args.narg()>=2) {
			LuaValue playerN = LUAHelper.getInternalValue(args.arg(1));
			LuaValue locN = LUAHelper.getInternalValue(args.arg(2));
			
			if(playerN.isuserdata(UUID.class) && locN.isuserdata(Location.class)) {
				Player player = PlayerHandler.getPlayerFromUUID((UUID) playerN.touserdata(UUID.class));
				Location loc = (Location) locN.touserdata(Location.class);
				
				if(player!=null && loc!=null) {
					if(castIntern(player, loc))
						return RETURN_TRUE;
				}
			}
		} else {
			SpellItems.log("Missing Argument for "+getClass().getCanonicalName());
		}
		
		return RETURN_FALSE;
	}
	
	private boolean castIntern(Player player, Location loc) {
		Game game = DvZ.instance.getPlayerGame(player.getUniqueId());
		
		if(game.enderPortal!=null) {
			return reinforePortal(game, player);
		}
		
		return false;
	}
	
	private boolean reinforePortal(Game game, Player player) {
		//TODO - maybe add this lock to the original enderman in the LUA script, to be able to change it
		//if(player.getUniqueId().equals(game.enderMan)) {
			Location nloc = game.enderPortal;
			World w = nloc.getWorld();
			
			w.strikeLightningEffect(nloc);
			for(int i=0; i<10; i++) {
				ItemStack it = new ItemStack(Material.ENDER_STONE, 1);
				ItemMeta im = it.getItemMeta();
				im.setDisplayName(ConfigManager.getLanguage().getString("string_spell_disableportal","Disable Portal"));
				ArrayList<String> li = new ArrayList<String>();
				li.add(ConfigManager.getLanguage().getString("string_used_seconds","Can be used every -0- Seconds!").replace("-0-", ""+ConfigManager.getStaticConfig().getInt("spelltime_disableportal",3)));
				im.setLore(li);
				it.setItemMeta(im);
				
				w.dropItem(nloc, it);
			}
			
			player.teleport(nloc);
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 95000, -20));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 95000, 4));
			
			game.enderActive = true;
			
			game.broadcastMessage(ConfigManager.getLanguage().getString("string_portal_reinforce","The Portal has been reinforced!"));
			
			return true;
		//}
		//return false;
	}
}
